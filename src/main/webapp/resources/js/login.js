(function($, undefined) {
    var $error;

    $(function(){
        $error = $("#error");
    })

    var onClick = function() {
        $(function() {
            var pass = hash($("#password").val()).toString();
            $.ajax({
                url: "Login",
                method: "POST",
                data: {username: $("#name").val(),
                       password: pass},
                dataType: "html",
                success: function(data) {
                    $("#label").val("");
                    ans = parseInt(data)
                    if (ans > 0) {
                        window.location.href ="./";
                        $.cookie('username', $("#name").val());
                        $.cookie('session_id', ans);
                    }
                    else if (ans == -2) {
                        $error.html("Invalid name/password");
                        $.removeCookie('username');
                        $.removeCookie('session_id');
                    }
                    else {
                        $error.html("else error");
                        $.removeCookie('username');
                        $.removeCookie('session_id');
                    }
                }
            })
        })
    }

    $(function() {
        $("#button").click(onClick);
    })
})(jQuery);