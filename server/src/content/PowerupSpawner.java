package content;

public class PowerupSpawner extends Thread {
	static int spawnsPerMinute = 3;
	static int timeBetweenSpawn = 60000/spawnsPerMinute;
	Room r;
	public PowerupSpawner(Room r){
		this.r = r;
	}
	@Override
	public void run(){
		try{
			while(r.running){
				this.r.addRandomPowerup();
				delay(timeBetweenSpawn);
			}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			System.out.println(this.r.getID()+": PowerupSpawner interrupted");
			this.r.powerupSpawner = null;
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
