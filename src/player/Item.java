package player;

import io.Renderable;
import universe.Point;
import abstracts.Savable;

public interface Item extends Renderable, Savable{
	public static final int
			p100 = 90,
			p90 = (int) (.9*p100),
			p80 = (int) (.8*p100),
			p70 = (int) (.7*p100),
			p60 = (int) (.6*p100),
			p50 = (int) (.5*p100),
			p40 = (int) (.4*p100),
			p30 = (int) (.3*p100),
			p20 = (int) (.2*p100),
			p10 = (int) (.1*p100),
			p5 = (int) (.05*p100),
			p110 = (int) (1.1*p100);
	Item generateItemWithValueAt(Point p, double value);
	void wearAndTear();
}
