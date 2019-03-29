auth = {
  init : function(){
    this.account.loginInit();
    this.account.registrationInit();
  },
  account : {
    loginInit: function(){
      $("#loginDialog").dialog({
        width: 300,
        resizable: false,
        autoOpen: false,
        draggable: false,
        open: function(event, ui) { $(".ui-dialog-titlebar-close", $(this).parent()).hide(); },

        show: {
          effect: "slide",
          direction: "right",
          duration: 500
        },
        hide: {
          effect: "slide",
          direction: "right",
          duration: 500
        }
      });
      var $loginForm = $("#loginDialog form");
      $loginForm.submit(function(){
        if(this.name.value.length == 0 || this.pw.value.length == 0)
         $('#loginDialog_nc').html('Bitte geben sie Ihre Zugangsdaten ein').finish().show('slow').delay(3000).hide('slow')
         else
         user.login(this.name.value,this.pw.value);
         return false;
      });
      $("input[type=submit]",$loginForm).button();
      $("a",$loginForm).click(function(){
      $("#loginDialog").dialog("close");
      $("#registrationDialog").dialog("open");
      });
    },
    registrationInit: function(){
        var $regForm = $("#registrationDialog form");

        $regForm.data("validation",function(){
          var form = $regForm[0];
            return !$(form.name).data("exists") && isValidName(form.name.value) && form.name.value.length >=3 && form.pw.value.length >= 6 && form.pw.value == form.pw2.value && isValidEmail(form.email.value) && !$(form.name).data("exists");
          });

          var $submitbtn = $("input[type=submit]",$regForm).button({disabled: true});

          $regForm.submit(function(){
          if($(this).data("validation")())
              server.send({type:3, stype:2, n : this.name.value, pw : this.pw.value, email: this.email.value});
          return false;
        });

        var namefieldFunction = function(){
        var f = function(field){
          return function(){
            if(isValidName(field.value) && field.value.length >= 3 && field.value.length <= 16)
            server.send({type:3, stype:1, name : field.value});
            else
            $("div[data-subject=name]",$regForm).animate({"backgroundPositionX" : -60},300);
            }
            }(this)

            clearTimeout($(this).data("timer"));
            $(this).data("timer",setTimeout(f,500));
        };

        $("input[name=name]",$regForm).on("keyup focusout",namefieldFunction)
        .focus(function(){
        $("div[data-subject=name]",$regForm).animate({"backgroundPositionX" : -30},300);
        })
        .tooltip({
          content : function(){
            var s = "Der Name muss zwischen 3-16 Zeichen lang sein und darf aus Buchstaben, Zahlen, Binde- und Unterstrichen bestehen.";
            if($(this).data("exists"))
                s += "<hr>Der zuletzt gepr&uuml;fte Accountname ist bereits vergeben!";
            return s;

            },
          items : "input"
        });


        $("input[name=pw]",$regForm).focusout(function(){
        if(this.value.length < 6)
        $("div[data-subject=pw]",$regForm).animate({"backgroundPositionX" : -60},300);
        else
        $("div[data-subject=pw]",$regForm).animate({"backgroundPositionX" : 0},300);
        $submitbtn.button("option","disabled",!$($regForm).data("validation")());
        }).tooltip({
          content : function(){ return "Das Passwort muss aus mindestens 6 Zeichen bestehen."; },
          items : "input"
        })
        .focus(function(){
        $("div[data-subject=pw]",$regForm).animate({"backgroundPositionX" : -30},300);
        });
        $("input[name=pw2]",$regForm).focusout(function(){
        if(this.value.length < 6 || this.value != $("input[name=pw]",$regForm).val())
        $("div[data-subject=pw2]",$regForm).animate({"backgroundPositionX" : -60},300);
        else
        $("div[data-subject=pw2]",$regForm).animate({"backgroundPositionX" : 0},300);
        $submitbtn.button("option","disabled",!$($regForm).data("validation")());
        })
        .focus(function(){
        $("div[data-subject=pw2]",$regForm).animate({"backgroundPositionX" : -30},300);
        })
        .tooltip({
          content : function(){ return "Um sicher zu gehen, dass du dich nicht vertippt hast, gebe hier erneut das Passwort ein."; },
          items : "input"
        });
        $("input[name=email]",$regForm).focusout(function(){
        if(!isValidEmail(this.value))
        $("div[data-subject=email]",$regForm).animate({"backgroundPositionX" : -60},300);
        else
        $("div[data-subject=email]",$regForm).animate({"backgroundPositionX" : 0},300);

        $submitbtn.button("option","disabled",!$($regForm).data("validation")());
        })
        .focus(function(){
        $("div[data-subject=email]",$regForm).animate({"backgroundPositionX" : -30},300);
        })
        .tooltip({
          content : function(){ return "Gebe hier eine g&uuml;tige<br>E-Mailadresse ein."; },
          items : "input"
        });

        $("div[data-subject=succeed]").hide();
        $("div[data-subject=nosucceed]").hide();


        $("#registrationDialog").dialog({
              autoOpen: false,
              resizable: false,
              draggable: false,
              show: {
                effect: "slide",
                direction: "left",
                duration: 500
              },
              hide: {
                effect: "slide",
                direction: "left",
                duration: 500
              },
            beforeClose: function () {
              if(server.connected)
                $("#loginDialog").dialog("open");
            }

            });
     }
    }
}