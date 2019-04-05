package content;

import java.util.ArrayList;
import java.util.Map.Entry;

public class CharacterMover extends Thread {
	public static int timeBetweenStep = 50;
	public static int distancePerSecond = 200;
	public static int stepsPerSecond = 1000/timeBetweenStep;
	public static double distancePerStep = distancePerSecond/stepsPerSecond;
	public static double kath = (int) Math.round(distancePerStep*Math.sqrt(0.5));
	
	Room r;
	ArrayList<Rectangle> obstacles;
	
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
			synchronized(this.r.chars){
				for(Character c : this.r.chars){
					if(c.dirX != 0 || c.dirY != 0) // Solange eine Richtung ungleich (0|0) vorhanden
					{
						double d = (c.dirX != 0 && c.dirY != 0) ? kath : distancePerStep;
						d = d*c.getSpeed();
						//System.out.println("SPEED: "+c.getSpeed()+" d: "+kath +"/"+distancePerStep+" Dist: "+d);
						int newX = c.rec.x+(int)Math.round(c.dirX*d);
						int newY = c.rec.y+(int)Math.round(c.dirY*d);
						
						Rectangle newrec = c.rec.clone().setCoord(newX, newY);
						
						
						Rectangle newrecX = c.rec.clone().setCoord(newX, c.rec.y);
						Rectangle newrecY = c.rec.clone().setCoord(c.rec.x, newY);
						
						if(isPassable(newrec))
						c.rec.setCoord(newX, newY);
						else if(isPassable(newrecX)) c.changeDirection(c.dirX,0);
						else if(isPassable(newrecY)) c.changeDirection(0,c.dirY);
						else c.changeDirection(0, 0);



							
					}

				}
			}
		delay(timeBetweenStep);
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
