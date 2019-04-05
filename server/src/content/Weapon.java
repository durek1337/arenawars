package content;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * @author Dominik
 * Waffen
 * Es gibt vier Waffen: Fäuste, Pistole, Shotgun und Sturmgewehr.
 * Waffen werden verwendet um gegnerischen Charakteren Schaden zuzufügen.
 * Das Kampfsystem beinhaltet mehrere Fern- und Nahkampfwaffen, mit denen der Spieler in der Lage ist seinem Gegner Schaden zu zufügen, indem er durch erfolgreiche Treffer ihren Lebensvorrat reduziert.
 * Die einzelnen Waffen besitzen verschiedene Eigenschaften, wie zB.:
 *	Schaden pro Treffer
 *	Feuerrate
 *	(optional) Genauigkeit
 * Genauere Informationen können dem Benutzerhandbuch bzw. den Spielregeln entnommen werden.
 */


public class Weapon {
int id, dmg, speed, chargetime, rechargetime, ammolimit, stack, ammo, bulletw, bulleth, groundw,groundh, distance;
String name;
double walkspeed = 1;
boolean hold;
public Weapon(int id, String name, int dmg, int speed, int chargetime, int rechargetime, int ammolimit, boolean hold){
	this.id = id;
	this.name = name;
	this.dmg = dmg;
	this.speed = speed; /// Pixel/Sekunde
	this.chargetime = chargetime;
	this.hold = hold;
	this.ammo = ammolimit;
	this.ammolimit = ammolimit;
	this.rechargetime = rechargetime;
	this.stack = this.ammolimit*3;
	
}
public Weapon setBulletSize(int w, int h){
	this.bulletw = w;
	this.bulleth = h;
	return this;
}
public Weapon setGroundSize(int w, int h){
	this.groundw = w;
	this.groundh = h;
	return this;
}
public Weapon setDistance(int d){
	this.distance = d;
	return this;
}
public Weapon setWalkSpeed(double d){
	this.walkspeed = d;
	return this;
}
public Weapon clone(){
return new Weapon(this.id, this.name, this.dmg, this.speed, this.chargetime, this.rechargetime, this.ammolimit, this.hold).setBulletSize(this.bulletw, this.bulleth).setDistance(this.distance).setWalkSpeed(this.walkspeed).setGroundSize(this.groundw, this.groundh);
}
public void recharge(){
this.stack+=this.ammo;
this.ammo = 0;
	
int ra = this.ammolimit;
if(ra > this.stack) ra = this.stack;

try {
	Thread.sleep(this.rechargetime);
} catch (InterruptedException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

this.ammo = ra;
this.stack -= ra;
}
public void addAmmo(int a){
this.stack += a;
}

public JsonObject getJSON(){
	return Json.createObjectBuilder()
			.add("id", this.id)
			.add("ammo",this.ammo)
			.add("ammolimit", this.ammolimit)
			.add("stack", this.stack)
			.build();
}

}
