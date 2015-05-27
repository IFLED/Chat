(function($, undefined) {
    var $error;

    $(function(){
        $error = $("#error");
    })

    var onClick = function() {
        $(function() {

            $.ajax({
                url: "First",
                method: "POST",
                data: {username: $("#name").val(),
                       password: $("#password").val()},
                dataType: "html",
                success: function(data) {
                    $("#label").val("");
                    ans = parseInt(data)
                    if (ans > 0) {

                        window.location.href ="./";
                        localStorage.clear();
                        localStorage.setItem("username", $("#name").val());
                        localStorage.setItem("session_id", ans);
                    }
                    else if (ans == -2) {
                        $error.html("Invalid name/password");
                    }
                    else {
                        $error.html("else error");
                    }
                }
            })
        })
    }

    $(function() {
        $("#button").click(onClick);
    })
})(jQuery);