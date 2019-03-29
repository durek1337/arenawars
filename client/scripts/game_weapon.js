function Weapon(data){
  this.id = data.id;
  this.wid = data.wid;
  this.x = data.x;
  this.y = data.y;

  this.$dom = $("<img />",{"class" : "weapon", "id" : "weapon-"+data.id, "src" : "images/weapon/"+data.wid+"/ground.png"}).css({
     "left" : data.x,
     "top" : data.y
    }).appendTo($("#gameobjectsGround"));

}

function Projectile(id, weaponid, pos, dir, x, y, speed){
  this.$dom = $();


  var p = user.cache.player[Math.abs(pos)];
  if(pos > 0)
  p.showFire();
  pos = Math.abs(pos);

  var map = p.map;
  var dirvector = {x : -Math.sin(dir*Math.PI/180), y : Math.cos(dir*Math.PI/180)};
  var coord = {
    left : x,
    top : y
  };

  var $pr = $("<div />", {"class" : "bullet"}).css(coord);
  $pr
  .css({
   "background-image" : "url('images/weapon/"+weaponid+"/bullet.png')",
   "transform" : "rotate("+dir+"deg)"
    });
  this.$dom = $pr.appendTo($("#projectiles"));



  var distanceStep = {
    x : (dirvector.x<0) ? -coord.left/dirvector.x : (map.w-coord.left)/dirvector.x,
    y : (dirvector.y<0) ? -coord.top/dirvector.y : (map.h-coord.top)/dirvector.y
    };


  var step = (Math.abs(distanceStep.x) < Math.abs(distanceStep.y)) ? Math.abs(distanceStep.x) : Math.abs(distanceStep.y);

  var dist = {left : step*dirvector.x, top: step*dirvector.y};

  coord.left+=dist.left; // Kleiner Abstand
  coord.top+=dist.top; // -||-

  var time = Math.round((step/(speed*user.getRoom().projectilespeed))*1000);

  this.$dom.animate(coord,{duration: time, easing: "linear", always: function(){$(this).remove()}});


  this.disappear = function(){
  this.$dom.remove();
  }
    user.cache.player[pos].shots[id] = this;
}
