package content;
import java.awt.Point;
import java.awt.Polygon;


public class Rectangle {
int id,x,y,w,h,angle;
Polygon pol;
Coord s, m; // Startpunkt oben links und Rotationspunkt

public Rectangle(Coord s, Coord m, int w, int h, int angle){
this.s = s;
this.x = s.x;
this.y = s.y;
this.w = w;
this.h = h;
setMid(m);
setAngle(angle);
}

public Coord getMid(){
	return new Coord(this.x+this.w/2,this.y+this.h/2);
}

boolean isInside(int x, int y){
return x >= this.x && x <= this.x+this.w && y>=this.y && y <= this.y+this.h;
}
boolean isInside(Rectangle r){
return r.isInside(this.x,this.y) && r.isInside(this.x+this.w,this.y+this.h) && r.isInside(this.x+this.w,this.y) && r.isInside(this.x,r.y+this.h);
}

public Rectangle setAngle(int angle){
	this.angle = angle;
	createPolygon();
	return this;
}

public Rectangle setCoord(int x, int y){
this.x = x;
this.y = y;
this.createPolygon();
return this;
}
public Rectangle addId(int id){
	this.id = id;
	return this;
}

public Coord getCoord(){
	return new Coord(this.x,this.y);
}
/*
boolean isColliding(Rectangle r){
boolean b = false;	
// Befindet sich ein Eckpunkt innerhalb des anderen Rechtecks?
b = b || isInside(r.x,r.y) || isInside(r.x+r.w,r.y) || isInside(r.x,r.y+r.h) || isInside(r.x+r.w,r.y+r.h);
b = b || r.isInside(this.x,this.y) || r.isInside(this.x+this.w,this.y) || r.isInside(this.x,this.y+this.h) || r.isInside(this.x+this.w,this.y+this.h);

	return b;
}
*/
boolean isColliding(Rectangle r){
	Point p;
	Polygon p1 = r.pol;
	Polygon p2 = this.pol;
	for(int i = 0; i < p2.npoints;i++)
	{
		p = new Point(p2.xpoints[i],p2.ypoints[i]);
		if(p1.contains(p)) return true;
	}
	for(int i = 0; i < p1.npoints;i++){
		p = new Point(p1.xpoints[i],p1.ypoints[i]);
		if(p2.contains(p)) return true;
		}
	return false;
}
public Rectangle clone(){
return new Rectangle(new Coord(this.x,this.y),this.m,this.w,this.h,this.angle).addId(this.id);
}
public void createPolygon(){
	createPolygon(this.m);
}
public void setMid(Coord m){
	this.m = m;
}
public Rectangle setSize(int w, int h){
	this.w = w;
	this.h = h;
	return this;
}
public void createPolygon(Coord m){
	Coord a,b,c,d;
	if(m == null) m = getMid();
	a = new Coord(this.x,this.y).rotateBy(m, this.angle);
	b = new Coord(this.x+this.w,this.y).rotateBy(m, this.angle);
	c = new Coord(this.x+this.w,this.y+this.h).rotateBy(m, this.angle);
	d = new Coord(this.x,this.y+this.h).rotateBy(m, this.angle);
	
	int[] xpoints = {a.x,b.x,c.x,d.x};
	int[] ypoints = {a.y,b.y,c.y,d.y};
	
	this.pol = new Polygon(xpoints,ypoints,4);
	
}
}

