package universe;

import io.Renderable;
import io.Renders;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import player.Ship;
import surface.Feature;
import abstracts.Chemistry;
import abstracts.Fun;
import abstracts.NameGen;
import abstracts.Restriction;
import abstracts.Savable;
import abstracts.Saver;

public class Star extends Body implements Renderable, Savable{
	public static final double MIN_PLANET_SIZE_RATIO = 0.04;
	public static final double MAX_PLANET_SIZE_RATIO = 0.18;
	public static final double
			SYSTEM_SUN_WIDTH_RATIO = 14,
			MIN_RADIUS = 0.00002,
			MAX_RADIUS = 0.0002,
			AVG_RADIUS = MIN_RADIUS + (MAX_RADIUS-MIN_RADIUS)/2,
			INTER_BODY_BUFFER = 0.00005;
	private Element e;
	private List<Planet> planets;
	private String name;
	private List<Restriction> restrictions;
	
	public Star(double x, double y, double radius, String name){
		super (x, y, radius);
		this.name = name;
		setupMyRestrictions(radius);
		List<Restriction> r = new ArrayList<>(restrictions);
		r.add(Restriction.IS_STAR);
		e = Chemistry.getElement(this, r);
	}

	private void setupMyRestrictions(double radius) {
		restrictions = new ArrayList<>();
		if(radius < MAX_RADIUS/3){
			restrictions.add(Restriction.STAR_SMALL);
		}else if(radius > MAX_RADIUS*2/3){
			restrictions.add(Restriction.STAR_LARGE);
		}else{
			restrictions.add(Restriction.STAR_MED);
		}
	}

	private void generatePlanets() {
		planets = new ArrayList<>();
		int numPlanets = Fun.rng(Fun.rng((int) (12/Star.MAX_RADIUS*radius))) + Fun.rng(5);
		int i = 0;
		while(i < numPlanets){
			double planetRadius = radius*(Fun.rdg(MAX_PLANET_SIZE_RATIO - MIN_PLANET_SIZE_RATIO)
					+ MIN_PLANET_SIZE_RATIO) + Feature.RADIUS*4 + Ship.RADIUS*2;
			double planetDist = Fun.skewRdg(radius*SYSTEM_SUN_WIDTH_RATIO, .5);
			if(Fun.chance(2)){
				planetDist /= 2;
			}
			Point b = dirOffset(Fun.rdg(Math.PI*2), 2*radius+planetRadius + planetDist);
			if(!b.outsideUniverse() && spotIsFree(b, planetRadius)){
				boolean planetUninhabited = Fun.prob(distanceTo(ZERO)+.1);
				planets.add(new Planet(b.getX(), b.getY(), name, planetRadius, i, this, planetUninhabited));
				i++;
			}
		}
	}
	
	private boolean spotIsFree(Point point, double pointRadius){
		if(distanceTo(point) < pointRadius + radius + INTER_BODY_BUFFER){
			return false;
		}
		for(Planet p : planets){
			if(point.distanceTo(p) < pointRadius + p.getRadius() + INTER_BODY_BUFFER){
				return false;
			}
		}
		return true;
	}

	public Element getElement() {
		return e;
	}
	
	public boolean planetsGenerated(){
		return planets != null;
	}

	public List<Planet> getPlanets() {
		if(planets == null){
			generatePlanets();
		}
		return planets;
	}
	
	public String getName(){
		return name;
	}
	
	public void render(Renders r, int... info) {
		Color renderCol = info[1] == 1? e.getCol() : Color.DARK_GRAY;
		if(info[1] == -1){
			return;
		}
		r.renderCircle(this, radius, 3 + info[1]*2, renderCol, true);
		if(info[0] == 1){
			r.renderText(new Point(x, y + radius/2), name, Color.WHITE, false);
			r.renderCircle(this, radius*1.1, 3, renderCol, false);
		}
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		long starKey = saver.freeKey();
		saver.mapObject(starKey, this);
		String string = "" + x + delims[dIndex] + y + delims[dIndex] + 
				radius + delims[dIndex] + name + delims[dIndex] +
				starKey + delims[dIndex] + saver.getKeyFor(e);
		if(planets != null){
			for(int i = 0; i < planets.size(); i++){
				string += delims[dIndex] + planets.get(i).save(saver, delims, dIndex+1);
			}
		}
		return string;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(""+delims[dIndex]);
		
		Star star = new Star(scan.nextDouble(), scan.nextDouble(), scan.nextDouble(), scan.next());
		saver.mapObject(scan.nextLong(), this);
		NameGen.addGivenFromLoad(star.getName());
		Element el = (Element) saver.getObject(scan.nextLong());
		star.e = el;
		star.planets = new ArrayList<>();
		while(scan.hasNext()){
			Planet next = new Planet(0, 0, null, 0, 0, null, false);
			next = (Planet) next.load(saver, delims, dIndex+1, scan.next());
			star.planets.add(next);
			next.setLightAndShadow(star);
		}
		if(star.planets.size() == 0){
			star.planets = null;
		}
		scan.close();
		return star;
	}

	public List<Restriction> getRestrictions() {
		return restrictions;
	}
}
