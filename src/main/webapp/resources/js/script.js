//ALL UI LOGIC
//ALL DAY


// ----------- VARS

var chatView;  
var messageList = [];

var usernames = {};

var editMode = false;
var settingsMode = false;

var actionID = 0;

var currentUserName;

var session_id;

// Struct for adding message to UI
var newMessage = function (time,user_id,messageID,messageText){
    return {
        time: time,
        user_id: user_id,
        messageID: messageID,
        messageText: messageText,
        
    };
};

// Struct for sending message to server
var newMessageSendRequest = function (session_id, username,room_id, messageText){
    return {
        session_id: session_id,
        message: JSON.stringify( {
            username : username,
            room_id: String(room_id),
            text: messageText,
            status: "new",
            
        })
        
    };
};
// Struct for deleting message from server
var deleteMessageRequest = function (session_id, message_id){
    return {
        session_id: session_id,
        message: JSON.stringify( {
            message_id: String(message_id),
            status: "delete",
        })
        
    };
};

var editMessageRequest = function (session_id, message_id, newMessage){
    return {
        session_id: session_id,
        message: JSON.stringify( {
            text: newMessage,
            message_id: String(message_id),
            status: "edit",
        })
        
    };
};

var changeNameRequest = function(session_id, old_username, new_username) {
    return {
        session_id: session_id,
        message: JSON.stringify( {
                old_username: old_username,
                new_username: new_username,
                status: "change name"
            })
    }
}

// Struct for geting message from server
var newGetRequest = function(session_id, action_id, username){
    return{
        session_id: session_id,
        action_id: action_id,
        username: username,    
    }
    
}

// ----------- Main Function

$( document ).ready(function(){

    handleChatSize();
    $( window ).resize(handleChatSize);
    
    chatView = $('.panel-body');
    run();
    
    
    scrollToBottom(chatView,false);
    chatView.css("opacity","100");
    
    $("#MessageForm").focus();
    
    
    //NewMessageAdd
    $("#SendBtn").click(userMessageSended);
    registerKeyPress(editMode);

//Additional setup
    $("#EditBtn").click(editMessageEditMode);
    $("#CancelEditBtn").click(cancelEditMode);
    
    
//SettingsListeners    
    $('.SettingsButton').on('click', toggleSettings);
    $("#NameSettingsApply").on('click', applySettings);
    $("#CancelSettingsBtn").on('click', toggleSettings);
    
    
    //console.log(currentUserName);
	   


});

// ----------- LOGIC. Chat UI updates. Server Requests

function run() { // start of app. data restore.
    
    var data = restore();
    createAllMessages(data);
    
    startSession(currentUserName); // post func - get session
    
    setTimeout(function(){ // Get Requset
		//console.log(actionID);
        getMessages();
    }, 1000)
}

function getMessages() {
    setTimeout(function(){
        var getRequest = newGetRequest(session_id, actionID, currentUserName)
        getAction(getRequest); // getNewActions
    }, 1000);
}

function createAllMessages(data) {
    if (data != null) {
	for(var i = 0; i < data.length; i++)
		addMessageFromData(data[i]);
    };    
}

function restore() {
    var userNameopt = $.cookie("username");
    if (userNameopt != null){
        currentUserName = userNameopt;
        $("#NameForm").val(currentUserName);
    } else {
        console.log("username does not exits");
        //window.location.href = "/homepage.htm";
    }

    session_id = $.cookie("session_id");
    if (session_id == null) {
        console.log("session_id does not exist");
        //window.location.href = "homepage.htm";
    }

	return null;
}

function store(){
    $.cookie("username", currentUserName);
    $.cookie("session_id", session_id);
}

function invalidateSession() {
    $.removeCookie("username");
    $.removeCookie("session_id");
}

function addMessageFromData(message){
    addNewMessage(message);
    
}

function deleteFromList(messageID){
    for (var i = 0 ; i < messageList.length ; i++){
        if (messageList[i].messageID == messageID){
            messageList.splice(i,1);
            store();
            continue;
        }
    }

}

function editInList(messageID,newText){
    for (var i = 0 ; i < messageList.length ; i++){
        if (messageList[i].messageID == messageID){
            messageList[i].messageText = newText;
            store();
            continue;
        }
    }

}



// Test Func. It woorks.
function postNewMessage(data){ 
    
    //console.log(JSON.stringify(message1));
    
    $.ajax({
    method: "POST",
    //url: "http://10.160.6.234:8080/Chat/Update",
	url: "Update",
    data: data,    
    success: function(data){
        if(data == 0){
            console.log('post message succes');
            return true;
        }
        else {
            console.log('post error in success');
            return false;
        }
    },
    error: function(data){
        console.log('post error - error');
        return false;
    }    
    });
}

function postDeleteMessage(data){ 
    
    //console.log(JSON.stringify(message1));
    
    $.ajax({
    method: "POST",
    //url: "http://10.160.6.234:8080/Chat/Update",
	url: "Update",
    data: data,    
    success: function(data){
        if(data == 0){
        console.log('del message succes');
    } else {
        console.log('del error in success');
    }
    },
    error: function(data){
        console.log('del error - error');
    }    
    });
}


function postEditMessage(data){ 
    
    //console.log(JSON.stringify(message1));
    
    $.ajax({
    method: "POST",
    //url: "http://10.160.6.234:8080/Chat/Update",
	url: "Update",
    data: data,    
    success: function(data){
        if(data == 0){
        console.log('edit message succes');
    } else {
        console.log('edit error in success');
    }
    },
    error: function(data){
        console.log('edit error - error');
    }    
    });
}

function postChangeName(data) {
    var ans = undefined;
    $.ajax({
        method: "POST",
        url: "Update",
        data: data,
        async: false,
        success: function(data){
            if(data >= 0){
                console.log('change name message succes');
                ans = true;
            } else {
                console.log('change name error in success');
                ans = false;
            }
        },
        error: function(data){
            console.log('change name error - error');
            ans = false;
        }
    });
    return ans;
}


function getAction(getRequest){ 
    
    //console.log(JSON.stringify(message1));
    
    $.ajax({
        method: "GET",
        //url: "http://10.160.6.234:8080/Chat/Get",
        url: "Get",
        data: getRequest,
        dataType: "html",
        success: function(data){
            //console.log("getActionSucces" + data);


            getActionFromServerWithJSON(data);

            getMessages();
        },
        error: function(data){
                console.log("getAction: getError");
        }
    });
}


function startSession(username){
}

function getActionFromServerWithJSON(jsonData) {
    if (jsonData.length == 0)
        return;

    var dataFromServer = $.parseJSON(jsonData);

    newUsernames = dataFromServer["usernames"];
    if (newUsernames != undefined) {
        for (i = 0; i < newUsernames.length; ++i) {
            user_id = newUsernames[i].user_id;
            name = newUsernames[i].name;
            if (usernames[user_id] != name) {
                usernames[user_id] = name;
                setName(user_id, name);
            }
        }
    }

    var newMessages = dataFromServer["messages"];

    for (i = 0; i < newMessages.length; i++) {

        var messageFromServer = newMessages[i];

        newActionId = parseInt(messageFromServer.action_id);
        actionID = Math.max(actionID, newActionId);

        var status = messageFromServer.status;
        //console.log(status);
        if (status == 1) { // NEW
            var message = newMessage(messageFromServer.time,
                messageFromServer.user_id,
                messageFromServer.message_id,
                messageFromServer.text);
            addNewMessage(message);
        }
        else if (status == 3) { // DELETE
            var messageIDToDelete = messageFromServer.message_id;

            deleteMessageByID(messageIDToDelete);
        }
        else if (status == 2) {// EDIT
            var messageIDToEdit = messageFromServer.message_id;
            var editMessageText = messageFromServer.text;

            editMessageByIDandNewText(messageIDToEdit, editMessageText);
        }
        else if (status == 4) { // change name
            console.log("status = 4");
        }
        else {
            console.log("some error in getting messages" + status);
        }
    }
}
    

// UI
function addNewMessage(message){

    var messBlock = $(".media-list > .media:first").clone();
    messBlock.find(".message").text(message.messageText);
    messBlock.find(".NickName").text(usernames[message.user_id]);
    messBlock.attr("user_id", [message.user_id]);
    messBlock.find(".Time").text(message.time);
    messBlock.attr("data-messageID",[message.messageID]);
     
    messBlock.removeClass("hidden"); 
    
    if(currentUserName == usernames[message.user_id]) { // SameUser
        messBlock.find(".deleteMessage").click(userMessageDelete);
        messBlock.find(".editMessage").click(userMessageEdit);
    }
    else { // Can't delete and edit this message
        messBlock.find(".deleteMessage").remove();
        messBlock.find(".editMessage").remove();
    }
    
    
    messBlock.appendTo(".media-list");

    messageList.push(message);
    store();
    
	scrollToBottom(chatView,false);
};

function setName(user_id, name) {
    $("[user_id = '" + user_id + "']").find(".NickName").text(name);
}




var userMessageSended = function(){ // new message sended by user
    
    var messageForm = $("#MessageForm");

    var prepareNewMessageForSend = newMessageSendRequest(session_id,
                                                        currentUserName,
                                                        1,
                                                        messageForm.val());
    
    if(!isMessageValid(prepareNewMessageForSend.message)) {
        return ;
    }

    //checkServerStatus();

   // postAction(action);
    //addNewMessage(message);
    
    if ( postNewMessage(prepareNewMessageForSend) ) {
        messageForm.val("");
    }
    
    scrollToBottom(chatView,true);
    
    messageFormRefresh();

};

function deleteMessageByID(messageID){

    var messBlock = $(".media-list > .media[data-messageID="+messageID+"]");
    //console.log(messBlock.text());
    messBlock.remove();
    deleteFromList(messageID);
    store();
    
}

function editMessageByIDandNewText(messageID, messageText){
    //console.log("new text = " + messageText);
    var messBlock = $(".media-list > .media[data-messageID="+messageID+"]");
    var textUI = messBlock.find(".message");
    textUI.text(messageText);


    editInList(messageID ,messageText);
    store();
}

var userMessageDelete = function(evt){
    
    if (editMode)
        return;
    
    var sure = confirm("Delete Message?"); // TODO: - Nice console.log view. And Animation
    if (!sure) {
        return false;
    }
    
    var messageToDelete = $(this).parents("li.media");
    var messageIDtoDelete = parseInt(messageToDelete.attr("data-messageID"));
    //deleteFromList(messageIDtoDelete);
    //messageToDelete.remove(); //TODO : - Awesome animation ASAP
    console.log('delete ' + messageIDtoDelete);
    
    var messageToDeleteRequestData = deleteMessageRequest(session_id,messageIDtoDelete);
    postDeleteMessage(messageToDeleteRequestData);
    
};


var userMessageEdit = function(evt){ // ENTER EDIT MODE
    
    if (editMode)
        return;
    
    var messageToEdit = $(this).parents("li.media");
    messageToEditGlobal = messageToEdit.find(".message");
    
    UIToggleEditMode();
    

}; 

var messageToEditGlobal;

var editMessageEditMode = function(){ // EDIT MESSAGE 
    if (!editMode)
        return;
    
    var text = $("#MessageForm").val();
    if (!isMessageValid(text))
        return;
    
   
    
    
    messageToEditGlobal.text("Editing...");
    var messageIDtoEdit = parseInt(messageToEditGlobal.parents("li.media").attr("data-messageID"));
    
    var editRequest = editMessageRequest(session_id, messageIDtoEdit, text);
    postEditMessage(editRequest);
    
    
    
    
    cancelEditMode();
}

var cancelEditMode = function(){
    if (!editMode)
        return;
    UIToggleEditMode();
}


function UIToggleEditMode(){
    if (editMode){
        editMode = false;
        messageFormRefresh();
        messageToEditGlobal.removeClass("editModeEnabled");
        $("#SendBtn").show();
        $("#EditBtn").addClass("hidden");
        $("#CancelEditBtn").addClass("hidden");
        registerKeyPress();
        
    } else {
        editMode = true;
        registerKeyPress(editMode);
        messageToEditGlobal.addClass("editModeEnabled");
        var textToEdit = messageToEditGlobal.text();
        $("#MessageForm").val(textToEdit);
        $("#MessageForm").focus();
        $("#SendBtn").hide();
        $("#EditBtn").removeClass("hidden");
        $("#CancelEditBtn").removeClass("hidden");
        
    }
    
}







// Does not work. Need to teach server to make callbacks.
var serverURL = "";
function checkServerStatus(){ // Rethink
    $.ajax({url: serverURL,
        type: "HEAD",
        timeout:1000,
        statusCode: {
            200: function (response) {
                console.log('Working!');
            },
            400: function (response) {
                console.log('Not working!');
            },
            0: function (response) {
                console.log('Not working!');
            }              
        }
 });
}



// ----------- UI details you do not need to carry about. 
// Really. Don't

 var handleChatSize = function handleChatSize(){
        
    
    $('.panel-body').height($('body').height() - $('.panel-footer').height() - 40);
    
    

 };


function scrollToBottom(view,animate){
    if(animate){
        view.animate({"scrollTop": $('.panel-body')[0].scrollHeight}, "slow");
    }
    else {
        view.animate({"scrollTop": $('.panel-body')[0].scrollHeight}, 0);
  
    }     
}  


var registerKeyPress = function(editMode){
    $(document).off("keypress");
    
    $(document).keypress(function(e) {
    if(e.which == 13 && !e.shiftKey) {
        if (editMode){
           editMessageEditMode();    
        } else {
           userMessageSended(); 
        }
        
        }
        
        
        
        
        
    });
}

var registerForNameCheck = function(){
    if(settingsMode){
    $(document).keyup(function(e){
            
    var Newname = $("#NameForm").val();
    if(isMessageValid(Newname)){
        
        $("#NameSettingsApply").removeClass("disabled");
        
    } else {
        
        $("#NameSettingsApply").addClass("disabled");
        
    }
        
            
            
        
      
    })
    } else {
        
       $(document).off("keyup"); 
        
    }
}


function messageFormRefresh(){
    $("#MessageForm").val("");
    $("#MessageForm").blur();
    setTimeout(function(){
        $("#MessageForm").focus();
    }, 50);
    
    
    
};
  
var settingsHeight = "-120px"

var toggleSettings = function(){
    if (settingsMode){
        
        settingsMode = false;
         $(".settings").css("z-index", "-1");   $("#SettingsButtonGlyph").removeClass("coloredGlyph");
        $( "#mainRow" ).transition({ y: '0px' });
        $('.settings').transition({ opacity: 0});
        $(".settings").css("box-shadow", "none");
        $(".settings").css("z-index", "-1");

    } else {
        
        settingsMode = true;
        $(".settings").css("bottom","120px");
        $(".settings").css("box-shadow", "0px 2px 3px #888888"); $("#SettingsButtonGlyph").addClass("coloredGlyph");
        $( "#mainRow" ).transition({ y: settingsHeight,
                                   });
        
        $('.settings').transition({ opacity: 100 }, function(){
            $(".settings").css("z-index", "0");
            
        });


    }
    
    registerForNameCheck();

}; 


var applySettings = function() {

    var newName = $("#NameForm").val();
    var dataToSend = changeNameRequest(session_id, currentUserName, newName);
    var result = postChangeName(dataToSend);
    if (result) {
        console.log("name changed!");
        currentUserName = newName;
        store();

        toggleSettings();
    }
}

    var isMessageValid = function(messageText){
        
        
        if (messageText.length == 0 )
            return false;
        
        for (var i = 0, ch; i < messageText.length; i++ ){
            ch = messageText.charCodeAt(i);
            if(ch != 32 && ch != 10){
            return true;
            }
        }
        return false;
    }

