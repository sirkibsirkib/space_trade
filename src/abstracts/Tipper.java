package abstracts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import player.ElementStack;
import player.Ship;
import universe.Element;
import universe.Point;

public abstract class Tipper {
	private static Map<String, String> tips = new HashMap<>();
	private static SoftcodedTips softcodedTips = new SoftcodedTips();
	
	public static void init(){
		addTip("item: astrometers", "Astrometers help the ship detect and lock onto celestial bodies that appear smaller in the sky. A more powerful Astrometer will allow your ship to jump directly to distant small objects. Each jump of your ship degrades the highest-quality Astrometer in your inventory. With no astromters, your ship has a base value equivalent to an astrometer with a rating of " +  Ship.BASE_ASTROMETER_LIMIT);
		addTip("item: beacons", "When beacons are placed inside vaults, your ship will draw a colored streak to them if you have beacon view enabled. The ship can at any time, jump to an active beacon within reach. This costs no resources but greatly damages the beacon, reducing range.");
		addTip("toggle beacons view", "By pressing [2], you can toggle the drawing of active beacons at any time.");
		addTip("toggle jumps view", "By pressing [1], you can toggle the drawing of nearby celestial bodies you can use your engines to jump to");
		addTip("feature: fields", "Clicking fields will forage them. Each forage requires and drains lifesupport. Foraging deposits some of the planet's base element into your ship inventory.");
		addTip("feature: depots", "Clicking a depot displays the depot's offer. The offer can be accepted to trade an item for another. Each accepted offer inflates the price for next time.");
		addTip("item: elements", "The universe is composed of elements. They are used as a resource for multiple items or traded for other items at Depots.");
		addTip("item: element stacks", "elements take up inventory space, but can stack up to " + ElementStack.CAP + ". Acquiring more than this limit will require a new stack");
		addTip("feature: vaults", "vaults are planetary features that act as additional stationary inventories. One can deposit or withdraw items by clicking the item in question and then clicking the corresponding arrow.");
		addTip("planets", "Planets orbit stars. They are much smaller than stars so one must be closer or have a highly sensitive astrometer to jump to them. Once on the surface, the ship can interact with the numerous features on the planet's surface without expending engine fuel.");
		addTip("feature: caves", "caves can be mined in much the same way fields can be foraged. Caves, however, supply a semi-random element native to the location in space. Caved will be exhausted and vanish after a fixed number of minings. Mining requires and consumes life support.");
		addTip("item: life supports", "The top-left-most life support in your inventory will be active at all times. All processes that require and consume life support will attempt to use the active life support. Life support consumes a small amount of a fixed resource each time it is used. Life support items will gradually deteriorate with use, and will consume more resources");
		addTip("blue dots", "SEE depots");
		addTip("red dots", "SEE vaults");
		addTip("white dots", "SEE fields");
		addTip("yellow dots", "SEE caves");
		addTip("item: engines", "engines are required to jump between stellar objects. They burn a quantity of an element in your inventory if possible to complete a jump. SEE engine priority. SEE jump lines");
		addTip("engine priority", "engines can be rearranged in the inventory from top-left to bottom right, going vertically first. This ordering will determine the order in which engines are tried for use in a jump. The color of connecting jump lines will dynamically change if toggled on. SEE jump lines");
		addTip("rearranging or moving inventory items", "moving an item in a controlled inventory requires selecting the item by clicking it, whereby it will show a yellow border, and then by pressing WASD to move the item.");
		addTip("jump lines", "the currently-reachable stellar objects are displayed as being connected to the ship by lines if jumps view is toggled on. SEE jump view. This indicates the order in which engines' uses will be attempted to complete a given jump. Engine capabilities are limited by their maximum ranges");
		addTip("item numbers on icon", "Items display all sorts of information about their quality in their icon. The various numbers indicate important values regarding costs, capabilities and proabilities. These vary from item to item but some conventions persist accross items.SEE number dash number.");
		addTip("number dash number (n-m)", "This is a notation used for several items in the game with variable element costs and probabilties. The (n) value indicates the number of the elements. Eg: 5 Narium per engine jump. The (m) value indicates probability / chance of the cost being deducted per use. Eg: a 2/3 engine will use 2 resources approx. 33% of the time");
		addTip("item: spectrogram", "spectrograms in your inventory will boost the detail of nearby stars visible to you, increasing the radius of various detail levels. Multiple spectrograms in your inventory will stack effectiveness linearly, but will all wear and tear at once");
		addTip("wear and tear / durability", "All usable items will degrade with use. This includes beacons inside vaults. This degradation somehow decreases the quality of the item by either increasing its use cost or decreasing its effectiveness, potentially requiring it to be replaced periodically.");
		addTip("emergency beacon jump", "SEE beacons. Without a functional engine, the ship cannot jump. Without functional life support, the ship cannot mine. Use an emergency beacon jump to escape being stranded.");
		addTip("zoom", "scrolling the mouse wheel in and out will allow you to change zoom levels to more precisely interact with different objects at vastly different scales");
		addTip("deleting items", "pressing the delete key with an item selected will prompt an option to permanently delete it.");
		addTip("saving and loading", "Press F5 to prompt a game save. Press F6 to prompt a game load. One save file can be kept at a time.");
		addTip("selecting items", "clicking an item in your inventory will select it. Selected items are used to indicate the intention of movement and deletion keys, as well as transferral to a vault.");
		addTip("green dots", "SEE shipyards");
		addTip("feature: shipyards", "shipyards are planetary features that allow the purchase of a new ship. New ships have different sizes of inventory. Once purchased, your old equipment is stored in the shipyard, and items can be withdrawn freely.");
		addTip("small planets", "without a sufficiently sensitive Astrometer, the ship may not be able to lock onto a distant small body. SEE astrometers.");
		addTip("toggle orbits view", "By pressing [3], you can toggle the display of orbital paths of planets around stars. This makes the structure of solar systems and the presence of lockable bodies more obvious.");
		addTip("white icons", "SEE selecting items. Items in the inventory with white accents likely indicates that an item is active. Items such as lifesupport (SEE lifesupports) use this system to indicate the item currently in use.");
		addTip("white beacon line", "the unique white beacon is present when you start the game. It indicates the direction to the vault that will finish the game. Flying in the direction of the stripe will get you closer.");
		addTip("gray stars", "stars that are visible enough (SEE astrometers) are displayed as grey. The exact size or elemental composition of these stars is unknown. A more powerful spectrometer (SEE spectrometers) will help with increasing your sight radius.");
		addTip("dark planet shadow", "planets have dark and light sides as displayed by a crescent of shadow. This can be used to locate the planet's host star. The size of the shadow indicates roughly how large the star seems from the planet.");
		addTip("floating letters", "jump lines are accompanied by rings that represent maximum jump ranges. These are accompanied by letters that can be used to match them to the corersponding engines in your inventory. SEE engines.");
		
		//addTip("", "");
	}

	public static void learnAbout(Element e, int times){
		softcodedTips.learnAbout(e, times);
	}

	private static void addTip(String title, String rawTipText) {
		if(tips.containsKey(title)){
			throw new Error();
		}
		StringBuilder sb = new StringBuilder(rawTipText);
		int i = 0;
		while ((i = sb.indexOf(" ", i + 40)) != -1) {
		    sb.replace(i, i + 1, "\r\n");
		}
		tips.put(title, sb.toString());
	}
	
	public static String[] entriesMatching(String query){
		List<String> results = new ArrayList<>();
		for(String s : tips.keySet()){
			if(s.contains(query)){
				results.add(s);
			}
		}
		java.util.Collections.sort(results);
		return results.toArray(new String[results.size()]);
	}

	public static String get(String selection) {
		return tips.get(selection);
	}
	
	public static String save(Saver saver, char[] delims, int dIndex) {
		return new SoftcodedTips().save(saver, delims, dIndex);
	}
	
	public static void load(Saver saver, char[] delims, int dIndex, String data) {
		softcodedTips = (SoftcodedTips) new SoftcodedTips().load(saver, delims, dIndex, data);
	}
	
	
	static class SoftcodedTips implements Savable{
		private List<ElementTip> elementTips;
		
		public SoftcodedTips(){
			elementTips = new ArrayList<>();
		}
		
		public void learnAbout(Element e, int times){
			ElementTip et = getTipAbout(e);
			for(int i = 0; i < times; i++){
				if(et == null){
					et = new ElementTip(e);
					elementTips.add(et);
				}
				et.learnSomething();
			}
		}

		private ElementTip getTipAbout(Element e) {
			for(ElementTip et : elementTips){
				if(et.e == e){
					return et;
				}
			}
			return null;
		}

		@Override
		public String save(Saver saver, char[] delims, int dIndex) {
			String s = "";
			for(ElementTip et : elementTips){
				s += et.save(saver, delims, dIndex+1) + delims[dIndex];
			}
			return s;
		}

		@Override
		public Object load(Saver saver, char[] delims, int dIndex, String data) {
			Scanner scan = new Scanner(data);
			scan.useDelimiter(delims[dIndex]+"");
			SoftcodedTips st = new SoftcodedTips();
			while(scan.hasNext()){
				st.elementTips.add((ElementTip) new ElementTip(null).load(saver, delims, dIndex+1, scan.next()));
			}
			scan.close();
			return st;
		}
	}
	
	static class ElementTip implements Savable{
		private Element e;
		private List<Restriction> prohibited;
		private List<Restriction> required;
		private List<Point> loci;
		
		ElementTip(Element e){
			this.e = e;
			prohibited = new ArrayList<>();
			required = new ArrayList<>();
			loci = new ArrayList<>();
			tips.put(keyString(), entryString());
		}
		
		private String keyString(){
			return "element: " + e.getName().toLowerCase();
		}
		
		private String entryString(){
			String s = e.getName() + " is an element.\n\n";
			if(required.size() > 0){
				s += "It's LIKELY to be found when";
				for(int i = 0; i < required.size(); i++){
					s += "\n  " + required.get(i).name().replace("_", " ").toLowerCase();
				}
				s += "\n\n";
			}
			if(prohibited.size() > 0){
				s += "It's UNLIKELY to be found when:";
				for(int i = 0; i < prohibited.size(); i++){
					s += "\n  " + prohibited.get(i).name().replace("_", " ").toLowerCase();
				}
				s += "\n\n";
			}
			if(loci.size() > 0){
				s += "Known coordinates:";
				for(int i = 0; i < loci.size(); i++){
					s += '\n' + String.format("  (%.2f,  %.2f)", loci.get(i).getX(), loci.get(i).getY());
				}
				s += "\n";
			}
			return s;
		}
		
		private void learnSomething(){
			switch(Fun.weightedCaseRng(3, 2, 2)){
			case 0:{
				if(e.getProhibitedRestrictions().size() > 0){
					Restriction r = e.getProhibitedRestrictions().get(Fun.rng(e.getProhibitedRestrictions().size()));
					if(!prohibited.contains(r)){
						prohibited.add(r);
					}
				}break;
			}
			case 1:{
				if(e.getRequiredRestrictions().size() > 0){
					Restriction r = e.getRequiredRestrictions().get(Fun.rng(e.getRequiredRestrictions().size()));
					if(!required.contains(r)){
						required.add(r);
					}
				}break;
			}
			case 2:{
				List<Point> loci = e.getLoci();
				Point locus = loci.get(Fun.rng(loci.size()));
				if(!this.loci.contains(locus)){
					this.loci.add(locus);
				}
			}
			}
			tips.remove(keyString());
			tips.put(keyString(), entryString());
		}

		@Override
		public String save(Saver saver, char[] delims, int dIndex) {
			String s = "" + saver.getKeyFor(e) + delims[dIndex] +
					Restriction.saveRestrictions(required, saver, delims, dIndex+1) +
					delims[dIndex] + Restriction.saveRestrictions(prohibited, saver, delims, dIndex+1);
			for(Point p : loci){
				s += delims[dIndex] + e.getLoci().indexOf(p);
			}
			return s;
		}

		@Override
		public Object load(Saver saver, char[] delims, int dIndex, String data) {
			Scanner scan = new Scanner(data);
			scan.useDelimiter(delims[dIndex]+"");
			ElementTip et = new ElementTip((Element) saver.getObject(scan.nextLong()));
			et.required = Restriction.loadRestrictions(saver, delims, dIndex+1, scan.next());
			et.prohibited = Restriction.loadRestrictions(saver, delims, dIndex+1, scan.next());
			while(scan.hasNext()){
				et.loci.add(et.e.getLoci().get(scan.nextInt())); 
			}
			scan.close();
			return et;
		}
	}
}
