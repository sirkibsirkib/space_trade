package surface;

import io.HasUIButton;
import io.Renderable;
import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.Ticker;
import player.Astrometer;
import player.Beacon;
import player.ElementStack;
import player.Engine;
import player.Item;
import player.LifeSupport;
import player.Pulses;
import player.Research;
import player.Spectrometer;
import universe.Element;
import universe.Planet;
import universe.Point;
import abstracts.Fun;
import abstracts.Log;
import abstracts.Saver;

public class Depot extends Feature implements HasUIButton, Pulses{
	private static final int MAX_OFFERS = 8;
	private static final int FEATURE_WRAP = 4;
	private List<Offer> offers;
	private boolean pulsing;
	private static ScreenPoint tl = new ScreenPoint(Item.p100*6+10, 50);
	private double nextWorth;
	
	public Depot(double x, double y, Planet parent) {
		super(x, y, Feature.RADIUS, parent);
		pulsing = false;
		nextWorth = Fun.rdg(2000*distanceTo(Point.ZERO) + 30) + 20;
		offers = new ArrayList<>();
		if(parent != null){
			int numOffers = Fun.rng(Fun.rng(MAX_OFFERS-1)) + 1;
			for(int i = 0; i < numOffers; i++){
				addNewOffer();
			}
		}
	}

	private void addNewOffer() {
		Offer o = null;
		do{
			Ticker.spin();
			o = generateOffer(tl.offset((int) ((offers.size()/FEATURE_WRAP)*(Item.p100*3.2 + 10)),
					(int) ((offers.size()%FEATURE_WRAP)*Item.p100*1.6)));
		}while(!noOfferWithStringSummary(o.getOfferStringSummary()));
		offers.add(o);
	}
	
	private boolean noOfferWithStringSummary(String s){
		for(Offer o : offers){
			if(o.getOfferStringSummary().equals(s)){
				return false;
			}
		}
		return true;
	}
	
	public Offer generateOffer(ScreenPoint offerTl){
		switch(Fun.weightedCaseRng(6, 5, 3, 4, 4, 3, 2)){
		case 0: return generateADeal(0, 0, offerTl); //elementStack
		case 1: return generateADeal(0, 1, offerTl); //engine
		case 2: return generateADeal(0, 2, offerTl); //spectrometer
		case 3: return generateADeal(0, 3, offerTl); //lifeSupport
		case 4: return generateADeal(0, 4, offerTl); //astrometer
		case 5: return generateADeal(0, 5, offerTl); //beacon
		case 6: return generateADeal(0, 6, offerTl); //research
		}
		throw new Error();
	}
	
	private Offer generateADeal(int costCode, int rewardCode, ScreenPoint offerTl){
		double costValue = nextWorth;
		nextWorth *= (1 + Fun.rdg(.3));
		double rewardValue = costValue*.9;
		Item price = null;
		switch(costCode){
		case 0: price = new ElementStack(null, 0).generateItemWithValueAt(this, costValue); break;
		case 1: price = new Engine(0, 0, 0, null).generateItemWithValueAt(this, costValue); break;
		case 2: price = new Spectrometer(0).generateItemWithValueAt(this, costValue); break;
		case 3: price = new LifeSupport(null, 0).generateItemWithValueAt(this, costValue); break;
		case 4: price = new Astrometer(0).generateItemWithValueAt(this, costValue); break;
		case 5: price = new Beacon(null, 0).generateItemWithValueAt(this, costValue); break;
		case 6: price = new Research(null, 0).generateItemWithValueAt(this, costValue); break;
		}
		
		Item reward = null;
		switch(rewardCode){
		case 0:{
			if(price instanceof ElementStack){
				reward = getDifferentElementStack(rewardValue, ((ElementStack) price).getElement());
			}else{
				reward = new ElementStack(null, 0).generateItemWithValueAt(this, rewardValue);
			}
			break;
		}
		case 1: reward = new Engine(0, 0, 0, null).generateItemWithValueAt(this, rewardValue); break;
		case 2: reward = new Spectrometer(0).generateItemWithValueAt(this, rewardValue); break;
		case 3: reward = new LifeSupport(null, 0).generateItemWithValueAt(this, rewardValue); break;
		case 4: reward = new Astrometer(0).generateItemWithValueAt(this, rewardValue); break;
		case 5: reward = new Beacon(null, 0).generateItemWithValueAt(this, rewardValue); break;
		case 6: reward = new Research(null, 0).generateItemWithValueAt(this, rewardValue); break;
		}
		return new Offer(price, reward, offerTl, costValue);
	}
	
	private ElementStack getDifferentElementStack(double value, Element other) {
		ElementStack e = new ElementStack(null, 0);
		do{
			Ticker.spin();
			e = (ElementStack) e.generateItemWithValueAt(this, value);
		}while(e.getElement() == other);
		return e;
	}

	@Override
	public void render(Renders r, int... info) {
		if(info[0] == 1){
			double drawWidth = pulsing? getWidth()*.8 : getWidth();
			r.renderCircle(this, drawWidth, 0, Color.BLUE, true);
			r.renderCircle(this, drawWidth, 0, Color.DARK_GRAY, false);
		}else{
			r.renderCircle(this, getWidth()/2, 0, Color.BLUE, true);
		}
	}

	private double getWidth() {
		return RADIUS*(offers.size() + 3)/ (1 + MAX_OFFERS);
	}

	@Override
	public boolean interact(FeatureInteractor fi) {
		fi.addPulsing(this);
		pulsing = true;
		for(Offer o : offers){
			fi.addUIButton(o.getOfferButton(), this);
		}
		return true;
	}

	@Override
	public void UIClickAt(FeatureInteractor fi, ScreenPoint sp) {
		Offer clicked = null;
		for(Offer o : offers){
			if(o.onButton(sp)){
				clicked = o;
			}
		}
		if(clicked == null){
			return;
		}
		if(fi.shipContains(clicked.getPrice())){
			fi.removeFromShip(clicked.getPrice());
			fi.addToShip(clicked.getReward());
			Log.logTrade(clicked);
			for(Offer o : offers){
				o.worsenOffer();
			}
			if(Fun.chance(3*offers.size()) && offers.size() < MAX_OFFERS){
				addNewOffer();
				fi.resetButtons(this);
			}
		}else{
			Log.cantAffordTrade(clicked.getPrice());
		}
		fi.changesTrue();
	}

	@Override
	public Renderable[] getButtonVisual() {
		return offers.toArray(new Renderable[offers.size()]);
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		String s = "depot" + delims[dIndex] + x + delims[dIndex] + y + delims[dIndex] + nextWorth;
		for(Offer o : offers){
			s += delims[dIndex] + o.save(saver, delims, dIndex+1);
		}
		return s;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan =  new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Depot d = new Depot(scan.nextDouble(), scan.nextDouble(), null);
		d.nextWorth = scan.nextDouble();
		while(scan.hasNext()){
			Offer o = (Offer) new Offer(null,null,null,0).load(saver, delims, dIndex+1, scan.next());
			d.offers.add(o);
		}
		scan.close();
		return d;
	}

	@Override
	public void pulseOff() {
		pulsing = false;
	}

	@Override
	public boolean isPulsingNow() {
		return pulsing;
	}

	@Override
	public void UIReleaseAt(FeatureInteractor fi, ScreenPoint sp) {
		//nothing
	}
}
