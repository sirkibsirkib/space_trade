package universe;

import java.util.List;

import main.Ticker;
import abstracts.Fun;


public class Point {
	public static final Point ZERO = new Point(0, 0);
	
	protected double x;
	protected double y;
	
	public double getX() {
		return x;
	}	

	public double getY() {
		return y;
	}

	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public double distanceTo(Point o){
		return Math.sqrt(Fun.sqr(o.x-x) + Fun.sqr(o.y-y));
	}
	
	public double distanceTo(double x, double y){
		return Math.sqrt(Fun.sqr(this.x-x) + Fun.sqr(this.y-y));
	}
	
	public double manhattan(Point c){
		return Math.abs(c.getX()-x) + Math.abs(c.getY()-y);
	}
	
	public double manhattan(double x, double y){
		return Math.abs(x-this.x) + Math.abs(y-this.y);
	}

	public Point dirOffset(double rot, double dist) {
		return new Point(x + (Math.cos(rot) * dist), y + (Math.sin(rot) * dist));		
	}
	
	public Point offset(double x, double y){
		return new Point(this.x+x, this.y+y);
	}

	public void print() {
		System.out.println(" coord: " + x + "  " + y);
	}
	
	public boolean samePointAs(Point other){
		return other.x == x && other.y == y;
	}

	public boolean outsideUniverse() {
		return distanceTo(ZERO) > 1;
	}

	public static Point randomUniversePoint() {
		Point x = null;
		do{
			Ticker.spin();
			x = new Point(Fun.rdg(2)-1, Fun.rdg(2)-1);
		}while(x.outsideUniverse());
		return x;
	}
	
	@Override
	public Point clone(){
		return new Point(x, y); 
	}

	public Point validOffset(double distance) {
		if(outsideUniverse() || distance >= 1){
			throw new Error();
		}
		Point p = null;
		do{
			p = dirOffset(Fun.rdg(Math.PI*2), distance);
			Ticker.spin();
		}while(p.outsideUniverse());
		return p;
	}
	

	
	public <E extends Point> E nearestOther(List<E> c){
		double dist = Double.MAX_VALUE;
		E other = null;
		for(E o : c){
			double nextDist = distanceTo(o);
			if(nextDist < dist && o != this){
				dist = nextDist;
				other = o;
			}
		}
		return other;
	}

	public double rectDistanceTo(Point o) {
		return Fun.max(Math.abs(x-o.x), Math.abs(y-o.y));
	}
	
	public double angleMeasure(Body b){
		return b.getRadius()*2/distanceTo(b);
	}
	
	public double directionTo(Point p){
		double angle = Math.atan2(p.y-y, p.x-x);
	    if(angle < 0){
	        angle += Math.PI*2;
	    }
	    return angle;
	}
}
