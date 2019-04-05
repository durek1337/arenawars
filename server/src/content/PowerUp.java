package content;

import javax.json.Json;

public class PowerUp extends Thread {
	String name;
	Character activator;
	int id, type, duration,groundh=50,groundw=50;
	double movementspeed=1;
	double projectilespeed=1;
	double projectiledamage=1;
	Room r;
	// type = 0 => Global, = 1 => Selbst, =2 => Alle anderen
	
	public PowerUp(int id, String n, int type){
		this.id = id;
		this.name = n;
		this.type = type;
	}
	public PowerUp bind(Room r){
		this.r = r;
		return this;
	}
	public PowerUp setDuration(int t){
		this.duration = t;
		return this;
	}
	public PowerUp setMovementSpeed(double f){
		this.movementspeed = f;
		return this;
	}
	public PowerUp setProjectileSpeed(double f){
		this.projectilespeed = f;
		return this;
	}
	public PowerUp setProjectileDamage(double f){
		this.projectilespeed = f;
		return this;
	}
	public void activate(Character c){
		this.activator = c;
		if(this.type == 0){
			if(this.movementspeed != 1)
			this.r.setMovementspeed(this.r.movementspeed*this.movementspeed);
			if(this.projectilespeed != 1)
			this.r.setProjectilespeed(projectilespeed*this.projectilespeed);
			
			// TODO Damage
			
		} else	
		if(this.type == 1){
			if(this.movementspeed != 1)
			c.setSpeed(c.speed*this.movementspeed);
			if(this.projectilespeed != 1)
			c.projectileSpeed *= this.projectilespeed;
		}
		start();

	}
	@Override
	public void run(){
		delay(this.duration);
		if(this.type == 0){
			if(this.movementspeed != 1)
			this.r.setMovementspeed(this.r.movementspeed/this.movementspeed);
			if(this.projectilespeed != 1)
			this.r.setProjectilespeed(this.r.projectilespeed/this.projectilespeed);
		} else
		if(this.type == 1){
			if(this.movementspeed != 1)
			this.activator.setSpeed(this.activator.speed/this.movementspeed);
			if(this.projectilespeed != 1)
			this.activator.projectileSpeed /= this.projectilespeed;
		}
	}
	public PowerUp clone(){
		return new PowerUp(this.id,this.name,this.type).setDuration(this.duration).setMovementSpeed(this.movementspeed).setProjectileSpeed(this.projectilespeed);
	}
	public static void delay(int t){
		try {
		    Thread.sleep(t);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}	


	}

	

}
