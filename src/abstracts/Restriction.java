package abstracts;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public enum Restriction implements Savable{
	IS_STAR(0),
	ON_PLANET_SURFACE(0),
	CAVE (0),
	
	STAR_ANGLE_SMALL (1),
	STAR_ANGLE_MED (1),
	STAR_ANGLE_LARGE (1),
	
	STAR_SMALL (2),
	STAR_MED (2),
	STAR_LARGE (2),
	
	PLANET_SMALL (3),
	PLANET_MED (3),
	PLANET_LARGE (3),
	
	UNINHABITED_PLANET (4),
	INHABITED_PLANET (4);
	
	private static final int NO_CATEGORY = Integer.MAX_VALUE;
	private int category;
	
	Restriction(){
		category = NO_CATEGORY;
	}
	
	Restriction(int category){
		this.category = category;
	}
	
	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		for(Restriction r : values()){
			if(r == this){
				return r.ordinal() + "";
			}
		}
		throw new Error();
	}
	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		int toFind = scan.nextInt();
		scan.close();
		for(Restriction r : values()){
			if(r.ordinal() == toFind){
				return this;
			}
		}
		throw new Error();
	}
	
	public static List<Restriction> loadRestrictions(Saver saver, char[] delims, int dIndex, String data){
		List<Restriction> results = new ArrayList<>();
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		while(scan.hasNext()){
			results.add((Restriction) CAVE.load(saver, delims, dIndex+1, scan.next()));
		}
		scan.close();
		return results;
	}
	
	public static String saveRestrictions(List<Restriction> restrictions, Saver saver, char[] delims, int dIndex){
		String result = "";
		for(int i = 0; i < restrictions.size(); i++){
			if(i > 0){
				result += delims[dIndex];
			}
			result += restrictions.get(i).save(saver, delims, dIndex+1);
		}
		return result;
	}
	public static List<Restriction> randomSetWithProb(double prob, boolean overlapAllowed) {
		prob = Math.abs(prob) + 0.000_000_1;
		List<Restriction> results = new ArrayList<>();
		List<Integer> categoriesTaken = new ArrayList<>();
		for(Restriction r : values()){
			if(Fun.prob(prob) &&
					(r.category == NO_CATEGORY || !categoriesTaken.contains(r.category))){
				results.add(r);
				if(r.category != NO_CATEGORY && !overlapAllowed){
					categoriesTaken.add(r.category);
				}
			}
		}
		return results;
	}
	
	public static List<Restriction> itemGenGrabBag(){
		List<Restriction> x = randomSetWithProb(0.07, true);
		if(Fun.chance(3) && !x.contains(ON_PLANET_SURFACE)){
			x.add(ON_PLANET_SURFACE);
		}
		return x;
	}
}
