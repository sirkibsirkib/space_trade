package abstracts;

import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import player.Astrometer;
import player.Beacon;
import player.ElementStack;
import player.Engine;
import player.Item;
import player.LifeSupport;
import player.Research;
import player.Spectrometer;
import surface.Offer;
import universe.Body;
import universe.Element;
import universe.Planet;
import universe.Star;

/*
 * Static object generates and stores log entries for game events.
 */
public abstract class Log {
	public static final int LOG_CAP = 10;
	
	private static List<Entry> logEntries = new ArrayList<>();
	
	///// LOG TYPES
	
	public static void logJump(Body dest, ElementStack cost){
		String build = "Jumped to " + nameOf(dest);
		if(cost != null){
			build += " spent " + elementStackString(cost);
		}
		log(build, Color.WHITE);
	}
	
	public static void logMine(Planet p, ElementStack reward){
		String build = "Mined " + p.getName() + " " + (p.getIndex()+1);
		if(reward != null){
			build += ", acquired " + elementStackString(reward);
		}
		log(build, Color.WHITE);
	}

	public static void logForage(Planet p, ElementStack reward) {
		String build = "Foraged surface of " + p.getName() + " " + (p.getIndex()+1);
		if(reward != null){
			build += ", acquired " + elementStackString(reward);
		}
		log(build, Color.WHITE);
	}

	public static void logMineExhausted(Planet p) {
		String build = "    Mine on " + nameOfPlanet(p) + " exhausted.";
		log(build, Color.WHITE);
	}

	public static void logTrade(Offer o) {
		String build = "Traded " + itemString(o.getPrice()) + " to get " + itemString(o.getReward());
		log(build, Color.WHITE);
	}
	


	public static void logResearched(Element element, int number) {
		String build = "Researched " + element.getName() + " " + number + " times. Codex updated.";
		log(build, Color.GREEN);
	}

	public static void shipPurchased(ElementStack cost) {
		String build = "Purchased new ship for " + itemString(cost) + ". Cargo stored.";
		log(build, Color.WHITE);
	}
	
	public static void noResourcesForJump(Body b) {
		String build = "Insufficent resources to jump to " + nameOf(b);
		log(build, Color.RED);
	}

	public static void destinationOutofRange() {
		String build = "Jump destination out of range";
		log(build, Color.RED);
	}
	
	public static void noResourcesForLifeSupport() {
		String build = "Insufficent resources for life support";
		log(build, Color.RED);
	}
	
	public static void logInventoryFull(int itemsLost) {
		String build = "Inventory full! " + itemsLost + " items lost.";
		log(build, Color.RED);
	}


	public static void cantAffordTrade(Item price) {
		String build = "Could not afford required " + itemString(price) + " to trade.";
		log(build, Color.RED);
	}

	public static void cantAffordShipPurchase(ElementStack cost) {
		String build = "Could not afford required " + itemString(cost) + " to purchase new ship.";
		log(build, Color.RED);
	}
	
	public static void vaultDepositLog(Item x) {
		String build = "Deposited " + itemString(x) + " into vault.";
		log(build, Color.WHITE);
	}
	
	public static void vaultWithdrawLog(Item x) {
		String build = "Withdrew " + itemString(x) + " from vault.";
		log(build, Color.WHITE);
	}

	public static void shipyardWithdraw(Item x) {
		String build = "Withdrew " + itemString(x) + " from shipyard storage.";
		log(build, Color.WHITE);
	}
	
	public static void genericLog(String message, Color col) {
		log(message, col);
	}

	public static void vaultActivateBeacon(Planet parent) {
		String build = "    Activated beacon on " + nameOf(parent) + ".";
		log(build, Color.WHITE);
	}
	
	public static void vaultDeactivateBeacon(Planet parent) {
		String build = "    Deactivated beacon on " + nameOf(parent) + ".";
		log(build, Color.WHITE);
	}

	public static void shipyardStorageEmpty() {
		String build = "    Shipyard storage empty. Old ship available for purchase.";
		log(build, Color.WHITE);
	}

	public static void logCheatUsed(String s) {
		String build = "Activated cheat '" + s + "'.";
		log(build, Color.CYAN);
	}

	public static void logBeaconJump() {
		String build = "Executed jump to beacon.";
		log(build, Color.WHITE);
	}
	
	///// HELPERS
		
	private static String itemString(Item i) {
		if(i instanceof ElementStack){
			return elementStackString((ElementStack) i);
		}
		if(i instanceof Astrometer) {
			return "Astrometer";
		}
		if(i instanceof Spectrometer) {
			return "Navigator";
		}
		if(i instanceof Engine) {
			return "Engine";
		}
		if(i instanceof LifeSupport) {
			return "LifeSupport";
		}
		if(i instanceof Beacon) {
			return "Beacon";
		}
		if(i instanceof Research) {
			Research r = (Research) i;
			return r.getNumber() + "x " + r.getElement().getName() + " Research";
		}
		return "?";
	}

	private static String elementStackString(ElementStack es) {
		return es.getNumber() + "x " + es.getElement().getName();
	}

	private static String nameOfPlanet(Planet p){
		String s = p.getName() + " ";
		switch(p.getIndex()+1){
		case 0: s += "I"; break;
		case 1: s += "II"; break;
		case 2: s += "II"; break;
		case 3: s += "IV"; break;
		case 4: s += "V"; break;
		case 5: s += "VI"; break;
		case 6: s += "VII"; break;
		case 7: s += "VIII"; break;
		case 8: s += "IX"; break;
		case 9: s += "X"; break;
		case 10: s += "XI"; break;
		case 11: s += "XII"; break;
		case 12: s += "XIII"; break;
		case 13: s += "XIV"; break;
		case 14: s += "XV"; break;
		case 15: s += "XVI"; break;
		case 16: s += "XVII"; break;
		case 17: s += "XVIII"; break;
		}
		return s;
	}
	
	private static String nameOf(Body b){
		String name = "";
		if(b instanceof Planet){
			Planet p = (Planet) b;
			name = nameOfPlanet(p);
		}
		if(b instanceof Star){
			Star s = (Star) b;
			name = s.getName();
		}
		return name;
	}
	
	public static void render(Renders r, int... info) {
		int lineHeight = 20;
		ScreenPoint tl = new ScreenPoint(r.getScreenWidth()-500-25, r.getScreenHeight()-lineHeight*logEntries.size()-25);
		for(int i = 0; i < logEntries.size(); i++){
			Entry le = logEntries.get(i);
			r.renderText(tl.offset(0, i*lineHeight), le.s, le.col, false);
		}
	}
	
	private static void log(String s, Color col){
		//Frame.setChanges(true);
		//TODO
		if(logEntries.size() >= LOG_CAP){
			logEntries.remove(0);
		}
		logEntries.add(new Entry(s, col));
	}
	
	private static class Entry {
		String s;
		Color col;
		Entry(String s, Color col){
			this.s = s;
			this.col = col;
		}
	}

	public static void clear() {
		logEntries.clear();
	}
}
