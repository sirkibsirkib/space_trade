package surface;

import io.Renderable;
import universe.Body;
import universe.Planet;
import abstracts.Savable;

public abstract class Feature extends Body implements Renderable, Savable{
	protected Planet parent;
	
	public Feature(double x, double y, double radius, Planet parent) {
		super(x, y, radius);
		this.parent = parent;
	}
	public void setParent(Planet parent){
		this.parent = parent;
	}
	public static final double RADIUS = 0.0000019;
	public abstract boolean interact(FeatureInteractor fi);
}
