package content;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.SwingUtilities;



public class Room {

	int id,mapid,limit=8,round=0,rounds=5,hp=100,roundDeaths=0, players=0, currentWeapons=0, currentWeaponId=0, currentGameobjectId=0, currentPowerupId=0;
	double movementspeed=1,projectilespeed=1;
	Map map;
String title;
Account creator;
Channel gamechannel;
Channel deathchannel;
HashMap<Integer,Spectator> spectator = new HashMap<>();
HashMap<Integer, Integer> accounts = new HashMap<>();
ArrayList<Character> chars = new ArrayList<>();
ArrayList<Statistic> roundstatistics = new ArrayList<>(); 
MapFrame mapframe;
Boolean running,started,respawn,revenge,teams,friendlyfire,weapondrop,powerups;

ProjectileMover projMover=null;
CharacterMover charMover=null;
PowerupSpawner powerupSpawner = null;

public Room(Account creator, JsonObject data){
	try{
	System.out.println(creator.getID()+": is trying to create a Room...");
	this.id = 0;
	this.mapid = data.getInt("mapid");
	this.running = false;
	this.started = false;
	this.creator = creator;
	this.title = data.getString("title");
	this.limit = data.getInt("limit");
	this.rounds = data.getInt("rounds");
	this.hp = data.getInt("health");
	this.map = null;
	this.respawn = data.getBoolean("respawn");
	this.weapondrop = data.getBoolean("weapondrop");
	this.revenge = data.getBoolean("revenge");
	this.teams = data.getBoolean("teams");
	this.friendlyfire = data.getBoolean("friendlyfire");
	this.powerups = data.getBoolean("powerups");
	
	this.mapframe = null;

	int tl = this.title.length();
	if(tl < 3 || tl > 20) throw new GameException("Der Raumtitel muss 3-20 Zeichen lang sein.");
	if(!Init.maps.containsKey(this.mapid)) throw new GameException("Die Map existiert nicht");
	if(this.hp <= 0) throw new GameException("Falsche HP-Angabe");
	if(this.limit < 0) throw new GameException("Falsche Rundenanzahlangabe");

	synchronized(Init.rooms){
		
		int i = 1;
		for(int id : Init.rooms.keySet()){
			if(i < id){
				this.id = i;
				break;
			} else i++;	
		}
		if(this.id == 0)
			this.id = i;
		
		this.gamechannel = new Channel(1,this.getID(),(Account a) -> {
			int pos = 0;
			Spectator s = null;

			if(!this.started){
			pos = getNewPosition();
			s = new Spectator(a,this,pos);
			a.spectator = s;

			this.gamechannel.receive(Json.createObjectBuilder().add("type",4).add("stype",5).add("spectator", s.getJSON()).build());
			this.spectator.put(pos,s);
			Init.lobby.receive(Json.createObjectBuilder().add("type",4).add("stype",5).add("roomid", this.id).build());
			
			this.gamechannel.receiveText("", a.name+" hat den Raum betreten");
			
			} else a.spectator = new Spectator(a,this,0);
			
			/*
			else if(!this.accounts.containsKey(a.id)) { // Wenn kein Reconnect, erstelle neuen Zuschauer
				s = new Spectator(a,this,pos);
				a.spectator = s;
				this.spectator.put(pos,s);
			}
//			*/
			
			
		},(Account a) -> {
			this.gamechannel.receiveText("", a.name+" hat den Raum verlassen");
				if(!this.started){
				if(a == this.creator){
					this.creator = null;
					sendToAll(Json.createObjectBuilder().add("type",1).add("stype",1).add("msg","Der Spielersteller hat das Spiel verlassen. Jeder kann nun das Spiel starten.").build());
				}
				
				leave(a.spectator);
				Init.lobby.receive(Json.createObjectBuilder().add("type",4).add("stype",6).add("roomid", this.id).build());
				sendToAll(Json.createObjectBuilder().add("type",4).add("stype",6).add("pos", a.spectator.pos).build());
			} else {
				try {
					a.send(Json.createObjectBuilder().add("type",5).add("stype",1).build());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				a.spectator = null;
			}
			if(this.gamechannel.receiver.size() == 0) deletegame();
		});
		this.deathchannel = new Channel(1,this.getID()+" tot",(Account a) -> {},(Account a) -> {});
		
		Init.rooms.put(i, this);
	}

	Init.lobby.receive(Json.createObjectBuilder().add("type", 4).add("stype", 1).add("room", this.getLightJSON()).build());
	
	
	join(creator); // Creator will be added to game	

	
	System.out.println(this.getID()+": Room created successfully");


	
	} catch(GameException gex){
		this.creator.sendError(gex.getMessage());
	} catch(Exception ex){
		this.creator.sendError("Beim Erstellen des Spiels trat ein Fehler auf.");
		ex.printStackTrace();
	}

}
String getID(){
return "Room #"+this.id;
}
int getNewPosition(){ // Eine freie Position wird aufsteigend gesucht. Hat ein Spieler die Position verlassen, kann sie eingenommen werden
int pos = 1;

synchronized(this.spectator){
Object[] objkeys = this.spectator.keySet().toArray();
Integer[] keys = Arrays.copyOf(objkeys, objkeys.length, Integer[].class);
Arrays.sort(keys);

for(int id : keys){
	if(pos < id){
		 return pos;
	} else pos++;	
}
if(pos > this.limit)
	pos = 0;

}

return pos;
}

public void join(Account a){
try{
	if(!this.started && this.spectator.size() >= this.limit){ // Fehlerbedingungen
		a.sendError("Das Spiel ist bereits voll!");
	} else {
		a.leaveChannel(Init.lobby);
		a.enterChannel(this.gamechannel);
		a.send(Json.createObjectBuilder().add("type", 4).add("stype", 3).add("room",this.getJSON()).build());
	
	
	if(this.started){
		int pos = 0;
		// Wenn das Spiel bereits läuft
			if(this.accounts.containsKey(a.id)){ // Wenn der Account zum Spiel gehört
				a.sendMessage("Die Verbindung zum Spiel wurde wiederhergestellt!");
				
				a.spectator = this.spectator.get(this.accounts.get(a.id));
				a.spectator.acc = a;
				pos = a.spectator.pos;

			} else {
				synchronized(this.spectator){
				a.sendMessage("Du bist dem Spiel als Zuschauer beigetreten!");
				}
				
				}
			a.send(this.getPosJSON(pos));
			a.send(this.getGameJSON());
		
	}
	
	}
} catch (Exception e){
		a.sendError("Fehler beim Beitreten des Spiels");
}
}
public JsonObject getPosJSON(int pos){
	return Json.createObjectBuilder().add("type", 5).add("stype",-1).add("pos",pos).build();
}
public void leave(Account a){
	a.leaveChannel(this.gamechannel);
	try {
		a.send(Json.createObjectBuilder().add("type", 4).add("stype", 4).build());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} // Geh aus dem Raum
	a.spectator = null;
}

public void deletegame(){
	
	this.started = false;
	System.out.println(this.getID()+": Delete Game");
	stop();
	if(this.mapframe != null)
	this.mapframe.dispose();
	Init.lobby.receive(Json.createObjectBuilder().add("type", 4).add("stype", 7).add("room", this.id).build());
	Init.rooms.remove(this.id);
	System.out.println(this.getID()+": Room deleted");
}

public void respawn(){
	this.players = 0;
	this.roundDeaths = 0;
	synchronized(this.chars){
	this.chars.clear();
	}
	
	synchronized(this.map.powerups){
		this.currentPowerupId = 0;
		this.map.powerups.clear();
	}
	synchronized(this.map.weapons){
		this.map.weapons.clear();
		this.currentWeapons = 0;
		this.currentWeaponId = 0;
	}
	synchronized(this.map.gameobjects){
		this.map.gameobjects.clear();
		this.currentGameobjectId = 0;
	}
	synchronized(this.spectator){
		for(Spectator s : this.spectator.values()){
		if(s.pos > 0) this.accounts.put(s.acc.id, s.pos); // Spieler fest mit Account verknüpfen
			s.send(getPosJSON(s.pos));
	}


		for(Object obj : this.map.groundlayer){
			JsonObject jobj = (JsonObject)obj;
			int id = jobj.getInt("go");
			int x = jobj.getInt("x");
			int y = jobj.getInt("y");
			addGameobject(new Coord(x,y),id,true);			
		}
		for(Object obj : this.map.overlayer){
			JsonObject jobj = (JsonObject)obj;
			int id = jobj.getInt("go");
			int x = jobj.getInt("x");
			int y = jobj.getInt("y");
			addGameobject(new Coord(x,y),id,false);		
		}


	this.gamechannel.receive(this.getGameJSON());
		

	}
}

public void newDeath(Character killer, Character victim){
boolean respawns = true;
int deaths = victim.statistic.getDeaths();
	if((this.respawn && deaths >= this.rounds) || !this.respawn){
	respawns = false;
	this.roundDeaths++;
	}

sendToAll(Json.createObjectBuilder().add("type", 5).add("stype",8).add("killer",killer.controller.pos).add("victim",victim.controller.pos).add("lifes",(respawns) ? this.rounds-deaths : 0).build());
	System.out.println(getID()+": "+this.roundDeaths+" of "+this.players+" are dead");

	if(victim.weapon != null)
		if(!this.weapondrop) addRandomWeapon();
		else {
			this.currentWeapons--;
			addWeapon(victim.rec.getCoord(),victim.weapon);
		}
	
	if(!respawns && this.roundDeaths >= this.players-1)
		roundFinished();

	else if(respawns){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			synchronized(this.chars){
			this.chars.remove(victim);
			}
			Statistic rs = victim.statistic;
			enter(createChar(victim.controller,rs));
		}
	}
}

public void enterChars(){ // Lasse alle Charaktere die Map betreten
	for(Spectator s : this.spectator.values()){
	if(s.isController()){ // Sonst nur Zuschauer
	Character c = createChar(s);
	enter(c);
	this.players++;
	}
	}
	synchronized(this.map.weapons){
	synchronized(this.map.weaopons){
		while(this.currentWeapons < this.players*2)
			addRandomWeapon();
	}
}

public void run(){
this.running = true;
if(this.charMover == null){
	this.charMover = new CharacterMover(this);
	this.charMover.start();
}
if(this.projMover == null){
this.projMover = new ProjectileMover(this);
this.projMover.start();
}

if(this.powerupSpawner == null && this.powerups){
this.powerupSpawner = new PowerupSpawner(this);
this.powerupSpawner.start();
}

System.out.println(this.getID()+": Is running!");
}
public void stop(){
this.running = false;
System.out.println(this.getID()+": Stopped!");

}

public void roundFinished(){
	ArrayList<Statistic> stats = new ArrayList<>();
	System.out.println(this.getID()+": Round "+this.round+" finished");
	Boolean newgame = false;
	JsonArrayBuilder arr = Json.createArrayBuilder();
	stop();
	synchronized(this.chars){
		for(Character c : this.chars){
			Statistic s = c.controller.stat.add(c.statistic);
			stats.add(s);
			c.controller.releaseAllKeys();
			c.changeDirection(0, 0);
		}
		Collections.sort(stats);
		for(Statistic s : stats){
			arr.add(s.getJSON());
		}
	}
	
	if(this.round < this.rounds && !this.respawn) newgame = true;
	
	sendToAll(Json.createObjectBuilder().add("type", 5).add("stype", 7).add("round",this.round).add("newgame",newgame).add("stats",arr).build());

	try {
		Thread.sleep(15000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
		if(this.started){
		if(newgame)
		roundStart();
		else gameFinished();
		}
	}

}

public void roundStart(){
	this.round++;
	respawn();
	run();
	enterChars();
}


public void gameFinished(){
		this.gamechannel.iterateAll((Account a)->{ // Schmeiße alle Clients raus -> Spiel schließt automatisch
			leave(a);
			a.enterChannel(Init.lobby);
			});
}

public void startgame() {

	System.out.println(this.getID()+" is going to start!");
	try{
// Spiel- und Mapdaten sammeln
if(this.started) throw new GameException("Das Spiel läuft bereits");
if(this.spectator.size() < 2) throw new GameException("Ein Spiel erfordert mindestens 2 Spieler");

this.running = true;
this.started = true;
this.map = Init.maps.get(this.mapid).clone();


System.out.println(this.getID()+" started!");

try{
	roundStart();
	if(new Boolean(Init.config.get("graphicalMonitoring")))
	SwingUtilities.invokeLater(() -> new MapFrame(this));
} catch(Exception e){
	e.printStackTrace();
}


	} catch(GameException e){
		creator.sendError(e.getMessage());
	} catch(Exception e){
		creator.sendError("Beim Starten des Spiels ist ein unbekannter Fehler aufgetreten!");
		e.printStackTrace();
	}

}


Character createChar(Spectator s){
	Character c = new Character(this);
	synchronized(this.chars){
	this.chars.add(c);
	}
	// Hier müsste der Spawnpunkt am besten definiert werden
	s.controlChar(c);
	
	return c;	
}
Character createChar(Spectator s, Statistic rs){
	synchronized(this.chars){
		int i = 0;
		for(Character c : this.chars){
			if(c.controller == s){
			this.chars.remove(i);
			break;
			}
			i++;
		}
	}
Character c = createChar(s);
c.statistic = rs;
return c;
}

public void leave(Spectator s){
System.out.println(s.getID()+" leaved "+this.getID());
this.spectator.remove(s.pos);
}

public void sendToAll(JsonObject obj){
this.gamechannel.receive(obj);
}




public void enter(Character c){
boolean colliding = true;
int x=0,y=0;
while(colliding){
colliding = false;
x = (int)Math.round(Math.random()*(this.map.w-Init.charWidth));
y = (int)Math.round(Math.random()*(this.map.h-Init.charHeight));

Rectangle crec = new Rectangle(new Coord(x,y),null,Init.charWidth,Init.charHeight,0);
for(Rectangle r : this.charMover.obstacles){
if(crec.isColliding(r)){
	colliding = true;
	break;
	}
}
}
c.enter(x,y);
}

JsonObject getLightJSON(){ // Get JSON Roominformation for listing
	JsonObjectBuilder ob = Json.createObjectBuilder();

	ob
	.add("id",this.id)
	.add("extended",0) // Wenige Rauminformationen für die Lobbyliste
	.add("title",this.title)
	.add("hp",this.hp)
	.add("rounds", this.rounds)
	.add("limit", this.limit)
	.add("player", (this.running) ? this.accounts.size() : this.spectator.size());

return ob.build();
}

JsonObject getJSON(){ // Get JSON Roominformation
	JsonObjectBuilder ob = Json.createObjectBuilder();
	JsonArrayBuilder parr = Json.createArrayBuilder();
	synchronized(this.spectator){
	for(Spectator s : this.spectator.values()) parr.add(s.getJSON());
	}

	ob
			.add("id",this.id)
	synchronized(this.map.weapons){
	for(Rectangle r : this.map.weapons.keySet())
		warr.add(Json.createObjectBuilder().add("id", r.id).add("x",r.x).add("y",r.y).add("wid", this.map.weapons.get(r).id));
			.add("rounds", this.rounds)
			.add("limit", this.limit)
			.add("movementspeed",this.movementspeed)
			.add("projectilespeed",this.projectilespeed)
			.add("spectator", parr.build());
	//if(this.props != null)
	//ob.add("props",this.props.getJSON());

return ob.build();
}
JsonObject getGameJSON(){ // Get JSON Roominformation
	JsonObjectBuilder ob = Json.createObjectBuilder();
	JsonArrayBuilder carr = Json.createArrayBuilder();
	JsonArrayBuilder warr = Json.createArrayBuilder();
	JsonArrayBuilder garr = Json.createArrayBuilder();
	JsonArrayBuilder parr = Json.createArrayBuilder();
	
	synchronized(this.chars){
	for(Character c : this.chars) carr.add(c.getJSON());
	}
	synchronized(this.map.weaopons){
	for(Rectangle r : this.map.weaopons.keySet())
		{
		warr.add(Json.createObjectBuilder().add("id", r.id).add("x",r.x).add("y",r.y).add("wid", this.map.weaopons.get(r).id));
		}
	}
	synchronized(this.map.gameobjects){
	for(Rectangle r : this.map.gameobjects.keySet())
		{
		Gameobject go = this.map.gameobjects.get(r);
		garr.add(Json.createObjectBuilder().add("ground",go.ground).add("go",go.getJSON()).build());
		}
	}
	synchronized(this.map.powerups){
	for(Rectangle r : this.map.powerups.keySet())
		{
		PowerUp pu = this.map.powerups.get(r);
		parr.add(Json.createObjectBuilder().add("id",r.id).add("pid", pu.id).add("x", r.x).add("y", r.y).build());
		}
	}

	ob
			.add("type", 5).add("stype", 0)
			.add("chars", carr.build())
			.add("weapons", warr.build())
			.add("gameobjects", garr.build())
			.add("powerups",parr.build())
			.add("map", this.map.getJSON());
	
	//if(this.props != null)
	//ob.add("props",this.props.getJSON());

return ob.build();
}



static JsonObject getListJSON(){
	JsonArrayBuilder builder = Json.createArrayBuilder();
	JsonObjectBuilder objbuilder = Json.createObjectBuilder();
	
for(Room r : Init.rooms.values()) builder.add(r.getLightJSON());

objbuilder
.add("type", 4) // Game-Data
.add("stype", 0) // Liste Lobbydaten
.add("rooms", builder.build())
.add("online",Init.lobby.getCount());

return objbuilder.build();
}

public void add(){

}


public void out(){
	System.out.println("### Mapinstance of Map "+this.id+"  ###");
	System.out.print("Activated by Spectator: ");
	for(int i=0;i<spectator.size();i++)
		System.out.print(spectator.get(i).getID()+" ");
		System.out.print("Contains Chars: ");
	for(int i=0;i<chars.size();i++)
		System.out.print(chars.get(i).getID()+" ");
	
}

public void removeWeapon(Rectangle r) {
	sendToAll(Json.createObjectBuilder().add("type", 5).add("stype", 6).add("id", r.id).build());
	synchronized(this.map.weapons){
	this.map.weapons.remove(r);
	}
	removeWeapon();
}

public void removeWeapon(){
	this.currentWeapons--;
	while(this.currentWeapons < this.players*2) addRandomWeapon();

	System.out.println("Weapon Removed: CW: "+this.currentWeapons+" Pl:+"+this.players);
}


public void addRandomWeapon() {
	int weaponid = (int) Math.ceil(Math.random()*Init.weapons.size());
	Weapon w = Init.weapons.get(weaponid).clone();
	Coord c = new Coord((int)Math.round(Math.random()*(this.map.w-w.groundw)),(int)Math.round(Math.random()*(this.map.h-w.groundh)));
	addWeapon(c, w);
}

public void addRandomPowerup(){
	int powerupid = (int) Math.ceil(Math.random()*Init.powerups.size());
	PowerUp p = Init.powerups.get(powerupid).clone();
	
	Coord c = new Coord((int)Math.round(Math.random()*(this.map.w-p.groundw)),(int)Math.round(Math.random()*(this.map.h-p.groundh)));	
	addPowerup(c,p);
	
	
}

public void addGameobject(Coord c, int goid, boolean ground){
	this.currentGameobjectId++;
	Gameobject go = Init.gameobjects.get(goid).clone();
	Rectangle r = new Rectangle(c,null,go.w,go.h,0).addId(this.currentGameobjectId);
	go.addRec(r);
	synchronized(this.map.gameobjects){
		this.map.gameobjects.put(r,go);
	}
	if(this.running)
	sendToAll(Json.createObjectBuilder().add("type", 5).add("stype", 9).add("ground",ground).add("go", go.getJSON()).build());

	
}

public void addWeapon(Coord c, Weapon w){
	this.currentWeaponId++;
	Rectangle r = new Rectangle(c,null,w.groundw,w.groundh,0).addId(this.currentWeaponId);

	
	synchronized(this.map.weapons){
	this.map.weapons.put(r, w);
	}
	this.currentWeapons++;
	if(this.running)
	sendToAll(Json.createObjectBuilder().add("type", 5).add("stype", 5).add("weapon", Json.createObjectBuilder().add("id",r.id).add("wid",w.id).add("x",r.x).add("y",r.y).build()).build());
}
public void addPowerup(Coord c, PowerUp p){
	this.currentPowerupId++;
	Rectangle r = new Rectangle(c,null,p.groundw,p.groundh,0).addId(this.currentPowerupId);

	
	synchronized(this.map.powerups){
	this.map.powerups.put(r, p.bind(this));
	}
	
	if(this.running)
	sendToAll(Json.createObjectBuilder().add("type", 5).add("stype", 14).add("powerup", Json.createObjectBuilder().add("id",r.id).add("pid", p.id).add("x", r.x).add("y", r.y).build()).build());
}

public void setMovementspeed(double f){
	this.movementspeed = f;
	sendToAll(Json.createObjectBuilder().add("type",5).add("stype",12).add("s",f).build());
	
	synchronized(this.chars){ // Animationen neu starten
		for(Character c : this.chars)
			c.holdDirection();
	}
	
}
public void setProjectilespeed(double f){
	this.projectilespeed = f;
	sendToAll(Json.createObjectBuilder().add("type",5).add("stype",13).add("s",f).build());
	synchronized(this.chars){ // Animationen neu starten
	for(Character c: this.chars){
		synchronized(c.shots){
		for(Projectile p : c.shots.values()){
			p.refreshAnimation();
		}
		}
		
		
	}
	}
	
}






public int getLatency() {
if(this.projMover != null && this.charMover != null)
return (int) Math.ceil((this.projMover.latency+this.charMover.latency)/2);
else return 0;
}


}
