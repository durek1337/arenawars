function Player(data){
this.pos = data.pos;
this.name = data.name;
this.x = data.x;
this.y = data.y;
this.w = 50;
this.h = 30;
this.hp = data.hp;
this.hpMax = data.hpMax;
this.speed = data.speed;
this.weapon = data.weapon;
this.weaponid = data.weaponid;
this.dir = data.dir;
this.walking = false;
this.shooting = false;
this.walkInterval = null;
this.reloadTimeout = null;
this.shootTimeout = null;
this.walkStartTime = 0;
this.isFollowing = false;
this.map = user.getRoom().map;
this.shots = {};

this.domAppend = function(){ // F�gt DOM-Element zur HTML Struktur hinzu
var domid = "player-"+this.pos;
$("#"+domid).remove(); // l�schen falls vorhanden

var $p = $("<div />",{"class" : "player", id : domid}).append($("<div />",{"class" : "model"}).css("background-image","url('images/charset/pos"+((this.pos <= 20) ? this.pos : 0)+".png')").append($("<div />",{"class" : "fist hand"})));
this.$dom = $p.appendTo($("#player"));

this.$dom.zIndex(1);
this.appendName();
if(this.hp > 0)
this.getHurt(0);
else this.death();

this.addWeapon(this.weapon);
}

this.setSpeed = function(s){
  this.speed = s;
};

this.getDistanceToUser = function(){
if(this.isFollowing) return 0;
else
if(user.follow == null)
return this.map.getDistanceToDisplay(this.x,this.y);
}

this.applyAmmo = function(){
if(this.isFollowing){
var $d = $("#hud .weapon");
if(this.weapon == null)
$d.html('');
else{
var a = this.weapon.ammo;

var $a = $("<span />",{"text" : a});
if(a == 0)
$a.css("color","#f00");
var al = this.weapon.ammolimit;
var s = this.weapon.stack;
$d.html($a);
$d.append("/"+al+" ("+s+")");
}
}
}

this.addWeapon = function(w){
if(Object.size(w) == 0)
w = null;
this.weapon = w;
this.showWeapon();
}

this.showWeapon = function(){
var wid = (this.weapon == null) ? 0 : this.weapon.id;

$(".model .weapon, .model .weaponshot",this.$dom).remove();
if(this.isFollowing)
$("#hud .weapon").css("background","");

if(wid > 0){
var $w = $("<div />",{"class" : "weapon hand"}).css("background","url(images/weapon/"+wid+"/hand.png) 0 0");
var $ws = $("<div />",{"class" : "weaponshot hand"}).css("background","url(images/weapon/"+wid+"/hand.png) "+-this.w*3+"px 0");

$(".model",this.$dom).append($w).append($ws);
if(this.isFollowing){
$("#hud .weapon").css("background","url(images/weapon/"+wid+"/hud.png)");
}
}
this.applyAmmo();
}

this.reloadWeapon = function(reloadingtime){
  this.reloading = true;
  var $w = $(".model .weapon",this.$dom);

  var arr = [4,5,6];
  var frametime = reloadingtime/arr.length;
    if(this.reloadTimeout != null) clearTimeout(this.reloadTimeout);
  this.reloadTimeout = setTimeout(function(obj){
  $w.show();
  obj.reloading = false;
  },arr.length*frametime,this);

  for(var i=0;i<arr.length;i++){
  setTimeout(function(i){
  return function(){
  $w.css("background-position",-i*50+"px 0px");
  }}(arr[i]),i*frametime);
  }

}

this.getVolume = function(){
    var p = user.follow;
    var d = (p != null) ? Math.sqrt(Math.pow(p.x-this.x,2)+Math.pow(p.y-this.y,2)) : 0;
    return (1/(1+(d/200))*user.soundFx);
}

this.showFire = function(){
  var wid = this.weapon.id;
  var $weapon = $(".model .weapon",this.$dom);
  var $ws = $(".model .weaponshot",this.$dom);

  $weapon.hide();
  $ws.show();

$(".fire",$ws).remove();
$f = $("<div />",{"class" : "fire"}).css("background-image","url(images/weapon/"+wid+"/fire.png)").appendTo($ws);

if(user.getConfig("sounds")){
var $s = $.playSound("images/weapon/"+wid+"/shoot"+(Math.floor(Math.random()*6)+1)+".mp3",{volume: this.getVolume()});
}

this.shooting = true;

var arr = [1,2,1,0];
clearTimeout(this.shootTimeout);
this.shootTimeout = setTimeout(function(obj){
$f.remove();
$ws.hide();
$weapon.show();
obj.shooting = false;
},arr.length*50,this);

for(var i=0;i<arr.length;i++){
setTimeout(function(i){
return function(){
$f.css("background-position",-i*50+"px 0px");
}}(arr[i]),i*50);

}

if(this.isFollowing){
this.weapon.ammo--;
this.applyAmmo();
}

}

this.stopAnimation = function(delay){
this.$dom.delay(delay*0.8);
this.stopMove();
}

this.showPunch = function(t){
  this.shooting = true;
  var $w = $(".model .weapon",this.$dom);
  var $f = $(".model .fist",this.$dom);
  $w.hide();
  $f.show();


  var arr = [1,0,1,2];
  var frametime = t/arr.length;
  clearTimeout(this.shootTimeout);
  this.shootTimeout = setTimeout(function(obj){
  $w.show();
  $f.hide();
  obj.shooting = false;
  },arr.length*frametime,this);

  for(var i=0;i<arr.length;i++){
  setTimeout(function(i){
  return function(){
  $f.css("background-position",-i*50+"px 0px");
  }}(arr[i]),i*frametime);
  }
}
this.getHurt = function(dmg){
if(this.hp <= dmg)
 this.hp = 0;
else this.hp-=dmg;

if(this.isFollowing){
    var $lh = $("#hud .lifehud .lifebarContainer");
    var quo = this.hp/this.hpMax;
    $(".lifevalue",$lh).text((this.hp > 0 ) ? this.hp+"/"+this.hpMax : "TOT");
    $(".lifebar",$lh).css("right",-Math.round((1-quo)*$lh.width()));
}

}
this.death = function(){
$(".model",this.$dom).hide();
this.$dom.stop();
this.$dom.append($("<div />",{"class" : "death"}).css({
"transform" : "rotate("+this.dir+"deg)",
"background-image" : "url(images/charset/death"+this.pos+".png)"
}));

if(user.pos == this.pos && user.alive)
user.onDeath();

this.$dom.zIndex(0);
}

this.showBlood = function(dir){

if(user.getConfig("sounds")){
var $s = $.playSound("sounds/blood"+(Math.floor(Math.random()*5)+1)+".mp3",{volume: this.getVolume()});
}

console.log("Zeige Blut: "+(dir-this.dir));
var $blood = $("<div />",{"class" : "blood"});
$blood.css("transform","rotate("+parseInt(dir)+"deg)");

var $blood = $blood.appendTo($(".model",this.$dom));
var arr = [0,1,2,1,0];

for(var i=0;i<arr.length;i++){
setTimeout(function(i){
return function(){
$blood.css("background-position",i*50+"px 0px");
}}(arr[i]),i*100);

}

setTimeout(function(){
$blood.remove();
},arr.length*100);

}



this.appendName = function(){
  var charheight = 50;
    $(".playername",this.$dom).remove();
    var n = $('<div />',{'class' : 'playername','text' : this.name}).css("background-color","#"+playercolors[this.pos]);

    var calcWidth = 7*this.name.length+12;
    n.css({
      width: calcWidth,
      left: (-calcWidth+charheight)/2-3
    });

    this.$dom.append(n);
    return this;
  }

this.startMove = function(x,y){ // Bewege in Richtung x,y Element aus {-1,0,1}
var map = this.map;


if(x != 0 && y != 0){ // >> Diagonale Bewegung
console.log("Diagonale Bewegung erkannt");
var xEnd = (x > 0) ? (map.w-this.w) : 0;
var yEnd = (y > 0 ) ? (map.h-this.h) : 0;


var xDist = Math.abs(xEnd-this.x);
var yDist = Math.abs(yEnd-this.y);

if(xDist < yDist) // Ziel bei x schneller erreicht?
yEnd = this.y + xDist*Math.sign(y);

else
xEnd = this.x + yDist*Math.sign(x);

this.walkTo(xEnd,yEnd);
} else if(x != 0 || y != 0){  // >> Gerade Bewegung
console.log("Gerade Bewegung erkannt");
var xEnd = this.x;
var yEnd = this.y;


if((x == 1 || x == -1) && y == 0){ // Horizontale Bewegung
if(Math.sign(x) > 0)
xEnd = map.w-this.w;
else
xEnd = 0;
} else if((y == 1 || y == -1) && x == 0){ // Vertikal
if(Math.sign(y) > 0)
yEnd = map.h-this.h;
else
yEnd = 0;
}

this.walkTo(xEnd,yEnd);
} else { this.stopMove(); this.stopwalk(); } // Richtung ist (0|0)
}

this.stopMove = function(){
this.$dom.stop();
if(this.isFollowing) this.map.stopDisplay();
}



this.walkTo = function(x,y){
var obj = this;
var dur =  Math.round(Math.sqrt(Math.pow(obj.x-x,2)+Math.pow(obj.y-y,2))*4/(this.speed*user.getRoom().movementspeed));
console.log("Bewege in Richtung: "+x+" | "+y+" in "+dur+"ms");

if(this.walking)
this.stopMove();
else
this.startwalk();

if(this.isFollowing) this.map.moveDisplay(x,y,dur);



this.$dom.animate({
  left : x,
  top : y
},{
  easing: "linear",
  duration : dur,
  always : function(){
    obj.x = parseInt(obj.$dom.css("left")); // Speichere Koordinaten
    obj.y = parseInt(obj.$dom.css("top"));
    },
    step : function(){
      obj.$dom.zIndex(parseInt(obj.$dom.css("top")));
    }
});

}

this.startwalk = function(){
this.walkStartTime = (new Date()).getTime();
this.walking = true;

  var arr = [0, 1, 2, 1];

var funcMoveAnimation = function(obj){
for(var i=0;i<arr.length;i++){
var f = function(i){
if(!obj.shooting && !obj.reloading){
if(obj.walking)
obj.showFrame(arr[i]);
else obj.showFrame(0);
}
}

setTimeout(f,200*i,arr[i]);
}
};
funcMoveAnimation(this);
clearInterval(this.walkInterval);
this.walkInterval = setInterval(funcMoveAnimation,arr.length*200,this);

}


this.showFrame = function(num){
$(".model, .model .weapon",this.$dom).css({
  "background-position" : -num*50+"px "+ 0+"px"
});
}

this.stopwalk = function(){
console.log("Stoppe Bewegungsanimation nach "+((new Date()).getTime()-this.walkStartTime)+"ms");
clearInterval(this.walkInterval);
this.walking = false;
}


this.setCoord = function(x,y){
  var oldx = parseInt(this.$dom.css("left"));
  var oldy = parseInt(this.$dom.css("top"));
console.log("Positioniere von ("+oldx+"|"+oldy+") auf ("+x+"|"+y+") - Abweichung ("+(x-oldx)+"|"+(y-oldy)+")");
this.x = x;
this.y = y;
this.justify();
}


this.justify = function(){
this.$dom.css({
    left : this.x,
    top : this.y
});

if(this.isFollowing)
this.map.justifyDisplay(this.x,this.y);

}

  this.showText = function(m){

  if(m.length > 170) m = m.substring(0,170)+" ...";

         var $txt = $("<div />",{"class" : "player_text", text : m});
        var fs_w = 6;
        var fs_h = 12;

    var calcWidth = Math.round(m.length*fs_w+10);

    if(calcWidth > 200) calcWidth = 200;

     $txt.css({"width": calcWidth, "left" : -Math.round(calcWidth/2)});

     $txt = $txt.append($("<div />",{"class" : "player_texttriangle"}));
     $txt = $txt.appendTo(this.$dom);
    $txt.show().css("top",-$txt.height()-40).delay(1500+m.length*40).fadeOut(function(){ $(this).remove(); });
  }



this.rotate = function(deg,duration){

if(duration == 0)
$(".model",this.$dom).css("transform","rotate("+deg+"deg)");
else
$(".model",this.$dom).rotate(deg,duration);

this.dir = deg;

}

this.domRemove = function(){
this.$dom.remove();
}



this.follow = function(){
if(user.follow != null)
user.noFollow();

this.isFollowing = true;
user.follow = this;

this.showWeapon(this.weaponid);
this.map.justifyDisplay(this.x,this.y);
$("#hud").show();
this.getHurt(0);

}

this.domAppend();
this.justify();
this.rotate(this.dir,0);

this.delete = function(){
  this.domRemove();
}



user.cache.player[this.pos] = this;
if(user.pos == this.pos) this.follow();
}