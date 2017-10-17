package player;

import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.Scanner;

import universe.Element;
import universe.Point;
import abstracts.Chemistry;
import abstracts.Fun;
import abstracts.Restriction;
import abstracts.Saver;

public class Research implements Item, Stackable {
	public static final int CAP = 1_000;
	
	private Element e;
	private int number;
	
	public Research(Element e, int number){
		this.e = e;
		this.number = number;
	}
	
	public int getNumber(){
		return number;
	}
	
	public Element getElement(){
		return e;
	}
	
	@Override
	public Research clone(){
		return new Research(e, number);
	}
	
	public void render(Renders r, int... info) {
		ScreenPoint sc = new ScreenPoint(info[0], info[1]);
		r.renderRect(sc.offset(p20, p20), p80, p80, e.getCol(), true);
		r.renderRect(sc.offset(p10, p10), p80, p80, e.getDarkerCol(), true);
		r.renderRect(sc, p80, p80, e.getCol(), true);
		
		r.renderText(sc, "Research", Color.WHITE, true);
		r.renderText(sc.offset(0, p30), e.getSymbol(), Color.WHITE, false);
		r.renderText(sc.offset(0, p60), "" + number, Color.WHITE, false);
	}

	@Override
	public Item generateItemWithValueAt(Point p, double value){
		double maxRad = .4;
		Element find = Chemistry.getElement(p.validOffset(Fun.rdg(Fun.rdg(maxRad))), null);
		double searchRad = Chemistry.distanceToElementFrom(find, p, Restriction.itemGenGrabBag());
		int num = (int) (1 + value / (searchRad + 0.001) * 0.000125);
		return new Research(find, num);
	}

	@Override
	public void wearAndTear() {
		//Do Nothing
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "resea" +  delims[dIndex] + saver.getKeyFor(e) + delims[dIndex] + number;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Research es = new Research((Element) saver.getObject(scan.nextLong()), scan.nextInt());
		scan.close();
		return es;
	}

	@Override
	public int getStackCap() {
		return CAP;
	}

	@Override
	public Stackable tryAddReturnRemain(Stackable s){
		if(s instanceof Research){
			Research es = (Research) s;
			if(es.e == e){
				int transfer = Fun.min(CAP - number, es.getNumber());
				number += transfer;
				es.number -= transfer;
				if(es.number == 0){
					return null;
				}
				return es;
			}
		}
		return s;
	}

	@Override
	public Stackable removeUpToCap() {
		int transfer = Fun.min(CAP, number);
		number -= transfer;
		return new Research(e, transfer);
	}

	@Override
	public boolean isOverCap() {
		return number > CAP;
	}

	@Override
	public boolean matches(Stackable s) {
		if(s instanceof Research){
			return ((Research) s).e == e;
		}
		return false;
	}

	@Override
	public Stackable duplicate() {
		return new Research(e, number);
	}
}
