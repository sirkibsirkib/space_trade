package main;


import player.Astrometer;
import player.ElementStack;
import player.Engine;
import player.LifeSupport;
import player.Spectrometer;
import universe.Element;
import abstracts.Chemistry;
import abstracts.Log;
import abstracts.Tipper;

public abstract class Cheater {
	static private GameData gd;
	
	public static void init(GameData gd){
		Cheater.gd = gd;
	}
	
	static public boolean tryCheatCode(String s){
		boolean success = false;
		switch(s){
		case "gitOuttaHee": emptyInventory(); success = true; break;
		case "shootForTheStars": addOverPoweredEngine(); success = true; break;
		case "needlesInHaystacks": addOverPoweredAstronometer(); success = true; break;
		case "ittyBittyDots": addOverPoweredSpectrometer(); success = true; break;
		case "breatheEasy": addOverPoweredLifeSupport(); success = true; break;
		case "debugSucksAss": allOverPoweredStuff(); success = true; break;
		case "tharonJaSeesAll": researchElements(); success = true; break;
		}
		if(success){
			Log.logCheatUsed(s);
		}
		return success;
	}

	private static void researchElements() {
		for(Element e : Chemistry.elements){
			Tipper.learnAbout(e, 30);
		}
	}

	private static void emptyInventory() {
		gd.getPlayer().getInventory().empty();
	}

	private static void allOverPoweredStuff() {
		emptyInventory();
		addOverPoweredEngine();
		addOverPoweredAstronometer();
		addOverPoweredSpectrometer();
		addOverPoweredLifeSupport();
	}

	private static void addOverPoweredLifeSupport() {
		Element e = Chemistry.getElement(gd.getPlayer(), null);
		gd.getPlayer().getInventory().add(new LifeSupport(new ElementStack(e, 0), 200));
		gd.getPlayer().getInventory().add(new ElementStack(e, 50));
	}

	private static void addOverPoweredSpectrometer() {
		gd.getPlayer().getInventory().add(new Spectrometer(50));
	}

	private static void addOverPoweredAstronometer() {
		gd.getPlayer().getInventory().add(new Astrometer(1000));
	}

	static private void addOverPoweredEngine() {
		Element e = Chemistry.getElement(gd.getPlayer(), null);
		gd.getPlayer().getInventory().add(new Engine(0, 1000, Engine.MAX_RANGE/2, e));
		gd.getPlayer().getInventory().add(new ElementStack(e, 50));
	}
}
