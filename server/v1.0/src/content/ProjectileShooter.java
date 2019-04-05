package content;

public class ProjectileShooter extends Thread {
	Character c;
	Weapon w;
	public ProjectileShooter(Character c){
		this.c = c;
		this.w = this.c.weapon;
	}
	
	@Override	
	public void run(){
		if(this.w == null){
			this.c.punch();
			delay(this.c.punchtime);
		} else
		while((c.controller.isHolding(0) || !this.w.hold) && this.c.weapon == this.w && this.w.ammo > 0 && this.c.room.running){
				this.c.addShot();
				delay(this.w.chargetime);
				if(!this.w.hold) break;
		}
		c.shooter = null;
		
	}

	public static void delay(int t){
		try {
		    Thread.sleep(t);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}	

	}

}
