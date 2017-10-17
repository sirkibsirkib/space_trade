package io;

import io.Frame.Painter;

import java.awt.Color;

import javax.swing.JOptionPane;

import main.Cheater;
import main.GameData;
import player.Ship;
import universe.Body;
import universe.Point;
import abstracts.Fun;
import abstracts.Log;
import abstracts.Tipper;

public class Screen implements PlayerInterface{
	public static final double
			MAX_ZOOM_IN = 400_000,
			MAX_ZOOM_OUT = 0.9;

	private static final double SCROLL_FACTOR = 1.3;

	public static Color[] jumpRangeColors;
	
	GameData gd;
	Inputs ins;
	UIMap uiMap;
	
	Painter painter;
	Frame frame;
	double zoom;
	Point view;
	
	boolean changes;
	boolean fullscreen;
	
	boolean drawJumps;
	boolean drawBeacons;
	boolean drawOrbits;
	boolean drawGalacticCenter;
	
	public Screen(GameData gd, Inputs ins, UIMap uiMap){
		setupJumpRangeColors();
		this.gd = gd;
		this.ins = ins;
		this.uiMap = uiMap;
		zoom = MAX_ZOOM_OUT;
		frame = new Frame(ins, uiMap, false, this);
		view = gd.getPlayer();
		changes = false;
		fullscreen = false;
		setDrawModes(false);
	}

	public void setDrawModes(boolean b) {
		drawBeacons = drawJumps = drawOrbits = drawGalacticCenter = b;
	}

	private void setupJumpRangeColors() {
		jumpRangeColors = new Color[10];
		for(int i = 0; i < 10; i++){
			jumpRangeColors[i] = new Color((i*100)%240, (i*160 + 30)%240, (i*65 + 200)%240);
		}
	}

	public void paint() {
		if(changes){
			frame.repaint();
		}
		changes = false;
	}
	
	public void setChanges(){
		changes = true;
	}

	public void toggleFullscreen() {
		killFrame();
		fullscreen = !fullscreen;
		frame = new Frame(ins, uiMap, fullscreen, this);
	}

	public void toggleDrawBeacons(){
		drawBeacons = !drawBeacons;
		if(drawBeacons)	Log.genericLog("Beacons shown.", Color.CYAN);
		else			Log.genericLog("Beacons hidden.", Color.CYAN);
	}
	
	public void toggleJumpRanges(){
		drawJumps = !drawJumps;
		if(drawJumps)	Log.genericLog("Jump ranges shown.", Color.CYAN);
		else			Log.genericLog("Jump ranges hidden.", Color.CYAN);
	}
	
	public void toggleOrbitLines(){
		drawOrbits = !drawOrbits;
		if(drawOrbits)	Log.genericLog("Orbit lines shown.", Color.CYAN);
		else			Log.genericLog("Orbit lines hidden.", Color.CYAN);
	}
	


	public void toggleDrawGalacticCenter() {
		drawGalacticCenter = !drawGalacticCenter;
		if(drawGalacticCenter)	Log.genericLog("Galactic center shown.", Color.CYAN);
		else					Log.genericLog("Galactic center hidden.", Color.CYAN);
	}

	public void zoomAllTheWayOut() {
		double outLimit = 2_500/gd.getPlayer().getMaxDistanceDiameterRatio();
		zoom = Fun.max(MAX_ZOOM_OUT,outLimit);
		changes = true;
	}
	
	public void zoomAllTheWayIn() {
		zoom = MAX_ZOOM_IN;
		changes = true;
	}

	@Override
	public double getZoom() {
		return zoom;
	}

	public Point getClickCoord(ScreenPoint clickEvent) {
		return frame.getClickCoord(clickEvent);
	}

	public void scrollIn() {
		if(zoom < MAX_ZOOM_IN){
			changes = true;
			zoom = Fun.min(MAX_ZOOM_IN, zoom*SCROLL_FACTOR);
		}
	}
	
	public void scrollOut() {
		if(zoom > MAX_ZOOM_OUT){
			changes = true;
			zoom = Fun.max(MAX_ZOOM_OUT, zoom/SCROLL_FACTOR);
		}
	}

	public boolean clickDistance(Point mouseLocation, Body body) {
		return mouseLocation.distanceTo(body) < body.getRadius() + 0.03/zoom;
	}

	public int getheight() {
		return Frame.getPainterHeight();
	}

	public void setGameData(GameData newGd) {
		gd = newGd;
	}

	public void setView(Ship pt) {
		view = pt;
	}
	
	

	@Override
	public Ship getPlayer() {
		return gd.getPlayer();
	}

	@Override
	public GameData getGameData() {
		return gd;
	}

	@Override
	public Point getView() {
		return view;
	}
	
	public void showMessage(String s){
		JOptionPane.showMessageDialog(frame, s);
	}
	
	public boolean queryYesNo(String titleBar, String text){
		int n = JOptionPane.showConfirmDialog(frame, text, titleBar, JOptionPane.YES_NO_OPTION);
		return n == 0;
	}

	public void loadFailed() {
		
	}

	@Override
	public boolean drawBeacons() {
		return drawBeacons;
	}

	@Override
	public boolean drawJumps() {
		return drawJumps;
	}

	@Override
	public boolean drawOrbits() {
		return drawOrbits;
	}
	
	@Override
	public boolean drawGalacticCenter(){
		return drawGalacticCenter;
	}

	public boolean queryTipsReturnDone() {
		String query = (String)JOptionPane.showInputDialog(
		                    frame,
		                    "Enter text to find relevant tip entries.\r\n"
		                    + "Matching entries will be presented as a list.\r\n"
		                    + "Enter empty string for the list of all tips.",
		                    "Tip Search",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    null,
		                    "astrometer");
		if(query == null){
			return true;
		}
		if(Cheater.tryCheatCode(query)){
			changes = true;
			return true;
		}
		String[] options = Tipper.entriesMatching(query.toLowerCase());
		if(options.length == 0){
			JOptionPane.showMessageDialog(frame, "No results!");
			return false;
		}
		String selection = (String)JOptionPane.showInputDialog(
                frame,
                "The following (" + options.length + ") tip entries were found:",
                "Tip Entries Found",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                null);
		if(selection == null){
			return true;
		}
		String[] buttons = { "Another Query", "Done"};    
		int returnValue = JOptionPane.showOptionDialog(null, Tipper.get(selection), "Selection: " + selection,
		        JOptionPane.PLAIN_MESSAGE, 0, null, buttons, buttons[0]);
		return returnValue == 1;
	}
	
	public void killFrame(){
		frame.setVisible(false);
		frame.dispose();
	}

	public String getSaveFileName(String defaultLoadName) {
		return (String)JOptionPane.showInputDialog(
                frame,
                "Enter a file name for your save.",
                "Save file name",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                defaultLoadName);
	}
	
	public String getLoadFileName(String[] fileNames) {
		return (String)JOptionPane.showInputDialog(
                frame,
                "(" + fileNames.length + ") save files were found:",
                "Load save file",
                JOptionPane.PLAIN_MESSAGE,
                null,
                fileNames,
                null);
	}

	public void toggleAllDrawModes(boolean b) {
		drawBeacons = drawJumps = drawOrbits = b;
	}

	public void completeZoomOut() {
		zoom = 1;
	}
}
