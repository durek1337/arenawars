function Gameobject(data, ground){
  console.log(data);
this.id = data.id; // Id auf der Map
this.goid = data.goid; // Gameobject-ID
this.x = data.x;
this.y = data.y;
this.w = data.w;
this.h = data.h;
var $target = null;
if(ground) $target = $("#gameobjectsGround"); else $target = $("#gameobjectsOver");

this.$dom = $("<div />",{"class" : "gameobject"}).css({
  "left" : this.x,
  "top" : this.y,
  "width" : this.w+"px",
  "height" : this.h+"px",
  "background" : "url(images/gameobject/"+this.goid+".png)"
}).appendTo($target);



}