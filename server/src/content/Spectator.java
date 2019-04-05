package content;

import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;

public class Spectator {
Account acc = null;
Room room = null;
Statistic stat = null;

ArrayList<Integer> holdedKeys = new ArrayList<>();
Character control = null;
Character follow = null;
int pos = 0;
int dirX=0;
int dirY=0;


public Spectator(Account a, Room r, int pos){
this.acc = a;
this.room = r;
this.pos = pos;
this.stat = new Statistic(pos);
}

public void followChar(Character c){
this.follow = c;
}

public void controlChar(Character c){
this.control = c.bind(this);
}

	
	public void send(JsonObject obj){
		try {
			this.acc.send(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getID(){
		return acc.getID()+" on position "+this.pos;
	}
	public void keyDown(int key){
		try{
			synchronized(this.holdedKeys){
			
				System.out.println(this.getID()+" Key "+key+" down");
				if(this.control != null && this.room.running && this.control.isControllable() && !this.isHolding(key)){
					System.out.println(this.getID()+" is able to control a Character");
					this.holdedKeys.add(key);
					
					switch(key){
					case 0: // Attacke
						if(this.control.shooter == null){
									ProjectileShooter ps = new ProjectileShooter(this.control);
									this.control.shooter = ps;
									ps.start();
						}
					break;
						
					case 1: // Hoch
					case 2: // Runter
					case 3: // Links
					case 4: // Rechts
						int[] dir = getMovingDir();
						this.dirX = dir[0];
						this.dirY = dir[1];
		
					break;
					
					case 5: // Benutzen
						this.control.use();
					break;
					case 6: // Nachladen
						new Thread(()->this.control.recharge()).start();
					break;
					
					
					
					}
					
				}
			}
		} catch(Exception e){
			e.printStackTrace();
				this.acc.sendError("Es trat ein Fehler bei der Verarbeitung des Tastendrucks auf");
		}

		
		
	}
	boolean isController(){
		return this.pos > 0;
	}
	public void keyUp(int key){
		try{
		synchronized(this.holdedKeys){
		System.out.println(this.getID()+" releases Key "+key);
		if(isHolding(key)){
			this.holdedKeys.remove(getHolding(key));
			if(key >= 1 && key <= 4){
				int[] dir = getMovingDir(); // [x,y]
				this.dirX = dir[0];
				this.dirY = dir[1];
			
			}
		} else 	System.out.println(this.getID()+" Key "+key+" wasn't marked as pressed ");
		}
		} catch(Exception e){
			e.printStackTrace();
			this.acc.sendError("Es trat ein Fehler bei der Verarbeitung des Tasten Loslassen auf");		
		}
	}
	public void releaseAllKeys(){
		//ArrayList<Integer> keys = new ArrayList<>();
		synchronized(this.holdedKeys){
		for(int key : new ArrayList<Integer>(this.holdedKeys))
			keyUp(key);
		}
		/*
		for(int key : keys)
			keyUp(key);
		*/
	}
	public void releaseMovementKeys(){
		keyUp(1);
		keyUp(2);
		keyUp(3);
		keyUp(4);
		
	}
	
	public int[] getMovingDir(){
		int[] dir = {0,0};
		if(isHolding(1))
			dir[1] = -1;
		else if(isHolding(2))
			dir[1] = 1;
		if(isHolding(3))
		dir[0] = -1;
		else if(isHolding(4))
		dir[0] = 1;
		
		return dir;
	}
	public void keyPressed(int key){
		
	}
	public boolean isHolding(int key){
		return getHolding(key) != -1;	
	}
	public int getHolding(int key){
		return this.holdedKeys.indexOf(key);	
	}
	public void rotate(int dir){
		if(this.isController() && this.control.isControllable())
			this.control.rotate(dir);
	}
	public boolean isLeader(){
		return this.room.creator == this.acc || this.room.creator == null; // Jeder ist Leader wenn dieser das Spiel verlassen hat
	}
	public JsonObject getJSON(){
		return Json.createObjectBuilder().add("pos",this.pos).add("name",this.acc.name).build();
	}
	public String getName(){
		return this.acc.name;
	}

}
