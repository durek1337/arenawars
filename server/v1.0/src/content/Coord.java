package content;

public class Coord {
	int x, y;
	public Coord(int x, int y){
		this.x = x;
		this.y = y;
		
	}
	
	public double distance(Coord c){
		return Math.sqrt(Math.pow(this.x-c.x, 2)+Math.pow(this.y-c.y, 2));
	}
	
	public Coord rotateBy(Coord m, int angle){ // Rotiere um Punkt m
		if(angle != 0){
			angle = -angle;
		int rx,ry;
		double cosinus = Math.cos(Math.toRadians(angle)), sinus = Math.sin(Math.toRadians(angle));
		
		
		rx = this.x-m.x;
		ry = this.y-m.y;
		this.x = (int) Math.round(cosinus*rx+sinus*ry)+m.x;
		this.y = (int) Math.round(-sinus*rx+cosinus*ry)+m.y;
		}
		return this;
	}
	@Override
	public String toString(){
		return "("+this.x+"|"+this.y+")";
	}

	
}
