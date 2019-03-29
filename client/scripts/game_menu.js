menu = {
  $d : null,
  init: function(){
    console.log("Initialisiere Menü");
    menu.$d = $("#menu");
      menu.$d.dialog({
          autoOpen : false,
          closeOnEscape: true,
          modal: true,
          resizable: false,
          draggable: false,
          buttons: {
          "Spiel verlassen": function() {
                   $( this ).dialog( "close" );
                   server.send({type:4,stype:1});
              }
            },
          open: function(){
            user.setOption("menu",false);
            user.modalmsg++;
          },
          close: function(){
            setTimeout("user.setOption('menu',true)",500);
            user.modalmsg--;
          }
          });
  },
  show : function(){
    menu.$d.dialog("open");
  }
}