package player;

import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.Scanner;

import universe.Point;
import universe.Universe;
import abstracts.Fun;
import abstracts.Saver;

public class Beacon implements Item {
	private Color col;
	private double signalRadius;
	Point beaconPoint;
	
	public Beacon(Color col, double signalRadius){
		this.col = col;
		this.signalRadius = signalRadius;
	}
	
	public void render(Renders r, int... info) {
		ScreenPoint sp = new ScreenPoint(info[0], info[1]);
		r.renderRect(sp.offset(p20, 0), p80, p80, col, true);
		r.renderRect(sp.offset(0, p80), p20, p20, Color.BLACK, true);
		r.renderText(sp, "Beacon", Color.WHITE, true);
		r.renderText(sp.offset(0, p40), "rad:" + Universe.auDistanceString(signalRadius), Color.WHITE, false);
	}
	
	public Color getCol(){
		return col;
	}
	
	public double getSignalRadius(){
		return signalRadius;
	}

	@Override
	public Item generateItemWithValueAt(Point p, double value) {
		Color tryCol;
		switch(Fun.rng(8)){
		case 0: tryCol =  Color.RED; break;
		case 1: tryCol =  Color.BLUE; break;
		case 2: tryCol =  Color.CYAN; break;
		case 3: tryCol =  Color.DARK_GRAY; break;
		case 4: tryCol =  Color.LIGHT_GRAY; break;
		case 5: tryCol =  Color.PINK; break;
		case 6: tryCol =  Color.GREEN; break;
		case 7: tryCol =  Color.ORANGE; break;
		default: tryCol =  Color.MAGENTA; break;
		}
		return new Beacon(tryCol, Math.pow(value, .82)*0.001);
	}
	
	public Point getBeaconPoint(){
		return beaconPoint;
	}
	
	public void setBeaconPoint(Point p){
		beaconPoint = p;
	}

	@Override
	public void wearAndTear() {
		signalRadius = signalRadius*.995 + 0.000001;
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "beaco" + delims[dIndex] + col.getRed() + delims[dIndex]
				+ col.getGreen() + delims[dIndex] + col.getBlue() + delims[dIndex] + signalRadius;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Color col = new Color(scan.nextInt(), scan.nextInt(), scan.nextInt());
		Beacon beac = new Beacon(col, scan.nextDouble());
		scan.close();
		return beac;
	}

	public void majorWear() {
		signalRadius = signalRadius*.6 + 0.000001;
	}
}
