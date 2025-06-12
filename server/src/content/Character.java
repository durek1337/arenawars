package content;
import java.util.HashMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;



public class Character {

	
public String name;
int hp,hpMax,dir;
double speed;
Room room;
Weapon weapon;
Spectator controller;
Rectangle rec;
HashMap<Integer,Projectile> shots;
ProjectileShooter shooter;
int projectileId,dirX,dirY,punchtime,punchdmg;
double projectileSpeed = 1;
Statistic statistic;


public Character(Room r){
	System.out.println(r.getID()+" created a Character");
	
	this.room = r;
	this.statistic = null;
	this.hp = r.hp;
	this.hpMax = this.hp;
	this.speed = 1.0;
	this.dir = 0;
	this.shots = new HashMap<>();
	this.weapon = null;
	this.shooter = null;
	this.projectileId = 0;
	this.punchtime = 300;
	this.punchdmg = 5;
	
}

public void enter(int x, int y){
this.rec = new Rectangle(new Coord(x,y),null,Init.charWidth,Init.charHeight,this.dir);
	
JsonObjectBuilder databuilder = Json.createObjectBuilder()
.add("type",6) // Typ 6: CharData
.add("stype",0) // Subtyp 0: Spawne auf Map
.add("char", getJSON()); // Characterinformation
	// creating char

this.room.sendToAll(databuilder.build());
}

public void getHurt(Character attacker, int dmg, int dir){
	attacker.statistic.addHits(1);
	attacker.statistic.addDmgOut(dmg);
	this.statistic.addDmgIn(dmg);

	JsonObjectBuilder databuilder = Json.createObjectBuilder()
			.add("type",6) // Typ 6: CharData
			.add("stype",4) // Subtyp 4: Schaden
			.add("pos", this.controller.pos)
			.add("dmg", dmg)
			.add("dir",dir); // Characterinformation
				// creating char

			this.room.sendToAll(databuilder.build());	
if(this.hp > 0){
	if(this.hp > dmg)
	this.hp = this.hp-dmg;
	else {
		this.hp = 0;
		new Thread(() -> death(attacker)).start();
	}}
}



public Character bind(Spectator s){
	this.controller = s;
	this.statistic = new Statistic(s.pos);
	System.out.println(s.getID()+" was bound to Character");
return this;
}


boolean isControllable(){
	return this.hp > 0;
}


JsonObject getJSON(){

	JsonObjectBuilder ob = Json.createObjectBuilder();

	ob
			.add("pos",this.controller.pos)
			.add("name",this.controller.acc.name)
			.add("x",this.rec.x)
			.add("y",this.rec.y)
			.add("dir",this.dir)
			.add("hp",this.hp)
			.add("hpMax",this.hpMax)
			.add("speed", this.getSpeed())
			.add("weapon", (this.weapon == null) ? Json.createObjectBuilder().build() : this.weapon.getJSON());

return ob.build();
}

public void disappear(){

	JsonObject obj = Json.createObjectBuilder()
			.add("type", 6) // type 6: CharData
			.add("stype",1) // Char is leaving the map
			.add("pos",this.controller.pos) // Charid / Position
			.build();		
	this.room.sendToAll(obj);
}

public void death(Character killer){
	this.hp = 0;
	killer.statistic.addKills(1);
	this.statistic.addDeaths(1);
	this.controller.releaseAllKeys();
	this.room.newDeath(killer,this);
}

public void rotate(int dir){
	this.dir = dir;
	this.rec.setAngle(dir);
	JsonObject obj = Json.createObjectBuilder()
			.add("pos",this.controller.pos)
			.add("type", 6) // type 4: sending gamedata
			.add("stype",3) // subtype: 3: ROTATE
			.add("dir",dir)
			.build();

	this.room.sendToAll(obj);
}


public double getSpeed(){
return (this.weapon == null) ? this.speed : this.speed*this.weapon.walkspeed;
}
public void setSpeed(double s){
	this.speed = s;
	this.room.sendToAll(Json.createObjectBuilder().add("type",6).add("stype",6).add("pos",this.controller.pos).add("s",this.getSpeed()).build()); // Bewege in Richtungsvektor x,y
	this.holdDirection();
}

public void holdDirection(){
changeDirection(this.dirX,this.dirY);
}
public void changeDirection(int x, int y){
synchronized(this){
//	System.out.println(this.getID()+" directed to ("+x+"|"+y+")");
	this.dirX = x;
	this.dirY = y;
	this.room.sendToAll(Json.createObjectBuilder().add("type",6).add("stype",2).add("pos",this.controller.pos).add("dir",Json.createObjectBuilder().add("x",this.dirX).add("y",this.dirY).build()).add("x", this.rec.x).add("y", this.rec.y).build()); // Bewege in Richtungsvektor x,y

	/*
	if(x == 0 && y == 0){
		System.out.println(this.getID()+" ended Moving");
	}
	*/
	//if(!this.isAlive()) this.start();
}
}



public String getID() {
	return "Char #"+this.controller.pos;
}

public void addShot(){
	try{
		this.projectileId++;
		this.projectileId%=1000;

		int pid = this.projectileId;
		this.weapon.ammo -= 1;
		
		synchronized(this.shots){
		Projectile pm = new Projectile(pid,this).raiseSpeed(this.projectileSpeed).fire();
		this.shots.put(pid, pm);
		
		//this.holdDirection(); // Aktualisiere Position
		}

		if(this.weapon.ammo == 0){
			if(this.weapon.stack > 0) new Thread(() -> recharge()).start();
			else {
				unequipWeapon();
				return;
			}
		}
		
			

	} catch(Exception e){
		e.printStackTrace();
	}

	
}

public void use(){
	Rectangle wrec = null;
synchronized(this.room.map.weapons){
	for(Rectangle r : this.room.map.weapons.keySet()){
		if(this.rec.isColliding(r)){
			wrec = r;
			break;
		}
	}
	
	if(wrec != null){
		Weapon w = this.room.map.weapons.get(wrec);
		
		if(this.weapon == null || this.weapon.id != w.id){	
			if(this.weapon == null) this.room.currentWeapons++; // Die Waffe bleibt im Spiel, nachher wird dieser Wert wieder gesenkt daher hier erhöhen
			this.weapon = w;
		} else if(this.weapon.id == w.id) this.weapon.stack += w.ammo+w.stack;
		
		this.room.removeWeapon(wrec); // Anzahl der Waffen sinkt um 1
		sendWeaponData();
		this.setSpeed(this.speed);
		
		
	}
		
	
}
if(wrec == null){
synchronized(this.room.map.powerups){
	wrec = null;
	for(Rectangle r : this.room.map.powerups.keySet()){
		if(this.rec.isColliding(r)){
			wrec = r;
			break;
		}
	}

	if(wrec != null){
		PowerUp p = this.room.map.powerups.remove(wrec);
		p.activate(this);
		this.controller.room.sendToAll(Json.createObjectBuilder().add("type", 5).add("stype", 15).add("activator",this.controller.pos).add("id",wrec.id).add("name", p.name).build());
	}
	
}
}

}
public void recharge(){
if(this.weapon.ammo < this.weapon.ammolimit){
this.room.sendToAll(Json.createObjectBuilder().add("type",5).add("stype", 10).add("pos",this.controller.pos).add("t",this.weapon.rechargetime).build());
this.weapon.recharge();
sendWeaponData();
}
}
public void punch(){
	this.room.sendToAll(Json.createObjectBuilder().add("type",5).add("stype", 11).add("pos",this.controller.pos).add("t",this.punchtime).build());
	Rectangle hitbox = this.rec.clone();
	hitbox.setMid(hitbox.getMid());
	hitbox.setSize(hitbox.w,10).setCoord(hitbox.x, hitbox.y+20);
	
	synchronized(this.room.chars){
		for(Character c : this.room.chars){
			if(c != this && c.rec.isColliding(hitbox)){
				c.getHurt(this, this.punchdmg, dir);
				break;
			}
		}
	}
	
}

public void sendWeaponData(){
	this.room.sendToAll(Json.createObjectBuilder().add("type",6).add("stype", 5).add("weapon",this.weapon.getJSON()).add("pos",this.controller.pos).build());
}
public void unequipWeapon(){
	this.weapon = null;
	this.room.sendToAll(Json.createObjectBuilder().add("type",6).add("stype", 5).add("weapon",Json.createObjectBuilder().build()).add("pos",this.controller.pos).build());
	this.setSpeed(this.speed);
	this.room.removeWeapon();
}


}
