package content;

import java.util.ArrayList;
import java.util.Map.Entry;

import javax.json.Json;

public class ProjectileMover extends Thread  {
	public static int timeBetweenStep = 50;
	Room r;
	ArrayList<Rectangle> obstacles;
	public ProjectileMover(Room r){
		this.r = r;
		this.obstacles = new ArrayList<>();
		
		synchronized(r.map.gameobjects){
		for(Entry<Rectangle,Gameobject> e : r.map.gameobjects.entrySet()){
			if(!e.getValue().interleaving) addObstacle(e.getKey());
		}
		}
		
	}
	public void addObstacle(Rectangle r){
		this.obstacles.add(r);
	}
	@Override
	public void run(){
		try{
		while(this.r.running){
			synchronized(this.r.chars){
				for(Character c : this.r.chars){
					ArrayList<Integer> collided = new ArrayList<>();
					synchronized(c.shots){
						for(Projectile p : c.shots.values())
							if(p.isColliding()){
								collided.add(p.id);
							}
						for(int i : collided){
								c.shots.remove(i);
								this.r.sendToAll(Json.createObjectBuilder().add("type",5).add("stype", 3).add("pos", c.controller.pos).add("id", i).build());
							}
					}
					for(Projectile p : c.shots.values()) p.move();

				}
			}
			delay(timeBetweenStep);
		}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			this.r.projMover = null;
			System.out.println(this.r.getID()+": ProjectileMover interrupted");				
		}
		
	}
	public static void delay(int t){
		try {
		    Thread.sleep(t);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}	


}
}
