package player;

import io.Renderable;
import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import universe.Body;
import universe.Point;
import abstracts.Fun;
import abstracts.Log;
import abstracts.Savable;
import abstracts.Saver;
import abstracts.Tipper;

public class Ship extends Point implements Renderable, Savable {
	public static final double RADIUS = 0.0000003;
	public static final int BASE_ASTROMETER_LIMIT = 50;
	private Inventory inv;
	private Astrometer astrometer;
	private LifeSupport lifeSupport;
	private List<Crew> crew;
	private List<Beacon> activeBeacons;
	
	
	
	public Ship(Point startPosition, Inventory inv) {
		super(startPosition.getX(), startPosition.getY());
		this.inv = inv;
		activeBeacons = new ArrayList<>();
		crew = new ArrayList<>();
		crew.add(new Crew());
		crew.add(new Crew());
		crew.add(new Crew());
		crew.add(new Crew());
		crew.add(new Crew());
	}
	
	public int addToInv(Item es){
		if(es instanceof Research){
			Research research = (Research) es;
			Tipper.learnAbout(research.getElement(), research.getNumber());
			Log.logResearched(research.getElement(), research.getNumber());
			return 0;
		}
		return inv.add(es);
	}

	public Inventory getInventory() {
		return inv;
	}

	public void jumpTo(Point p) {
		x = p.getX();
		y = p.getY();
	}

	@Override
	public void render(Renders r, int... info) {
		r.renderCircle(this, RADIUS, 0, Color.GRAY, true);
		r.renderCircle(this, RADIUS, 0, Color.BLACK, false);
	}
	
	public Point startPoint(){
		return startPoint();
	}
	
	public void checkEquipment(){
		astrometer = null;
		for(Item x : inv.listItems()){
			if(x instanceof Astrometer){
				astrometer = (Astrometer) x;
				break;
			}
		}
		lifeSupport = null;
		for(Item x : inv.listItems()){
			if(x instanceof LifeSupport){
				lifeSupport = (LifeSupport) x;
				break;
			}
		}
	}
	
	public double getAstrometerSensitivity(){
		if(astrometer == null){
			return BASE_ASTROMETER_LIMIT;
		}
		return Fun.max(BASE_ASTROMETER_LIMIT, astrometer.getSensitivity());
	}
	
	public double getMaxDistanceDiameterRatio(){
		return getAstrometerSensitivity()*aggregateSpectrometerFactor();
	}
	
	public LifeSupport getLifeSupport(){
		return lifeSupport;
	}

	public boolean useLifeSupport() {
		return inv.useLifeSupport(lifeSupport);
	}

	public Astrometer getAstrometer() {
		return astrometer;
	}
	
	public List<Beacon> getActiveBeacons() {
		return activeBeacons;
	}
	
	public void removeActiveBeacon(Beacon b){
		activeBeacons.remove(b);
	}
	
	public void addActiveBeacon(Beacon b){
		activeBeacons.add(b);
	}
	
	private double aggregateSpectrometerFactor(){
		double factor = 1;
		for(Item item : inv.listItems()){
			if(item instanceof Spectrometer){
				factor += ((Spectrometer) item).getFactor()-1;
			}
		}
		return factor;
	}

	public void wearAndTear() {
		for(Item item : inv.listItems()){
			if(item instanceof Spectrometer){
				item.wearAndTear();
			}
		}
		if(astrometer != null){
			astrometer.wearAndTear();
		}
		for(Beacon b : activeBeacons){
			if(b != null){
				b.wearAndTear();
			}
		}
	}

	public boolean isWithin(Body b) {
		return distanceTo(b) < b.getRadius()*2;
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "" + x + delims[dIndex] + y + delims[dIndex] + inv.save(saver, delims, dIndex+1); 
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		Ship ship = new Ship(Point.ZERO, null);
		ship.x = scan.nextDouble();
		ship.y = scan.nextDouble();
		ship.inv = (Inventory) new Inventory(1, 1, null, null).load(saver, delims, dIndex+1, scan.next());
		scan.close();
		return ship;
	}

	public void clearAllBeacons() {
		activeBeacons.clear();
	}

	public double getMaxJumpRange() {
		double[] jumpRanges = inv.engineRanges();
		if(jumpRanges.length == 0){
			return 0;
		}
		return jumpRanges[jumpRanges.length-1];
	}

	public boolean withinAstrometerRange(Body b) {
		return angleMeasure(b) > 1/(getAstrometerSensitivity());
	}

	public Inventory swapInventoryWith(Inventory givenInv) {
		Inventory wasMine = inv;
		String wasDrawTitleString = inv.getDrawTitleString();
		ScreenPoint wasTL = inv.getTL();
		inv.reskin(givenInv.getTL(), givenInv.getDrawTitleString());
		givenInv.reskin(wasTL, wasDrawTitleString);
		inv = givenInv;
		return wasMine;
	}
}
