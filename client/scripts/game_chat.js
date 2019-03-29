chat = {
  $d : $(),
  $form : $(),
  $formText : $(),
  init : function(){
    var $d = $("#display .gamechat");
    console.log("Initialisiere Chat")
        chat.$d = $d;
        chat.$form = $("form:first",$d);
        chat.$formText = $("input:first",chat.$form);
        chat.$form.submit(function(){
          if(this.msg.value.length > 0){
        console.log("Chatnachricht: "+this.msg.value);
        user.sendChatMessage(this.msg.value);
        this.msg.value="";
        }
        chat.hide();
        return false;
        });
        chat.$formText
        .focus(function(){
          user.setOption("kb",false);
          })
        .focusout(function(){
          chat.hide();
        });

        /*
        .autocomplete({
            source: function(request, response) {
            var filteredArray = $.map(["/invite "] , function(item) {
        if( item.startsWith(request.term)){
            return item;
        }
        else{
            return null;
        }
    });
    response(filteredArray);
}
        });
        */


  },
  show : function(){
    this.$d.show();
    this.$formText.focus();
  },
  hide : function(){
    this.$d.hide();
    user.setOption("kb",true);
  }


};