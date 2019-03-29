function Map(data){
    this.id = data.id;
    this.title = data.title;
    this.w = data.w;
    this.h = data.h;


    this.apply = function(){
      console.log("Map #"+this.id+" ("+this.title+") wird angewendet...");
      $("#map").css({
      width : this.w,
      height: this.h
      }).show();

    }
    this.justifyDisplay = function(x,y){
    this.stopDisplay();
    x -= clientMeassurement.displayW/2;
    y -= clientMeassurement.displayH/2;

    $("#map").css({
      left : -x,
      top: -y
      });
    }

    this.moveDisplay = function(x,y,dur){

      if(dur <= 0) dur = 1;
    x -= clientMeassurement.displayW/2;
    y -= clientMeassurement.displayH/2;

/**
    var x = (x < 0) ? 0 : x;
    x = (x > this.w) ? this.w : x;
    var y = (y < 0) ? 0 : y;
    y = (y > this.w) ? this.w : y;
**/

    $("#map").animate({
            left : -x,
            top: -y
            },{
            easing : "linear",
            duration : dur
            });
    }
   this.stopDisplay = function(){
     $("#map").stop(true);
   }
}