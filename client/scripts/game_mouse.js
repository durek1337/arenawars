mouse = {
  x : 0,
  y : 0,
  angle : 0,
  changeAngle : true,
    init : function(){
      $h = $("#handlerAll");
        var mm = function(event){
        if(user.ingame && user.pos > 0 && user.getOption("rotatable")){
        //console.log("moved: "+event.pageX+" | "+event.pageY);
        var $d = $("#display");
        var disp = $d.position();
        var dispWidth = Math.round($d.width());
        var dispHeight = Math.round($d.height());
        var charWidth = Math.round(50*clientMeassurement.f);
        var charHeight = Math.round(40*clientMeassurement.f); // Kleiner Ausgleich aufgrund der Patronenbreite


        var map = $("#map").position();
        var c = user.cache.player[user.pos].$dom.position();
        mouse.x = event.pageX-(disp.left-dispWidth/2+map.left+c.left+charWidth/2);
        mouse.y = event.pageY-(disp.top-dispHeight/2+map.top+c.top+charHeight/2);

        var a = Math.round(mouse.getAngle(mouse.x,mouse.y));
        if(a != mouse.angle && mouse.changeAngle)
        mouse.setAngle(a);

        }
        };
        var md = function(event){
          if(user.getOption("firable"))
           keyboard.handlerDown(0); // Feuer
        };
        var mu = function(event){
          if(user.getOption("firable"))
            keyboard.handlerUp(0);
        };
        if(!isMobileDevice)
        $h.mousemove(mm).mousedown(md).mouseup(mu);
        else {
        $h.on("touchstart",function(e){
          mm(e.targetTouches[0]);
          md(e.targetTouches[0]);
          })
          .on("touchend",mu)
          .on("touchmove",function(e){
            mm(e.targetTouches[0])
          });
        var $sc = $("#shootcontrol");
          $sc.css("transform","scale("+clientConfiguration['keyScale']+")");
          $sc.on("touchstart touchmove mousedown mousemove", function(event){

              var touch = event.targetTouches[0];
              var offset = $(this).offset();
              var w = ($(this).width()*clientMeassurement.f*clientConfiguration['keyScale']);
              var h = ($(this).height()*clientMeassurement.f*clientConfiguration['keyScale']);
              var distX = touch.pageX-offset.left-w/2;
              var distY = touch.pageY-offset.top-h/2;

              var posX = distX/(w/2);
              var posY = distY/(h/2);

              var a = Math.round(180*Math.atan2(-posX,posY)/Math.PI);

              var amp = Math.sqrt(Math.pow(posX,2)+Math.pow(posY,2));

            if(a != mouse.angle && mouse.changeAngle){
                $sc.find("div[data-subject=stick]").css({
                   left : distX,
                   top: distY
                }).html(a+"&deg;");
                mouse.setAngle(a);
            }
            if(amp > 0.5)
                md();

          })
          .on("touchend",function(){
            mu();
            $sc.find("div[data-subject=stick]").css({
              left:0,
              top:0
            })
          });

         }


  },
  getAngle : function(x,y){
    return 180*Math.atan2(-x,y)/Math.PI;
  },
  setAngle : function(a){
    mouse.angle = a;
    mouse.changeAngle = false;
    setTimeout(function(){
      mouse.changeAngle = true;
    },50);
    server.send({type: 2, stype:1, angle: a});
  }

};