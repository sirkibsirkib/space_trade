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

public class ElementStack implements Item, Stackable {
	public static final int CAP = 200;
	
	private Element e;
	private int number;
	
	public ElementStack(Element e, int number){
		this.e = e;
		this.number = number;
	}
	
	public void add(int number){
		this.number += number;
	}
	
	public void remove(int number){
		this.number -= number;
	}
	
	public int getNumber(){
		return number;
	}
	
	public Element getElement(){
		return e;
	}
	
	@Override
	public ElementStack clone(){
		return new ElementStack(e, number);
	}
	
	public void render(Renders r, int... info) {
		ScreenPoint sc = new ScreenPoint(info[0], info[1]);
		if(number > CAP){
			r.renderRect(sc, p100, p100, e.getCol(), true);
			r.renderText(sc.offset(p80, p80), "+", Color.WHITE, false);
		}else{
			int x = (int) (p100*Math.sqrt(number)/Math.sqrt(CAP));
			r.renderRect(sc, x, x, e.getCol(), true);
		}
		switch(e.getShapeCode()){
		case 0: r.renderRect(sc.offset(p40, p40), p20, p20, e.getDarkerCol(), true);break;
		case 1: r.renderCircle(sc.offset(p50, p50), p20, 0, e.getDarkerCol(), true);break;
		case 2: r.renderTriangle(sc.offset(p40, p60), sc.offset(p60, p60), sc.offset(p50, p40), e.getDarkerCol(), true);break;
		}
		
		r.renderText(sc, e.getName(), Color.WHITE, true);
		r.renderText(sc.offset(0, p30), e.getSymbol(), Color.WHITE, false);
		r.renderText(sc.offset(0, p60), "" + number, Color.WHITE, false);
	}

	@Override
	public Item generateItemWithValueAt(Point p, double value){
		double maxRad = .4;
		Element find = Chemistry.getElement(p.validOffset(Fun.rdg(Fun.rdg(maxRad))), null);
		double searchRad  = distToElement(p, find);
		int num = (int) (1 + value / (searchRad + 0.001) * 0.00125);
		return new ElementStack(find, num);
	}

	private double distToElement(Point p, Element find) {
		int mult = 5;
		double tot = 0;
		for(int i = 0; i < mult; i++){
			Chemistry.distanceToElementFrom(find, p, Restriction.itemGenGrabBag());
		}
		return tot/mult;
	}

	@Override
	public void wearAndTear() {
		//Do Nothing
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "eleme" +  delims[dIndex] + saver.getKeyFor(e) + delims[dIndex] + number;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		ElementStack es = new ElementStack((Element) saver.getObject(scan.nextLong()), scan.nextInt());
		scan.close();
		return es;
	}

	@Override
	public int getStackCap() {
		return CAP;
	}

	@Override
	public Stackable tryAddReturnRemain(Stackable s){
		if(s instanceof ElementStack){
			ElementStack es = (ElementStack) s;
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
		return new ElementStack(e, transfer);
	}

	@Override
	public boolean isOverCap() {
		return number > CAP;
	}

	@Override
	public boolean matches(Stackable s) {
		if(s instanceof ElementStack){
			return ((ElementStack) s).e == e;
		}
		return false;
	}

	@Override
	public Stackable duplicate() {
		return new ElementStack(e, number);
	}
}
