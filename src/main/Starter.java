package main;

import io.Inputs;
import io.Screen;
import io.ScreenPoint;
import io.UIMap;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import player.Astrometer;
import player.Beacon;
import player.ElementStack;
import player.Engine;
import player.Inventory;
import player.LifeSupport;
import surface.Vault;
import universe.Element;
import universe.Point;
import abstracts.Chemistry;
import abstracts.Fun;
import abstracts.Restriction;
import abstracts.Tipper;


public abstract class Starter {
	
	public static boolean exit = false;
	
	public static void main(String[] args){
		Tipper.init();
		newGame();
	}
	
	public static void newGame(){
		Chemistry.init();
		Inputs ins = new Inputs();
		UIMap uiMap = new UIMap();
		GameData gd = new GameData(Inventory.generateInventoryWithSlots(14, new ScreenPoint(25, 50), "Ship Inventory:"));
		Cheater.init(gd);
		Screen screen = new Screen(gd, ins, uiMap);
		Ticker ticker = new Ticker(gd, ins, uiMap, screen);
		Thread t = new Thread(ticker);
		getPlayerStarted(gd, ticker);
		t.start();
	}


	private static void getPlayerStarted(GameData gd, Ticker ticker) {
		Inventory inv = gd.getPlayer().getInventory();
		List<Restriction> r = new ArrayList<>();
		r.add(Restriction.ON_PLANET_SURFACE);
		
		Element e = Chemistry.getElement(gd.getPlayer(), r);
		Element e2 = null;
		double dist = 0.00004;
		do{
			Point pointOfEl2 = gd.getPlayer().offset(Fun.rdg(dist)-dist/2, Fun.rdg(dist)-dist/2);
			if(!pointOfEl2.outsideUniverse()){
				dist = dist*1.3 + 0.0001;
				e2 = Chemistry.getElement(pointOfEl2, null);
			}
			Ticker.spin();
		}while(e2 == e);
		
		Engine eng1 = new Engine(10, 2, 0.003, e);
		Engine eng2 = new Engine(16, 1, 0.008, e2);
		inv.add(eng1);
		inv.add(eng2);
		inv.swap(0, 0, 3, 0);
		inv.swap(0, 1, 3, 1);

		inv.add(new ElementStack(e, 100));
		inv.add(new ElementStack(e2, 100));

		inv.add(new LifeSupport(new ElementStack(e, 1), 8));
		inv.add(new LifeSupport(new ElementStack(e2, 1), 5));
		inv.add(new Astrometer(90));
		inv.add(new Beacon(Color.CYAN, .08));
		Vault goal = gd.getUniverse().getGoalVault();
		Beacon goalBeacon = new Beacon(Color.WHITE, .0004);
		goal.getInventory().add(goalBeacon);
		ticker.activateBeacon(goalBeacon, goal);
		
		gd.getUniverse().fullyGenerate(gd.getPlayer(), 0.02);
		

//		Engine eng3 = new Engine(1, 99, Engine.MAX_RANGE, e2);
//		inv.add(eng3);
//		inv.add(new Spectrometer(90));
//		inv.add(new Astrometer(560));
		
		gd.getPlayer().checkEquipment();
		
	}
	
}
