package content;

import java.util.ArrayList;
import java.util.Map.Entry;

public class CharacterMover extends Thread {
	public static int timeBetweenStep = 50;
	public static int distancePerSecond = 250;
	public static int stepsPerSecond = 1000/timeBetweenStep;
	public static double distancePerStep = distancePerSecond/stepsPerSecond;
	public static double kath = (int) Math.round(distancePerStep*Math.sqrt(0.5));
	
	Room r;
	ArrayList<Rectangle> obstacles;
	long latency = 0;
	
	public CharacterMover(Room r){
		this.r = r;
		this.obstacles = new ArrayList<>();
		
		synchronized(r.map.gameobjects){
		for(Entry<Rectangle,Gameobject> e : r.map.gameobjects.entrySet()){
			if(!e.getValue().passable) addObstacle(e.getKey());
		}
		}
		
	}
	public void addObstacle(Rectangle r){
		this.obstacles.add(r);
	}

	
	@Override
	public void run(){ // Bewege Charakter solange in die Richtung bis er nicht mehr laufen soll (Taste loslässt)
		//double step = Math.sqrt(2);
		try{
		while(this.r.running){
			long time = System.currentTimeMillis();
			synchronized(this.r.chars){
				int newX=0, newY=0;

				double d = 0;
				for(Character c : this.r.chars){
					synchronized(c){
					if(c.controller.dirX != 0 || c.controller.dirY != 0){

						d = (c.controller.dirX != 0 && c.controller.dirY != 0) ? kath : distancePerStep;
						d *= c.getSpeed();

						newX = c.rec.x+(int)Math.round(c.controller.dirX*d);
						newY = c.rec.y+(int)Math.round(c.controller.dirY*d);
						
						Rectangle newrec = c.rec.clone().setCoord(newX, newY).setJustifiedSize(30, 30).createPolygon();
						
						int newdirX = 0;
						int newdirY = 0;
						
						if(!isPassable(newrec)){
							if(c.controller.dirX != 0 && c.controller.dirY != 0){
								Rectangle newrecX = c.rec.clone().setCoord(newX, c.rec.y).setJustifiedSize(30, 30).createPolygon();
								Rectangle newrecY = c.rec.clone().setCoord(c.rec.x, newY).setJustifiedSize(30, 30).createPolygon();

								
								if(isPassable(newrecX))
									newdirX = c.controller.dirX;
								else if(isPassable(newrecY))
									newdirY = c.controller.dirY;
							}
						} else {
							newdirX = c.controller.dirX;
							newdirY = c.controller.dirY;
						}
						
						if(c.dirX != newdirX || c.dirY != newdirY) c.changeDirection(newdirX, newdirY);
						
						if(c.dirX != 0 || c.dirY != 0) // Solange eine Richtung ungleich (0|0) vorhanden
						{
							d = (c.dirX != 0 && c.dirY != 0) ? kath : distancePerStep;
							d *= c.getSpeed();
							
							if(c.dirX != c.controller.dirX || c.dirY != c.controller.dirY){
							newX = c.rec.x+(int)Math.round(c.dirX*d);
							newY = c.rec.y+(int)Math.round(c.dirY*d);
							}
							
							c.rec.setCoord(newX, newY).createPolygon();							
						}
						

					} else if(c.dirX != 0 || c.dirY != 0) c.changeDirection(0, 0);
					}
					
				}
			}
			
			this.latency = System.currentTimeMillis()-time;
		delay((int)Math.round(timeBetweenStep/this.r.movementspeed));
		}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			this.r.charMover = null;
			System.out.println(this.r.getID()+": CharacterMover interrupted");			
		}
		
	}
	
	boolean isPassable(Rectangle newrec){
		boolean allowed = true;
		for(Rectangle r : this.obstacles){
			if(newrec.isColliding(r) ){
				allowed = false;
				break;
			}
		}
		if(allowed && !newrec.isInside(this.r.map.rec))
		allowed = false;
		return allowed;
	}
	public static void delay(int t){
			try {
			    Thread.sleep(t);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}	

		}
			
}
