package main;

import io.Frame;
import io.HasUIButton;
import io.Inputs;
import io.Screen;
import io.UIButton;
import io.UIMap;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import player.Beacon;
import player.ElementStack;
import player.Engine;
import player.Inventory;
import player.Item;
import player.Pulses;
import surface.Feature;
import surface.FeatureInteractor;
import universe.Body;
import universe.Planet;
import universe.Point;
import universe.Star;
import abstracts.Fun;
import abstracts.Log;
import abstracts.SaverLoader;

public class Ticker implements Runnable, FeatureInteractor{
	private static final int SPIN_LIMIT = 1_000_000;
	
	public static int spin = 0;
	
	private GameData gd;
	private Inputs ins;
	private UIMap uiMap;
	private Screen screen;
	private SaverLoader saverLoader;
	
	private List<Pulses> currentlyPulsing = new ArrayList<>();
	
	
	//DEBUG
	public static void spin(){
		spin++;
		if(spin > SPIN_LIMIT){
			System.out.println("SPUN " + SPIN_LIMIT + " TIMES...");
			throw new Error();
		}
	}
	private static void unSpin() {
		spin = 0;
	}
	
	Ticker(GameData gd, Inputs ins, UIMap uiMap, Screen screen){
		this.gd = gd;
		this.ins = ins;
		this.screen = screen;
		this.uiMap = uiMap;
		saverLoader = new SaverLoader();
	}
	
	private void zoomInAnimate(){
		screen.completeZoomOut();
		screen.toggleAllDrawModes(false);
		while(screen.getZoom() < Screen.MAX_ZOOM_IN){
			screen.scrollIn();
			screen.paint();
			sleep(40);
		}
		ins.reset();
		uiMap.clearUI();
		screen.toggleAllDrawModes(true);
	}
	
	public void run() {
		zoomInAnimate();
		while(!Starter.exit){
			currentlyPulsing.clear();
			Ticker.unSpin();
			handleInputs();
			ins.reset();
			screen.paint();
			//tick();
			if(currentlyPulsing.size() > 0){
				sleep(50);
			}else{
				sleep(20);
			}
			resetSwells();
			screen.paint();
		}
	}
	
	private void resetSwells() {
		for(Pulses pul : currentlyPulsing){
			if(pul.isPulsingNow()){
				screen.setChanges();
			}
			pul.pulseOff();
		}
		
	}
	private void handleInputs() {
		if(ins.consume(KeyEvent.VK_ESCAPE)){
			if(!uiMap.isClear()){
				uiMap.clearUI();
				screen.setChanges();
				return;
			}
			if(screen.queryYesNo("Confirm Exit Game", "Are you sure you wish to exit the current game?\r\nUnsaved progress will be lost.")){
				System.exit(0);
			}
		}
		if(ins.consume(KeyEvent.VK_F4)){
			screen.toggleFullscreen();
		}
		if(ins.consume(KeyEvent.VK_ENTER)){
			boolean done = false;
			while(!done){
				done = screen.queryTipsReturnDone();
			}
		}
		if(ins.consume(KeyEvent.VK_1)){
			screen.toggleJumpRanges();
			screen.setChanges();
		}
		if(ins.consume(KeyEvent.VK_2)){
			screen.toggleDrawBeacons();
			screen.setChanges();
		}
		if(ins.consume(KeyEvent.VK_3)){
			screen.toggleOrbitLines();
			screen.setChanges();
		}
		if(ins.consume(KeyEvent.VK_4)){
			screen.toggleDrawGalacticCenter();
			screen.setChanges();
		}
		if(ins.consume(KeyEvent.VK_I)){
			toggleInventoryScreen();
			screen.setChanges();
		}
		if(ins.consume(KeyEvent.VK_Q)){
			
			screen.zoomAllTheWayOut();
		}
		if(ins.consume(KeyEvent.VK_E)){
			screen.zoomAllTheWayIn();
		}
		if(ins.consume(KeyEvent.VK_I)){
			toggleInventoryScreen();
			screen.setChanges();
		}
		tickSaveInput();
		tickLoadInput();
		tickNewGameInput();
		tickInventoryInputs();
		if(ins.getClickEvent() != null){
			if(invOpen()){
				if(gd.getPlayer().getInventory().deselect()){
					screen.setChanges();
				}
			}
			clickUniverse(screen.getClickCoord(ins.getClickEvent()));
		}
		tickScroll();
		if(uiMap.hasWaiting()){
			if(uiMap.getPressedNotReleased()){
				uiMap.getWaiting().UIClickAt(this, uiMap.getClick());
			}else{
				uiMap.getWaiting().UIReleaseAt(this, uiMap.getClick());
			}
			uiMap.clearWaiting();
		}
	}
	
	private void tickNewGameInput() {
		if(ins.consume(KeyEvent.VK_F7)){
			if(screen.queryYesNo("Confirm New Game", "Are you sure you wish to start a new game?\r\nUnsaved progress of the current game will be lost.")){
				screen.killFrame();
				Starter.newGame();
				screen.showMessage("New Game started successfully.");
				zoomInAnimate();
			}
		}
	}
	
	private void tickSaveInput() {
		if(ins.consume(KeyEvent.VK_F5)){
			if(screen.queryYesNo("Confirm Load", "Are you sure you wish to save the game?\r\nSave file will permanently overwrite a previous save file.")){
				String saveFileName = screen.getSaveFileName(saverLoader.getLastUsedLoadFileName());
				if(saveFileName == null){
					screen.showMessage("Save cancelled.");
				}else{
					int code = saverLoader.save(gd, saveFileName);
					if(code == 0){
						saverLoader.setLastUsedLoadFileName(saveFileName);
						screen.showMessage("Game saved successfully.");
					}else{
						screen.showMessage("Game failed to save to '" + saverLoader.getSavePath() + File.separatorChar + saveFileName + "'.");
					}
				}
			}
		}
	}
	
	private void tickLoadInput() {
		if(ins.consume(KeyEvent.VK_F6)){
			if(screen.queryYesNo("Confirm Load", "Are you sure you wish to attempt load the game\r\nfrom a save file?\r\nUnsaved progress of the current game will be lost.")){
				File folder = new File(saverLoader.getSavePath());
				List<String> fileNames = new ArrayList<>();
				for(File f : folder.listFiles()){
					if(f.getName().endsWith(SaverLoader.EXTENSION)){
						fileNames.add(f.getName().substring(0, f.getName().length()-6));
					}
				}
				if(fileNames.size() == 0){
					screen.showMessage("No save files found at directory\r\n'" + saverLoader.getSavePath() + File.separatorChar + '\'');
					return;
				}
				String loadFileName = screen.getLoadFileName(fileNames.toArray(new String[fileNames.size()]));
				if(loadFileName == null){
					screen.showMessage("Load cancelled.");
					return;
				}
				try{
					GameData newGd = saverLoader.load(loadFileName);
					if(newGd != null){
						gd = newGd;
						Cheater.init(newGd);
						updateInventoryState();
						screen.setGameData(gd);
						screen.setView(gd.getPlayer());
						ins.reset();
						uiMap.clearUI();
						screen.setChanges();
						Log.clear();
						saverLoader.setLastUsedLoadFileName(loadFileName);
						screen.showMessage("Game loaded successfully.");
						zoomInAnimate();
					}else{
						screen.showMessage("Game failed to load from '" + saverLoader.getSavePath() + File.separatorChar + loadFileName + "'.");
					}
				}catch(Exception e){
					screen.loadFailed();
				}
			}
		}
	}
	private void tickScroll() {
		if(ins.getScroll() < 0 || ins.consume(KeyEvent.VK_UP)){
			screen.scrollIn();
		}else if(ins.getScroll() > 0 || ins.consume(KeyEvent.VK_DOWN)){
			if(screen.getZoom() > 2_500/gd.getPlayer().getMaxDistanceDiameterRatio()){
				screen.scrollOut();
			}
		}
	}

	private void tickInventoryInputs() {
		if(ins.consume(KeyEvent.VK_DELETE)){
			if(playerInventoryUp() && gd.getPlayer().getInventory().hasItemSelected()
					&& screen.queryYesNo("Confirm Delete Item", "Are you sure you want to permanently delete this item?")){
				if(gd.getPlayer().getInventory().deleteSelected()){
					screen.setChanges();
				}
			}
		}
		updateInventoryState();
	}
	private boolean playerInventoryUp() {
		return uiMap.isMapped(gd.getPlayer().getInventory());
	}

	private void updateInventoryState() {
		gd.getPlayer().checkEquipment();
	}

	private void toggleInventoryScreen() {
		if(!invOpen()){
			uiMap.add(gd.getPlayer().getInventory().getButton(), gd.getPlayer().getInventory());
		}else{
			uiMap.remove(gd.getPlayer().getInventory().getButton());
		}
		screen.setChanges();
	}
	
	public  void clickUniverse(Point click) {
		Beacon b = getClickActiveBeacon(click);
		if(b != null && gd.getPlayer().distanceTo(b.getBeaconPoint()) > 0.0001 &&
				screen.queryYesNo("Confirm Beacon Jump", "Do you wish to execute a jump to\r\n"
				+ "the target beacon? The beacon will be damaged.")){
			Log.logBeaconJump();
			jumpShipTo(b.getBeaconPoint());
			b.majorWear();
			return;
		}
		for(Star s : gd.getUniverse().starsWithSystemsAt(click)){
			if(s != null){
				Planet nearestClickPlanet = click.nearestOther(s.getPlanets());
				if(nearestClickPlanet != null){
					Feature nearestClickFeature = click.nearestOther(nearestClickPlanet.getFeatures());
					if(nearestClickFeature != null && clickDistance(click, nearestClickFeature) &&
							withinBody(gd.getPlayer(), nearestClickPlanet) && screen.getZoom() > Frame.SURFACE_ZOOM){
						clickFEATURE(nearestClickFeature, nearestClickPlanet);
						return;
					}
					if(clickDistance(click, nearestClickPlanet)
							 && gd.getPlayer().withinAstrometerRange(nearestClickPlanet)){
						clickPLANET(nearestClickPlanet);
						return;
					}
				}
				
			}
		}
		Star nearestStar = click.nearestOther(gd.getUniverse().getStars());
		if(clickDistance(click, nearestStar) && gd.getPlayer().withinAstrometerRange(nearestStar)){
			clickSTAR(nearestStar);
		}
		
	}
	private Beacon getClickActiveBeacon(Point click) {
		Beacon closest = null;
		double distance = Double.MAX_VALUE;
		for(Beacon b : gd.getPlayer().getActiveBeacons()){
			double dist = click.distanceTo(b.getBeaconPoint());
			if(dist < 0.03/screen.getZoom() && dist < distance){
				closest = b;
				distance = dist;
			}
		}
		return closest;
	}
	
	private boolean withinBody(Point p, Body b) {
		return p.distanceTo(b) <= b.getRadius();
	}

	private boolean clickSTAR(Star clickStar) {
		return tryUseEnginesToJumpTo(clickStar);
	}

	private boolean clickPLANET(Planet clickPlanet) {
		return tryUseEnginesToJumpTo(clickPlanet);
	}

	private void clickFEATURE(Feature feat, Planet featurePlanet) {
		if(!invOpen()){
			toggleInventoryScreen();
		}
		if(!withinBody(gd.getPlayer(), feat)){
			jumpToFeature(feat);
		}else{
			interactWithFeature(feat, featurePlanet);
		}
	}

	private void interactWithFeature(Feature feat, Planet featurePlanet) {
		if(feat.interact(this)){
			gd.getPlayer().wearAndTear();
			screen.setChanges();
		}
	}

	@Override
	public boolean usePlayerLifeSupport() {
		return gd.getPlayer().useLifeSupport();
	}
	
	private void jumpToFeature(Feature feat) {
		closeUIExceptInventory();
		jumpShipTo(feat);
		screen.setChanges();
	}
	
	private boolean clickDistance(Point mouseLocation, Body body) {
		return screen.clickDistance(mouseLocation, body);
	}

	private boolean tryUseEnginesToJumpTo(Body b) {
		double distance = b.distanceTo(gd.getPlayer());
		if(distance > gd.getPlayer().getMaxJumpRange()){
			Log.destinationOutofRange();
			changesTrue();
			return false;
		}
		if(withinBody(gd.getPlayer(), b)){
			return false;
		}
		int ableIndex = gd.getPlayer().getInventory().firstAbleEngineIndex(distance);
		if(ableIndex >= 0){
			Engine eng = gd.getPlayer().getInventory().getNthEngine(ableIndex);
			executeJumpTo(b, eng);
			return true;
		}
		Log.noResourcesForJump(b);
		changesTrue();
		return false;
	}
	
	private void executeJumpTo(Body b, Engine eng) {
		ElementStack engCost = eng.getFuelCost();
		ElementStack spent = null;
		closeUIExceptInventory();
		if(Fun.chance(eng.getChance())){
			gd.getPlayer().getInventory().remove(engCost);
			spent = engCost;
		}
		jumpShipTo(b);
		Log.logJump(b, spent);
		eng.wearAndTear();
		gd.getPlayer().wearAndTear();
		
		screen.setChanges();
	}
	
	private void jumpShipTo(Point b) {
		Point start = gd.getPlayer().clone();
		double dir = start.directionTo(b);
		double dist = start.distanceTo(b);
		int animationSteps = (int)Math.pow((dist / Engine.MIN_RANGE),.65) + 4;
		
		for(int i = 0; i < animationSteps; i++){
			gd.getPlayer().jumpTo(start.dirOffset(dir, dist*i/(animationSteps-1)));
			screen.setChanges();
			screen.paint();
			sleep(30);
		}
	}
	
	private void closeUIExceptInventory() {
		boolean invOpen = invOpen();
		uiMap.clearUI();
		if(invOpen){
			toggleInventoryScreen();
		}
	}

	private boolean invOpen() {
		return playerInventoryUp();
	}
	
	private void sleep(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void addUIButton(UIButton b, HasUIButton h) {
		uiMap.add(b, h);
	}
	@Override
	public int addToShip(Item es) {
		int remain = gd.getPlayer().addToInv(es);
		if(remain > 0){
			Log.logInventoryFull(remain);
		}
		return remain;
	}
	@Override
	public boolean shipContains(Item x) {
		return gd.getPlayer().getInventory().contains(x);
	}
	@Override
	public int removeFromShip(Item x) {
		return gd.getPlayer().getInventory().remove(x);
	}
	@Override
	public void changesTrue() {
		screen.setChanges();
	}
	@Override
	public Inventory getPlayerInventory() {
		return gd.getPlayer().getInventory();
	}
	@Override
	public void activateBeacon(Beacon b, Point beaconPoint) {
		b.setBeaconPoint(beaconPoint);
		gd.getPlayer().addActiveBeacon(b);
	}
	@Override
	public void deactivateBeacon(Beacon b) {
		gd.getPlayer().removeActiveBeacon(b);
	}
	@Override
	public int getScreenHeight() {
		return screen.getheight();
	}
	@Override
	public void addPulsing(Pulses pulses) {
		currentlyPulsing.add(pulses);
	}
	@Override
	public void resetButtons(Feature caller) {
		closeUIExceptInventory();
		if(caller != null){
			caller.interact(this);
		}
	}
	@Override
	public Inventory swapShipInventoryWith(Inventory inv) {
		return gd.getPlayer().swapInventoryWith(inv);
	}
	@Override
	public void clearUIButtons() {
		
		uiMap.clearUI();
		
	}
	@Override
	public void zoomFullyIn() {
		screen.zoomAllTheWayIn();
		screen.setChanges();
	}
	@Override
	public void checkShipEquipment() {
		gd.getPlayer().checkEquipment();
	}
}
