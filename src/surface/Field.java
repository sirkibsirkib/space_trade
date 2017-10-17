package surface;

import io.Renders;

import java.awt.Color;
import java.util.Scanner;

import player.ElementStack;
import player.Pulses;
import universe.Element;
import universe.Planet;
import abstracts.Fun;
import abstracts.Log;
import abstracts.Saver;

public class Field extends Feature implements Pulses{
	private Element e;
	private int yield;
	private double prob;
	private boolean pulsing;

	public Field(double x, double y, Element e, Planet parent) {
		super(x, y, Feature.RADIUS, parent);
		this.e = e;
		yield = Fun.rng(3)+3;
		prob = 1;
		recalcWidth();
		pulsing = false;
	}
	
	private ElementStack getRewardStack() {
		ElementStack es = new ElementStack(e, yield);
		if(yield > 1 && Fun.chance(13 / yield)){
			yield -= 1;
		}else if(yield <= 1){
			prob += .2;
			if(!Fun.chance((int)(prob))){
				es = null;
			}
		}
		recalcWidth();
		return es;
	}

	private void recalcWidth() {
		radius = Feature.RADIUS*(yield/prob+6.0)/12;
	}

	@Override
	public void render(Renders r, int... info) {
		if(info[0] == 1){
			double drawRad = pulsing? radius*.8 : radius;
			r.renderCircle(this, drawRad, 0, Color.WHITE, true);
			r.renderCircle(this, drawRad, 0, Color.DARK_GRAY, false);
		}else{
			r.renderCircle(this, radius/2, 0, Color.WHITE, true);
		}
	}

	@Override
	public boolean interact(FeatureInteractor fi) {
		if(fi.usePlayerLifeSupport()){
			ElementStack reward = getRewardStack();
			fi.addToShip(reward);
			Log.logForage(parent, reward);
			pulsing = true;
			fi.addPulsing(this);
			return true;
		}
		fi.changesTrue();
		Log.noResourcesForLifeSupport();
		return false;
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "field" + delims[dIndex] + x + delims[dIndex] + y + delims[dIndex]
				+ saver.getKeyFor(e) + delims[dIndex] + yield + delims[dIndex] + prob;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Field field = new Field(scan.nextDouble(), scan.nextDouble(), (Element) saver.getObject(scan.nextLong()), null);
		field.yield = scan.nextInt();
		field.prob = scan.nextDouble();
		field.recalcWidth();
		scan.close();
		return field;
	}

	@Override
	public void pulseOff() {
		pulsing = false;
	}

	@Override
	public boolean isPulsingNow() {
		return pulsing;
	}
}
