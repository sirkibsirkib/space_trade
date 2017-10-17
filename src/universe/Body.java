package universe;

public abstract class Body extends Point {
	protected double radius;
	public Body(double x, double y, double radius){
		super(x, y);
		this.radius = radius;
	}

	public double getRadius() {
		return radius;
	}
	
	public double distanceDiameterRatio(Point p){
		return distanceTo(p) / (2*radius);
	}
}
