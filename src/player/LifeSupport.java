package player;

import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.Scanner;

import abstracts.Chemistry;
import abstracts.Fun;
import abstracts.Restriction;
import abstracts.Saver;
import universe.Element;
import universe.Point;

public class LifeSupport implements Item {
	private ElementStack fuel;
	private int chance;
	private int drawRad;
	
	public LifeSupport(ElementStack fuel, int chance){
		this.fuel = fuel;
		this.chance = chance;
		drawRad = (int) (p50/(Math.sqrt(chance)+.2));
	}
	
	@Override
	public void render(Renders r, int... info) {
		r.renderRect(new ScreenPoint(info[0]+p10, info[1]+p10), p80, p80, Color.DARK_GRAY, true);
		r.renderCircle(new ScreenPoint(info[0]+p50, info[1]+p60), drawRad, 0, fuel.getElement().getCol(), true);
		r.renderText(new ScreenPoint(info[0]+p10, info[1]+p10), "Life", Color.WHITE, true);
		r.renderText(new ScreenPoint(info[0]+p10, info[1]+p30), "Support", Color.WHITE, true);
		r.renderText(new ScreenPoint(info[0]+p10, info[1]+p60).offset(0, p10), fuel.getElement().getSymbol() + "  " + fuel.getNumber() + "-" + chance, Color.WHITE, false);
	}

	@Override
	public Item generateItemWithValueAt(Point p, double value) {
		double eGetDistance = Fun.rdg(Fun.rdg(Fun.rdg(.4)));
		Point p2 = p.validOffset(eGetDistance);
		Element e = Chemistry.getElement(p2, null);
		double distanceToElement = Chemistry.distanceToElementFrom(e, p, Restriction.itemGenGrabBag());
		int tryChance = (int) Math.pow((2.4*value*(distanceToElement+0.1)), .75)+2;
		return new LifeSupport(new ElementStack(e, 1), tryChance);
	}

	public ElementStack getFuel() {
		return fuel;
	}

	public int getChance() {
		return chance;
	}

	public void wearAndTear(){
		if(Fun.chance(450/(chance+10))){
			if(chance > 2 || (fuel.getNumber() > 4 && chance > 1)){
				chance--;
			}else{
				fuel.add(1);
			}
		}
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "lifes" + delims[dIndex] + fuel.save(saver, delims, dIndex+1) + delims[dIndex] + chance;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		ElementStack fuel = (ElementStack) new ElementStack(null, 0).load(saver, delims, dIndex+1, scan.next());
		LifeSupport ls = new LifeSupport(fuel, scan.nextInt());
		scan.close();
		return ls;
	}
}
