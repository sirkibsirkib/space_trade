package universe;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.Ticker;
import abstracts.Fun;
import abstracts.Restriction;
import abstracts.Savable;
import abstracts.Saver;

public class Element implements Savable{
	
	private List<Restriction> prohibitedRestrictions, requiredRestrictions;
	private List<Point> loci;
	private Color col, darkerCol;
	private String name, symbol;
	private int shapeCode;
	private double locusRadius;
	
	public Element(String name, String symbol, Color col, List<Restriction> prohibitedRestrictions, List<Restriction> requiredRestrictions, Point... givenLoci){
		this.col = col;
		if(col != null){
			darkerCol = darken(col);
		}
		this.name = name;
		this.symbol = symbol;
		freshLoci(givenLoci);
		if(symbol != null){
			calcShapeCode();
		}
		this.prohibitedRestrictions = prohibitedRestrictions;
		this.requiredRestrictions = requiredRestrictions;
		if(prohibitedRestrictions != null && requiredRestrictions != null){
			locusRadius = .09 + requiredRestrictions.size()*0.022 + prohibitedRestrictions.size()*0.012;
		}
		
	}
	
	
	private Color darken(Color color) {
		return new Color((int)(color.getRed()*.6), 
				(int)(color.getGreen()*.6),
				(int)(color.getBlue()*.6));
	}


	public String getName() {
		return name;
	}

	public Color getCol() {
		return col;
	}

	public int suitabilityCode(Point c, List<Restriction> restrictions, double graceDistance) {
		if(!acceptable(restrictions)){
			return 1;
		}
		for(int i = 0; i < loci.size(); i++){
			Point locus = loci.get(i);
			double dist = locus.distanceTo(c);
			if(dist < locusRadius){
				return 0;
			}
		}
		return 2;
	}

	private boolean acceptable(List<Restriction> restrictions) {
		int balance = 0;
		if(restrictions == null){
			return true;
		}
		for(Restriction r : restrictions){
			if(prohibitedRestrictions.contains(r)){
				balance -= 5;
			}else{
				balance += 2;
			}
			if(requiredRestrictions.contains(r)){
				balance += 6;
			}else{
				balance -= 1;
			}
		}
		return balance + Fun.rng(2) >= 0;
	}


	public void freshLoci(Point... preassigned) {
		loci = new ArrayList<>();
		for(Point p : preassigned){
			if(!p.outsideUniverse()){
				loci.add(p);
			}
		}
		while(loci.size() < 3){
			Ticker.spin();
			loci.add(Point.randomUniversePoint());
		}
	}
	public List<Point> getLoci() {
		return loci;
	}

	public Color getDarkerCol(){
		return darkerCol;
	}

	public void addRandomLoci(int numAdditionalLoci) {
		for(int i = 0; i < numAdditionalLoci; i++){
			loci.add(Point.randomUniversePoint());
		}
	}


	public String getSymbol() {
		return symbol;
	}


	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		String string = name + delims[dIndex] + symbol + delims[dIndex] +
				col.getRed() + delims[dIndex] +  col.getGreen() + delims[dIndex] + col.getBlue() +
				delims[dIndex] + Restriction.saveRestrictions(prohibitedRestrictions, saver, delims, dIndex+1) + 
				delims[dIndex] + Restriction.saveRestrictions(requiredRestrictions, saver, delims, dIndex+1);
		for(int i = 0; i < loci.size(); i++){
			string += "" + delims[dIndex] + loci.get(i).getX() + delims[dIndex] +  loci.get(i).getY();
		}
		return string;
	}


	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		String sName = scan.next();
		String sSymbol = scan.next();
		Element el = new Element(sName, sSymbol, new Color(scan.nextInt(), scan.nextInt(), scan.nextInt()),
				Restriction.loadRestrictions(saver, delims, dIndex+1, scan.next()),
				Restriction.loadRestrictions(saver, delims, dIndex+1, scan.next()));
		el.loci = new ArrayList<>();
		while(scan.hasNext()){
			loci.add(new Point(scan.nextDouble(), scan.nextDouble()));
		}
		el.calcShapeCode();
		scan.close();
		return el;
	}


	private void calcShapeCode() {
		int x = 0;
		for(int i = 0; i < name.length(); i++){
			x += name.charAt(i);
		}
		shapeCode = x%3;
	}


	public int getShapeCode() {
		return shapeCode;
	}


	public List<Restriction> getProhibitedRestrictions() {
		return prohibitedRestrictions;
	}
	
	public List<Restriction> getRequiredRestrictions() {
		return requiredRestrictions;
	}
}
