package surface;

import io.Renders;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import player.ElementStack;
import player.Pulses;
import universe.Element;
import universe.Planet;
import abstracts.Chemistry;
import abstracts.Fun;
import abstracts.Log;
import abstracts.Restriction;
import abstracts.Saver;

public class Cave extends Feature implements Pulses{
	private boolean pulsing;
	private List<ElementStack> contents;

	public Cave(double x, double y, Planet parent) {
		super(x, y, Feature.RADIUS, parent);
		if(parent != null){
			contents = createElementStacksForMining();
		}
		pulsing = false;
	}
	
	private List<ElementStack> createElementStacksForMining() {
		List<ElementStack> stacks = new ArrayList<>();
		int numStacks = Fun.rng(5) + 3;
		Element[] options = new Element[4];
		List<Restriction> r = new ArrayList<>(parent.getRestrictions());
		r.add(Restriction.CAVE);
		for(int i = 0; i < options.length; i++){
			options[i] = Chemistry.getElement(this, r);
		}
		for(int i = 0; i < numStacks; i++){
			stacks.add(new ElementStack(options[Fun.weightedCaseRng(6,4,2,1)], Fun.rng(4)+3));
		}
		return stacks;
	}

	@Override
	public void render(Renders r, int... info) {
		if(info[0] == 1){
			double drawWidth = pulsing? RADIUS*.8 : RADIUS;
			r.renderCircle(this, drawWidth, 0, Color.YELLOW, true);
			r.renderCircle(this, drawWidth, 0, Color.DARK_GRAY, false);
		}else{
			r.renderCircle(this, RADIUS/2, 0, Color.YELLOW, true);
		}
	}

	@Override
	public boolean interact(FeatureInteractor fi) {
		if(fi.usePlayerLifeSupport()){
			ElementStack reward = contents.remove(0);
			int remain = fi.addToShip(reward);
			if(remain > 0){
				Log.logInventoryFull(remain);
			}
			fi.addPulsing(this);
			pulsing = true;
			Log.logMine(parent, reward);
			if(contents.size() == 0){
				Log.logMineExhausted(parent);
				parent.removeFeature(this);
			}
			return true;
		}
		fi.changesTrue();
		Log.noResourcesForLifeSupport();
		return false;
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		String string = "cave " + delims[dIndex] + x + delims[dIndex] + y;
		for(ElementStack es : contents){
			string += delims[dIndex] + es.save(saver, delims, dIndex+1);
		}
		return string;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Cave cave = new Cave(scan.nextDouble(), scan.nextDouble(), null);
		cave.contents = new ArrayList<>();
		while(scan.hasNext()){
			cave.contents.add((ElementStack) new ElementStack(null, 0).load(saver, delims, dIndex+1, scan.next()));
		}
		scan.close();
		return cave;
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
