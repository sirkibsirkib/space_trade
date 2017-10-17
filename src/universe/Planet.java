package universe;

import io.Renderable;
import io.Renders;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.Ticker;
import player.Ship;
import surface.Cave;
import surface.Depot;
import surface.Feature;
import surface.Field;
import surface.Shipyard;
import surface.Vault;
import abstracts.Chemistry;
import abstracts.Fun;
import abstracts.Restriction;
import abstracts.Savable;
import abstracts.Saver;

public class Planet extends Body implements Renderable, Savable{
	public static final double MAX_RADIUS = Star.MAX_RADIUS*Star.MAX_PLANET_SIZE_RATIO;
	
	private String name;
	List<Restriction> restrictions;
	private Element e;
	private int index;
	private List<Feature> features;
	private Point shadowCenter;
	private Star parentStar;
	private boolean uninhabited;
	private double shadowRad;
	
//	private RingSystem ringSystem;
	
	public Planet(double x, double y, String name, double radius, int index, Star parentStar, boolean uninhabited) {
		super(x, y, radius);
		this.name = name;
		this.index = index;
		this.uninhabited = uninhabited;
		if(parentStar != null){
			setLightAndShadow(parentStar);
			setupRestrictions();
			List<Restriction> r = new ArrayList<>(restrictions);
			r.add(Restriction.ON_PLANET_SURFACE);
			e = Chemistry.getElement(this, r);
//			ringSystem = new RingSystem(Fun.rng(5)+6, 0, .2, radius*1.3, radius*1.6, Color.WHITE, e.getCol(), e.getDarkerCol());
		}
	}

	public void setLightAndShadow(Star parentStar) {
		this.parentStar = parentStar;
		double ang = angleMeasure(parentStar);
		ang = Math.pow(ang/2, .25);
		shadowCenter = dirOffset(directionTo(parentStar),radius*(1-ang));
		shadowRad = ang;
	}

	private void setupRestrictions() {
		restrictions = new ArrayList<>(parentStar.getRestrictions());
		if(radius > MAX_RADIUS * .6){
			restrictions.add(Restriction.PLANET_LARGE);
		}else if(radius < MAX_RADIUS * .3){
			restrictions.add(Restriction.PLANET_SMALL);
		}else{
			restrictions.add(Restriction.PLANET_MED);
		}
		if(angleMeasure(parentStar) < .22){
			restrictions.add(Restriction.STAR_ANGLE_SMALL);
		}else if(angleMeasure(parentStar) > .62){
			restrictions.add(Restriction.STAR_ANGLE_LARGE);
		}else{
			restrictions.add(Restriction.STAR_ANGLE_MED);
		}
		if(uninhabited){
			restrictions.add(Restriction.UNINHABITED_PLANET);
		}else{
			restrictions.add(Restriction.INHABITED_PLANET);
		}
	}

	public Element getElement() {
		return e;
	}
	
	public List<Feature> getFeatures(){
		if(features == null){
			generateFeatures();
		}
		return features;
	}

	private void generateFeatures() {
		features = new ArrayList<>();
		int numFeatures = (int)(Math.pow(Fun.rdg(radius*900_000), .7)+Fun.rng(3));
		
		addFeatures : 
		for(int i = 0; i < numFeatures; i++){
			Point p = getFreeFeatureSpot();
			if(p == null){
				break addFeatures;
			}
			double fX = p.getX();
			double fY = p.getY();
			
			int switchCode = uninhabited? Fun.weightedCaseRng(0, 2, 6, 0, 0)
					: Fun.weightedCaseRng(4, 2, 6, 1, 2);
			switch(switchCode){
			case 0:	features.add(new Depot(fX, fY, this)); break;
			case 1: features.add(new Cave(fX, fY, this)); break;
			case 2: features.add(new Field(fX, fY, e, this)); break;
			case 3: features.add(new Vault(fX, fY, this)); break;
			case 4: features.add(new Shipyard(fX, fY, this)); break;
			}
		}
	}
	
	private Point getFreeFeatureSpot(){
		Point p = null;
		int j = 0;
		do{
			Ticker.spin();
			j++;
			if(j > 10){
				return null;
			}
			p = dirOffset(Fun.rdg(Math.PI*2), Fun.skewRdg(radius - Feature.RADIUS, 3));
		}while(!pointFreeForFeature(p));
		return p;
	}
	
	private boolean pointFreeForFeature(Point p){
		if(distanceTo(p) < Ship.RADIUS + Feature.RADIUS){
			return false;
		}
		if(features == null){
			return true;
		}
		for(Feature f : features){
			if(p.distanceTo(f) < Feature.RADIUS*3){
				return false;
			}
		}
		return true;
	}
	
	
	public String getName(){
		return name;
	}

	public int getIndex() {
		return index;
	}

	public boolean featuresGenerated() {
		return features != null;
	}
	
	public void addRandomVault(){
		while(true){
			Point p = getFreeFeatureSpot();
			if(p != null){
				features.add(new Vault(p.getX(), p.getY(), this));
				return;
			}
		}
	}

	@Override
	public void render(Renders r, int... info) {
		//rings
		
//		double ringRot = 0;
		
//		ringSystem.renderBehind(r);
		r.renderCircle(this, radius, 2, e.getDarkerCol(), true);
		r.renderCircle(shadowCenter, radius*shadowRad, 2, e.getCol(), true);
//		r.renderEllipse(this, radius*2, radius*.4, Color.RED, false);
//		r.renderArc(this, radius, 0, 180, 0, e.getCol(), true);
	}

	public void removeFeature(Feature feat) {
		features.remove(feat);
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		String string = "" + x + delims[dIndex] + y +delims[dIndex] + name
				+ delims[dIndex] + radius + delims[dIndex] + index
				+ delims[dIndex] + saver.getKeyFor(parentStar)
				+ delims[dIndex] + (uninhabited?1:0)
				+ delims[dIndex] + saver.getKeyFor(e);
		string += delims[dIndex] + Restriction.saveRestrictions(restrictions, saver, delims, dIndex+1);
		if(features != null){
			for(int i = 0; i < features.size(); i++){
				string += delims[dIndex];
				string += features.get(i).save(saver, delims, dIndex+1);
			}
		}
		return string;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		//TODO
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		Planet p = new Planet(scan.nextDouble(), scan.nextDouble(), scan.next(), scan.nextDouble(),
				scan.nextInt(), (Star) saver.getObject(scan.nextLong()), scan.nextInt() == 1);
		p.e = (Element) saver.getObject(scan.nextLong());
		p.restrictions = Restriction.loadRestrictions(saver, delims, dIndex+1, scan.next());
		p.features = new ArrayList<>();
		while(scan.hasNext()){
			Feature f = saver.loadFeature(scan.next(), dIndex+1);
			f.setParent(p);
			p.features.add(f);
		}
		if(p.features.size() == 0){
			p.features = null;
		}
		scan.close();
		return p;
	}

	public Star getParentStar() {
		return parentStar;
	}

	public List<Restriction> getRestrictions() {
		return restrictions;
	}

	public boolean isUninhabited() {
		return uninhabited;
	}
	
//	private Planet outer(){
//		return this;
//	}
	
//	private class RingSystem{
//		private double[] ringDist;
//		private Color[] ringCol;
//		private double rotation;
//		private double angle;
//		
//		RingSystem(int numRings, double rotation, double angle, double minDist, double maxDist, Color... ringColors){
//			ringDist = new double[numRings];
//			ringCol = new Color[numRings];
//			this.rotation = rotation;
//			this.angle = angle;
//			for(int i = 0; i < numRings; i++){
//				ringDist[i] = Fun.rdg(maxDist-minDist) + minDist;
//				ringCol[i] = ringColors[Fun.rng(ringColors.length)];
//			}
//		}
//		
//		void renderBehind(Renders r){
//			for(int i = 0; i < ringDist.length; i++){
//				r.renderArc(outer(), ringDist[i], rotation, 180, ringDist[i]*angle, ringCol[i], false);
//			}
//		}
//	}
}
