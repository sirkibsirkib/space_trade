	package universe;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.Ticker;
import surface.Feature;
import surface.Vault;
import abstracts.Fun;
import abstracts.NameGen;
import abstracts.Savable;
import abstracts.Saver;

public class Universe implements Savable {
	public static final int NUM_STARS = 2_500;
	public static final double AU = Star.AVG_RADIUS * Star.SYSTEM_SUN_WIDTH_RATIO; 
	
	public List<Star> stars;
	
	public Universe(){
		stars = new ArrayList<>();
		int i = 0;
		while(i < NUM_STARS){
			double rot = Fun.rdg(2*Math.PI);
			double dist = Math.pow(Fun.rdg(1), 1.5)*.8 + Fun.rdg(.07);
			if(Fun.chance(20)){
				dist /= 25;
			}
			Point starPoint = new Point(Math.sin(rot)*dist, Math.cos(rot)*dist);
			double starRad = Star.MIN_RADIUS + Fun.rdg(Star.MAX_RADIUS - Star.MIN_RADIUS);
			
			if(canPutStarAt(starPoint, starRad)){
				stars.add(new Star(starPoint.getX(), starPoint.getY(), starRad,
						NameGen.newName(Fun.rng(Integer.MAX_VALUE))));
				i++;
			}
		}
	}
	
	private boolean canPutStarAt(Point starPoint, double starRad){
		for(Star s : stars){
			if(s.distanceTo(starPoint) < 1.3*(starRad+s.radius)*Star.SYSTEM_SUN_WIDTH_RATIO){
				return false;
			}
		}
		return true;
	}
	
	public List<Star> getStars(){
		return stars;
	}
	
	public Planet startPosition(){
		double dist = 0.001;
		while(true){
			Star central = Point.ZERO.dirOffset(Fun.rdg(Math.PI*2), Fun.rdg(dist)).nearestOther(stars);
			if(central.getPlanets().size() >= 4){
				for(Planet p : central.getPlanets()){
					Ticker.spin();
					if(p.getFeatures().size() >= 4 && !p.isUninhabited()){
						p.addRandomVault();
						return p;
					}
				}
			}
			dist += 0.001;
		}
	}

	public Vault getGoalVault() {
		while(true){
			Star distant = Point.ZERO.dirOffset(Fun.rdg(Math.PI*2), Fun.rdg(.2) + .7).nearestOther(stars);
			for(Planet p : distant.getPlanets()){
				Ticker.spin();
				for(Feature feat : p.getFeatures()){
					if(feat instanceof Vault){
						return (Vault) feat;
					}
				}
			}
		}
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		String string = "";
		for(int i = 0; i < stars.size(); i++){
			if(i > 0){
				string += delims[dIndex];
			}
			string += stars.get(i).save(saver, delims, dIndex+1);
		}
		return string;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Universe uni = new Universe();
		uni.stars = new ArrayList<>();
		while(scan.hasNext()){
			Star next = (Star) new Star(0,0,0,null).load(saver, delims, dIndex+1, scan.next());
			uni.stars.add(next);
		}
		scan.close();
		return uni;
	}
	
	public List<Star> starsWithSystemsAt(Point p){
		List<Star> result = new ArrayList<>();
		for(Star s : stars){
			if(s.distanceTo(p) <= 1.3*s.radius*Star.SYSTEM_SUN_WIDTH_RATIO){
				result.add(s);
			}
		}
		return result;
	}

	public void fullyGenerate(Point point, double radius) {
		for(Star s : stars){
			if(point.distanceTo(s) < radius){
				for(Planet p : s.getPlanets()){
					p.getFeatures();
				}
			}
		}
	}
	
	public static String auDistanceString(double d){
		return String.format("%1$,.1f¤", d/AU);
	}
}
