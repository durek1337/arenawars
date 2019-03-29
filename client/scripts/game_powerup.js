function Powerup(data){
this.id = data.id;
this.pid = data.pid;

  this.$dom = $("<img />",{"class" : "powerup", "id" : "powerup-"+data.id, "src" : "images/powerup/"+data.pid+".png"}).css({
     "left" : data.x,
     "top" : data.y
    }).appendTo($("#gameobjectsGround"));

}