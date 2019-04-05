package content;

import javax.json.Json;
import javax.json.JsonObject;

public class Gameobject {
Rectangle rec;
Coord c;
int id,w,h;
String name;
boolean passable;
boolean ground;
boolean interleaving;

public Gameobject(int id, String name){
	this.id = id;
}
public Gameobject setSize(int w, int h){
	this.w = w;
	this.h = h;
	return this;
}
@Override
public Gameobject clone(){
	return new Gameobject(this.id,this.name).setSize(this.w, this.h).setPassable(this.passable).setGround(this.ground).setInterleaving(this.interleaving);
}
public Gameobject setPassable(boolean b){
	this.passable = b;
	return this;
}
public Gameobject setGround(boolean b){
	this.ground = b;
	return this;
}
public Gameobject setInterleaving(boolean b){
	this.interleaving = b;
	return this;
}
public void addRec(Rectangle r){
	this.rec = r;
}

public JsonObject getJSON(){
return Json.createObjectBuilder().add("goid", this.id).add("id",this.rec.id).add("x", this.rec.x).add("y", this.rec.y).add("w", this.rec.w).add("h", this.rec.h).build();
}

}
