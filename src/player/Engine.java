package player;

import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.Scanner;

import universe.Element;
import universe.Point;
import universe.Universe;
import abstracts.Chemistry;
import abstracts.Fun;
import abstracts.Restriction;
import abstracts.Saver;

public class Engine implements Item{
	public static final double
			MAX_RANGE = 0.05,
			MIN_RANGE = 0.0002;
	
	int cost;
	int chance;
	double range;
	Element e;
	int rangeBarWidth;
	int costBarHeight;
	
	public Engine(int cost, int chance, double range, Element e){
		this.cost = cost;
		this.chance = chance;
		this.range = range;
		this.e = e;
		calcRangeBarHeight();
		calcCostBarHeight();
	}

	private void calcCostBarHeight() {
		costBarHeight = (int) (p100*1.0/((0.2*cost/chance)+1));
	}

	private void calcRangeBarHeight() {
		rangeBarWidth = p10+ (int) (p90 * Math.sqrt(range) / Math.sqrt(MAX_RANGE));
	}
	
	public void render(Renders r, int... info) {
		r.renderRect(new ScreenPoint(info[0], info[1]), p100, costBarHeight, Color.DARK_GRAY, true);
		r.renderRect(new ScreenPoint(info[0], info[1]), rangeBarWidth, p100, e.getCol(), true);
		r.renderText(new ScreenPoint(info[0]+p5, info[1]+p10), "Engine", Color.WHITE, true);
		r.renderText(new ScreenPoint(info[0]+p5, info[1]+p30), e.getSymbol(), Color.WHITE, false);
		r.renderText(new ScreenPoint(info[0]+p5, info[1]+p50), "ful: " + cost + "-" + chance, Color.WHITE, false);
		r.renderText(new ScreenPoint(info[0]+p5, info[1]+p70), "rng: " + Universe.auDistanceString(range), Color.WHITE, false);
	}
	
	public ElementStack getFuelCost(){
		return new ElementStack(e, cost);
	}
	
	public int getChance(){
		return chance;
	}
	
	public void wearAndTear(){
		if(Fun.chance(10)){
			if(Fun.chance(3) && chance > 1){
				chance--;
				calcCostBarHeight();
			}else{
				cost++;
				calcCostBarHeight();
			}
		}
	}

	public double getJumpRange() {
		return range;
	}

	@Override
	public Item generateItemWithValueAt(Point p, double value) {
		//p = Point.ZERO;
		if(p.outsideUniverse()){
			throw new Error();
		}
		int tryCost = 0;
		int tryChance = 0;
		double tryRange = 0;
		Element e = null;
		double val = 0;
		double cappedMaxRange = Engine.MAX_RANGE-10*Engine.MIN_RANGE * (p.distanceTo(Point.ZERO)*.7+.3);
		do{
			tryCost = Fun.rng(Fun.rng(100))+1;
			tryChance = Fun.rng(Fun.rng(4))+1;
			if(cost < 7){
				chance += Fun.rng(30);
			}
			tryRange = Fun.rdg(Fun.rdg(Fun.rdg(cappedMaxRange))) + Engine.MIN_RANGE + Fun.rng(7)*Engine.MIN_RANGE;
			Point p2 = p.validOffset(0);
			e = Chemistry.getElement(p2, null);
			double distanceToElement = Chemistry.distanceToElementFrom(e, p, Restriction.itemGenGrabBag());
			if(e != null){
				val = 11
						*(0.3+Math.pow(tryRange/(Engine.MIN_RANGE/2), 2.1))
						/(3+Math.pow(tryCost*tryChance, 1.8))
						/(0.2+Math.pow(distanceToElement, 1.6))
						/(10+Math.pow(tryChance, 0.3));
				//System.out.printf("%.2f\t<c:%d ch:%d rng:%d dte:%.2f>%n", val, tryCost, tryChance, ((int)(tryRange/Engine.MIN_RANGE)), distanceToElement);
			}
		}while(val < value*.8 || val > value/.8);
		return new Engine(tryCost, tryChance, tryRange, e);
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "engin" + delims[dIndex] + cost + delims[dIndex] + chance + delims[dIndex] + range + delims[dIndex] + saver.getKeyFor(e);
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Engine eng = new Engine(scan.nextInt(), scan.nextInt(), scan.nextDouble(), (Element) saver.getObject(scan.nextLong()));
		scan.close();
		return eng;
	}
}
