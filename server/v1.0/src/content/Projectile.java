package content;

import javax.json.Json;

public class Projectile {
	public static int timeBetweenStep = 50;
	int id,speed,dir;
	Weapon w;
	Character c;
	Rectangle rec;
	
	public Projectile(int id, Character c){
		this.id = id;
		this.w = c.weapon;
		this.c = c;
		this.dir = c.dir;
		
		this.speed = this.w.speed;

		Coord m = c.rec.getMid();
		Coord bc = new Coord((int)Math.round(m.x-this.w.bulletw/2),(int)Math.round(m.y+w.distance));
		this.rec = new Rectangle(bc,m,this.w.bulletw,this.w.bulleth,dir);	

	}
	public Projectile fire(){
		this.c.statistic.addShoots(1);
		this.c.room.sendToAll(Json.createObjectBuilder().add("type",5).add("stype", 2).add("pos", c.controller.pos).add("wid",w.id).add("x",this.rec.pol.xpoints[0]).add("speed",this.speed).add("y", this.rec.pol.ypoints[0]).add("id", this.id).add("dir",this.dir).build());
		return this;
	}
	public void move(){
		int newY = this.rec.y+(int)Math.round(this.speed/(1000/timeBetweenStep));
		this.rec.setCoord(this.rec.x, newY);
	}
	
	public boolean isColliding(){
		if(!this.c.room.map.rec.isColliding(this.rec))
		return true;
		else {
		for(Rectangle r : this.c.room.projMover.obstacles){
		if(this.rec.isColliding(r))	return true;
		}
		for(Character c : this.c.room.chars)
		synchronized(c){
		if(c.hp > 0 && this.rec.isColliding(c.rec)){
			c.getHurt(this.c, this.w.dmg, this.dir);
			return true;		
		}
		
		}
		}
		return false;
	}
	
	
}
