keyboard = { // W=87 A=65 S=83 D=68 Enter=13
keys : {
  17 : 0, // Feuer
  87 : 1, // Hoch
  83 : 2, // Runter
  65 : 3, // Links
  68 : 4, // Rechts
  27 : 10, // Escape Menü
  38 : 1,
  40 : 2,
  37 : 3,
  39 : 4,
  69 : 5, // Benutzen
  82 : 6 // R - Reload
},
realKeys : {
  0 : {hold : false},
  1 : {hold : false},
  2 : {hold : false},
  3 : {hold : false},
  4 : {hold : false},
  5 : {hold : false},
  6 : {hold : false},
  10 : {hold : false}
},
init : function(){
  if(!isMobileDevice){ // PC - Plugged Keyboard

     $(document).on("keydown",this.down);
     $(document).on("keyup",this.up);
     $(document).on("keypress",this.press);

  } else { // Mobile Device - Touchcontrol

    var $ac = $("#actioncontrol");
    var $mc = $("#movecontrol");

    $ac.add($mc).css("transform","scale("+clientConfiguration['keyScale']+")");


    $("div[data-subject=use]",$ac).on("touchstart",function(){
      keyboard.handlerDown(5);
      $(this).css("background-position","100% 0");
    })
    .on("touchend",function(){
      keyboard.handlerUp(5);
      $(this).css("background-position","0 0");
    });

    $("div[data-subject=reload]",$ac).on("touchstart",function(){
      keyboard.handlerDown(6);
      $(this).css("background-position","100% 0");
    })
    .on("touchend",function(){
      keyboard.handlerUp(6);
      $(this).css("background-position","0 0");
    });



    $mc.data("dirX",0).data("dirY",0);
    $mc.data("setDir",function(dirX,dirY){
        var olddirX = $mc.data("dirX");
        var olddirY = $mc.data("dirY");

        if(dirX != olddirX){
            if(olddirX != 0){
                keyboard.handlerUp((olddirX<0) ? 3 : 4);
                $("div[data-subject=x"+olddirX+"]",$mc).css("background-position","0 0");
                }
            if(dirX != 0){
               keyboard.handlerDown((dirX<0) ? 3 : 4);
               $("div[data-subject=x"+dirX+"]",$mc).css("background-position","100% 0");
            }
             $mc.data("dirX",dirX);
        }
        if(dirY != olddirY){
            if(olddirY != 0){
                keyboard.handlerUp((olddirY<0) ? 1 : 2);
                $("div[data-subject=y"+olddirY+"]",$mc).css("background-position","0 0");
            }
            if(dirY != 0){
                keyboard.handlerDown((dirY<0) ? 1 : 2)
                $("div[data-subject=y"+dirY+"]",$mc).css("background-position","100% 0");
            }
             $mc.data("dirY",dirY);
        }

    });

    $("#movecontrol").on("touchstart touchmove mousedown mousemove", function(event){
        var touch = event.targetTouches[0];
        var offset = $(this).offset();

        var posX = (touch.pageX-offset.left)/($(this).width()*clientMeassurement.f*clientConfiguration['keyScale']);
        var posY = (touch.pageY-offset.top)/($(this).height()*clientMeassurement.f*clientConfiguration['keyScale']);

        var dirX = (posX < (1/3)) ? -1 : ((posX > (2/3)) ? 1 : 0);
        var dirY = (posY < (1/3)) ? -1 : ((posY > (2/3)) ? 1 : 0);

        $(this).data("setDir")(dirX,dirY);

        //$(this).css("background-position","100% 0");

    });


    $("#movecontrol").on("touchend mouseup touchcancel", function(){
        $(this).data("setDir")(0,0);
    });
  }


},
upHandlers : {


},
handlerDown : function(k){
    console.log("Handler Down: "+k);
    keyboard.realKeys[k].hold = true;

   if(k == 0 && user.getOption("firable")){
    server.send({type : 1, stype : 0, key : k});
   } else
   if(k>=1 && k<=4 && user.getOption("movable"))
      server.send({type : 1, stype : 0, key : k});
   else if(k == 5 || k == 6){
      server.send({type : 1, stype : 0, key : k});
   }



},
handlerUp : function(k){
  console.log("Handler Up: "+k);
    keyboard.realKeys[k].hold = false;
    var handler = keyboard.upHandlers[k];
    if(typeof handler != "undefined"){
      console.log("Client Handler found");
      handler();
    }

    if(k>=0 && k <=6 && user.getOption("kb")) server.send({type : 1, stype : 1, key : k});
    else if(k == 10)
    if(user.getOption("menu") && user.modalmsg == 0) menu.show();
},
  down : function(event){ // Tastaturreaktionsfunktion mit Taste event.keyCode
    var kk = event.keyCode;
    var k = keyboard.keys[kk];
if(typeof k != "undefined" && !keyboard.realKeys[k].hold && (user.getOption("kb") || k == 10))
keyboard.handlerDown(k);
},
up : function(event){
  var kk = event.keyCode;
  var k = keyboard.keys[kk];
 if(typeof k != "undefined") keyboard.handlerUp(k);

},
press : function(event){
    var k = event.keyCode;
if(k == 13 && user.getOption("kb") && user.modalmsg == 0) chat.show(); // Wenn Enter gedrückt wurde
},
releaseAllKeys : function(){
for(var key in keyboard.realKeys){
  if(keyboard.realKeys[key].hold) keyboard.handlerUp(parseInt(key));
}
}
};