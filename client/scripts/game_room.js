function Room(data){
this.id = data.id;
this.creator = data.creator;
this.extended = data.extended == 1;
this.title = data.title
this.limit = data.limit;
this.player = data.player;
this.spectator = null;
this.startgame = null;
this.map = null;
this.hp = data.hp;
this.rounds = data.rounds;
this.roundtimer = null;

if(this.extended){
this.movementspeed = data.movementspeed;
this.projectilespeed = data.projectilespeed;
}
this.removeRow = function(){
  $("#lobbyrow-"+this.id).remove();

}
this.showPlayer = function(){
  $("#lobbyrow-"+this.id+" td[name='player']").html("("+this.player+"/"+this.limit+")");
}
this.addPlayer = function(data){
 this.player++;
 if(!this.extended) this.showPlayer();
else
this.addPlayerToCell(data.pos,data.name);
}

this.removePlayer = function(pos){
 this.player--;
 if(!this.extended) this.showPlayer();
 else
 $("#roomplayerlist-"+pos).html('');
}


this.addToLobby = function(){
  $("#lobbytable tbody").append('<tr id="lobbyrow-'+this.id+'"><td>#'+this.id+'</td><td name="player"></td><td>'+this.title+'</td><td>'+this.rounds+' ('+this.hp+')</td><td><button onclick="user.joinRoom('+this.id+');">Beitreten</button></td></tr>');
}
this.goToRoom = function(data){
 user.roomid = this.id;
 var $table = $("#roomplayer table tbody");
$("#lobby").hide();
$("#room, #roomchat").show();
$("#room").tabs("option","heightStyle", "fill");


$(".publicchat .chatContent").html('');

this.spectator = data.spectator;

$table.html('');
for(var i=1;i<=this.limit;i++){ // Erstelle Kacheln für die Spielerübersicht
var newrow = i%2 == 1;
var endrow = (i%2 == 0 || i == this.limit) && i>0;

if(newrow)
$row = $("<tr />");
$row.append($("<td />",{"id" : "roomplayerlist-"+i}).css("background-color","#"+playercolors[i]));

if(newrow && i == this.limit)
$row.append($("<td />"));

if(endrow)
$table.append($row);
}

for(var i=0;i<this.spectator.length;i++){
var s = this.spectator[i];
var pos = s.pos;
this.addPlayerToCell(pos,s.name);
}
$("#roominformation").html('<b>#'+this.id+': '+this.title+'</b><br>Maximale Spieleranzahl: '+this.limit+'<br>Runden/Leben: '+this.rounds+'<br>Lebenspunkte zu Beginn: '+this.hp);
}
this.leaveRoom = function(){
$("#room").tabs("option", "active", 0);
$("#room").hide();
user.roomid = 0;
}

this.addPlayerToCell = function(pos,name){
  $('#roomplayerlist-'+pos).html(name);
}
// Erweiterte Angaben
if(!this.extended) {
this.delete = function(){
this.removeRow();
}
this.addToLobby();

} else {
this.goToRoom(data);
this.startgame = function(){
  server.send({type : 4, stype : 3});
}
this.bindMap = function(m){
 this.map = new Map(m);
 this.map.apply();
 $("#room").hide();
}

}
user.cache.rooms[this.id] = this;
}