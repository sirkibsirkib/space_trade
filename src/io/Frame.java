package io;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import main.GameData;
import player.Beacon;
import player.Ship;
import surface.Feature;
import universe.Body;
import universe.Planet;
import universe.Point;
import universe.Star;
import universe.Universe;
import abstracts.Fun;
import abstracts.Log;

@SuppressWarnings("serial")
public class Frame extends JFrame{
	public static final int
							PLANETS_ZOOM = 60,
							SYSTEM_ZOOM = 250,
							SURFACE_ZOOM = 3000,
							MIN_WIDTH = 1_000,
							MIN_HEIGHT = 700;
	public static final double MAX_ZOOM_IN = 400_000,
							MAX_ZOOM_OUT = 0.9;
	public static final Color ALPHA = null;
	public static Color[] jumpRangeColors;
	public static final Color ALMOST_BLACK = new Color(25,25,25);
	private UIMap uiMap;
	
	private PlayerInterface pi;
	
	private static Painter painter;
	
	public static int getPainterWidth(){
		return painter.getWidth();
	}
	
	public static int getPainterHeight(){
		return painter.getHeight();
	}
	
	public int getSmallerDimension(){
		return Fun.min(getWidth(), getHeight());
	}
	
	public int getLargerDimension(){
		return Fun.max(getWidth(), getHeight());
	}
	
	private Ship getPlayer(){
		return pi.getPlayer();
	}
	
	private GameData getGameData(){
		return pi.getGameData();
	}
	
	private Point getView(){
		return pi.getView();
	}
	
	public Frame(Inputs ins, UIMap uiMap, boolean fullscreen, PlayerInterface pi){
		this.pi = pi;
		this.uiMap = uiMap;
		jumpRangeColors = new Color[10];
		for(int i = 0; i < 10; i++){
			jumpRangeColors[i] = new Color((i*100)%240, (i*160 + 30)%240, (i*65 + 200)%240);
		}
		setSize(MIN_WIDTH, MIN_HEIGHT);
		setTitle("Space Trade");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		
		Listener l = new Listener(ins, uiMap);
		addKeyListener(l);
		painter = new Painter(l);
		add(painter, BorderLayout.CENTER);
		
		getContentPane().setBackground(Color.BLACK);
		if(fullscreen){
			setUndecorated(true);
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		setVisible(true);
	}
	
	class Painter extends JComponent implements Renders{
		private Graphics2D ggg;
		public Font mediumFont = new Font("Arial", Font.BOLD, 18);
		public Font smallFont = new Font("Arial", Font.BOLD, 14);

		public Painter(Listener l) {
			addMouseListener(l);
			addMouseWheelListener(l);
		}

		@Override
		public void update(Graphics g){
			paintComponent(g);
		}
		
		@Override
		public void paintComponent(Graphics g){
			ggg = (Graphics2D) g;
			ggg.setFont(mediumFont);
			if(pi.drawGalacticCenter()){
				drawGalacticCenter();
			}
			if(pi.drawBeacons()){
				drawBeaconLines();
			}
			if(pi.drawJumps()){
				paintJumps();
			}
			if(pi.drawOrbits()){
				paintSolarSystems();
			}
			
			paintStars();
			
			if(pi.getZoom() > PLANETS_ZOOM){
				paintPlanets();
				getPlayer().render(this);
			}
			
			uiMap.render(this);
			Log.render(this);
		}
		
		private void drawGalacticCenter() {
			painter.renderCircle(Point.ZERO, getPlayer().distanceTo(Point.ZERO), 0, ALMOST_BLACK, false);
		}

		private void paintSolarSystems() {
			for(Star s : pi.getGameData().getUniverse().stars){
				if(s.distanceTo(getPlayer()) > .03){
					continue;
				}
				for(Planet p : s.getPlanets()){
					if(getPlayer().withinAstrometerRange(p)){
						painter.renderCircle(p.getParentStar(), p.distanceTo(p.getParentStar()), 0, Color.DARK_GRAY, false);
					}
				}
			}
		}

		private boolean withinFrame(Point p, double frameFactor){
			if(p instanceof Body){
				Body b = (Body) p;
				return p.rectDistanceTo(getPlayer()) < (frameFactor*getLargerDimension()/getSmallerDimension())/pi.getZoom() + b.getRadius();
			}
			return p.rectDistanceTo(getPlayer()) < (frameFactor*getLargerDimension()/getSmallerDimension())/pi.getZoom();
		}

		private void drawBeaconLines() {
			for(Beacon b : getPlayer().getActiveBeacons()){
				if(b.getBeaconPoint() != null){
					boolean reach = b.getSignalRadius() >= getPlayer().distanceTo(b.getBeaconPoint());
					Point drawPoint = reach?
							b.getBeaconPoint() :
							getPlayer().dirOffset(getPlayer().directionTo(b.getBeaconPoint()), b.getSignalRadius());
					drawLineBetween(getPlayer(), drawPoint, b.getCol());
					if(reach){
						renderCircle(drawPoint, 0.00015, b.getSignalRadius()/.003+1, b.getCol(), false);
					}
				}
			}
		}

		private void paintJumps() {
			double[] engineRanges = getPlayer().getInventory().engineRanges();
			for(int i = 0; i < engineRanges.length; i++){
				renderCircle(getPlayer(), engineRanges[i], 0, jumpRangeColors[i], false);
				renderText(getPlayer().offset(0, engineRanges[i]), "" + (char)(i+'A'), jumpRangeColors[i], true);
			}
			for(Star s : getGameData().getUniverse().getStars()){
				if(s.rectDistanceTo(getPlayer()) < 1.4/pi.getZoom()){
					if(getPlayer().angleMeasure(s) > 1/getPlayer().getAstrometerSensitivity()){
						paintJumpPoint(s);
					}
					for(Planet p : s.getPlanets()){
						if(p.rectDistanceTo(getPlayer()) < 1.4/pi.getZoom() &&
								getPlayer().withinAstrometerRange(p)){
							paintJumpPoint(p);
						}
					}
				}
			}
		}

		private void paintJumpPoint(Point p){
			int engIndex = getPlayer().getInventory().firstAbleEngineIndex(getPlayer().distanceTo(p));
			if(engIndex >= 0){
				drawLineBetween(getPlayer(), p, jumpRangeColors[engIndex]);
			}
		}

		private void paintPlanets() {
			Universe u = getGameData().getUniverse();
			for(Star s : u.getStars()){
				if(s.distanceTo(getPlayer()) > 0.02){
					continue;
				}
				for(Planet p : s.getPlanets()){
					if(pi.getZoom() > PLANETS_ZOOM && p.manhattan(getPlayer()) < 0.02){
						p.render(this);
						if(getPlayer().isWithin(p) && pi.getZoom() > SURFACE_ZOOM){
							paintFeatures(p);
						}else if(pi.getZoom() > SYSTEM_ZOOM){
							paintDistantFeatures(p);
						}
					}
				}
			}
		}

		private void paintDistantFeatures(Planet p) {
			for(Feature f : p.getFeatures()){
				f.render(this, 0);
			}
		}

		private void paintFeatures(Planet p) {
			for(Feature f : p.getFeatures()){
				f.render(this, 1);
			}
		}

		private void paintStars() {
			for(Star s : getGameData().getUniverse().stars){
				if(withinFrame(s, 2)){
					paintStar(s);
				}
			}
		}


		private void paintStar(Star s) {
			int drawCloseup = pi.getZoom() > SYSTEM_ZOOM? 1 : 0;
			double ddRatioRatio = getPlayer().getMaxDistanceDiameterRatio() / s.distanceDiameterRatio(getPlayer());
			int showCol = ddRatioRatio < 1? -1 :
				ddRatioRatio < 2? 0 : 1;
			s.render(this, drawCloseup, showCol);
		}
		
		private void drawLineBetween(Point a, Point b, Color col){
			ScreenPoint sa = onScreen(a);
			ScreenPoint sb = onScreen(b);
			Shape drawLine = new Line2D.Double(sa.getX(), sa.getY(), sb.getX(), sb.getY());
			ggg.setPaint(col);
			ggg.draw(drawLine);
		}
		

		
		////// experiment

		@Override
		public void renderCircle(Point c, double radius, double minWidth, Color col, boolean fill) {
			renderCircle(onScreen(c), screenDistance(radius), minWidth, col, fill);
		}

		@Override
		public void renderCircle(ScreenPoint sc, double radius, double minWidth, Color col, boolean fill) {
			double sWidth = Fun.max(minWidth, radius);
			if(sc.getX() < getWidth() || sc.getY() < getHeight() || sc.getX()+sWidth+sWidth > 0 || sc.getY()+sWidth+sWidth > 0){
				Shape drawEllipse = new Ellipse2D.Double(sc.getX()-sWidth/2, sc.getY()-sWidth/2, sWidth, sWidth);
				ggg.setPaint(col);
				if(fill){
					ggg.fill(drawEllipse);
				}else{
					ggg.draw(drawEllipse);
				}
			}
		}

		@Override
		public void renderText(Point c, String text, Color col, boolean small) {
			renderText(onScreen(c), text, col, small);
		}

		@Override
		public void renderText(ScreenPoint sc, String text, Color col, boolean small) {
			int xOffset = 1;
			int yOffset = 11;
			ggg.setPaint(Color.BLACK);
			if(small){
				ggg.setFont(smallFont);
			}
			ggg.drawString(text, sc.getX()+xOffset-1, sc.getY()+yOffset-1);
			ggg.setPaint(col);
			ggg.drawString(text, sc.getX()+xOffset, sc.getY()+yOffset);
			if(small){
				ggg.setFont(mediumFont);
			}
		}

		@Override
		public void renderRect(Point tl, Point br, Color col, boolean fill) {
			renderRect(onScreen(tl), onScreen(tl), col, fill);
		}

		@Override
		public void renderRect(ScreenPoint tl, ScreenPoint br, Color col, boolean fill) {
			Shape square = new Rectangle2D.Float(tl.getX(), tl.getY(), br.getX()-tl.getX(), br.getY()-br.getY());
			ggg.setPaint(col);
			if(fill){
				ggg.fill(square);
			}else{
				ggg.draw(square);
			}
		}

		@Override
		public void renderRect(ScreenPoint tl, int width, int height, Color col, boolean fill) {
			Shape square = new Rectangle2D.Float(tl.getX(), tl.getY(), width, height);
			ggg.setPaint(col);
			if(fill){
				ggg.fill(square);
			}else{
				ggg.draw(square);
			}
		}

		@Override
		public void renderTriangle(ScreenPoint one, ScreenPoint two, ScreenPoint three, Color col, boolean fill) {
			int[] x3 = new int[3];
			x3[0] = one.getX();
			x3[1] = two.getX();
			x3[2] = three.getX();
			int[] y3 = new int[3];
			y3[0] = one.getY();
			y3[1] = two.getY();
			y3[2] = three.getY();
			Shape square = new Polygon(x3, y3, 3);
			ggg.setPaint(col);
			if(fill){
				ggg.fill(square);
			}else{
				ggg.draw(square);
			}
		}
		
		public ScreenPoint onScreen(Point c){
			return new ScreenPoint(
				(int) ((c.getX()- getView().getX())*pi.getZoom()*(getSmallerDimension()/2D) + (getWidth()/2)),
				(int) ((c.getY()- getView().getY())*pi.getZoom()*(getSmallerDimension()/2D) + (getHeight()/2)));
		}
		
		public double screenDistance(double realDistance){
			return realDistance*pi.getZoom()*getSmallerDimension();
		}

		@Override
		public int getScreenHeight() {
			return getHeight();
		}

		@Override
		public int getScreenWidth() {
			return getWidth();
		}
		
		Point getClickCoord(ScreenPoint sc){
			return new Point((1.0*(sc.getX()) - (getWidth()/2)) /(pi.getZoom()*(getSmallerDimension()/2D)) + getView().getX()
					, (1.0*(sc.getY()) - (getHeight()/2)) /(pi.getZoom()*(getSmallerDimension()/2D)) + getView().getY());
		}

		@Override
		public void renderEllipse(Point center, double width, double height, Color col, boolean fill) {
			renderEllipse(onScreen(center), screenDistance(width), screenDistance(height), col, fill);
		}

		@Override
		public void renderEllipse(ScreenPoint center, double width, double height, Color col, boolean fill) {
			Shape drawEllipse = new Ellipse2D.Double(center.getX()-width/2, center.getY()-height/2, width, height);
			ggg.setPaint(col);
			if(fill){
				ggg.fill(drawEllipse);
			}else{
				ggg.draw(drawEllipse);
			}
		}

		@Override
		public void renderArc(Point c, double radius, double minRadius, double extent, double start, Color col, boolean fill) {
			renderArc(onScreen(c), screenDistance(radius), minRadius, extent, start, col, fill);
		}
		
		@Override
		public void renderArc(ScreenPoint sc, double radius, double minRadius, double extent, double start, Color col, boolean fill) {
			double sWidth = Fun.max(minRadius, radius);
			if(sc.getX() < getWidth() || sc.getY() < getHeight() || sc.getX()+sWidth+sWidth > 0 || sc.getY()+sWidth+sWidth > 0){
				Shape drawArc2D3 = new Arc2D.Double(sc.getX()-sWidth/2, sc.getY()-sWidth/2, sWidth, sWidth, start, extent,  fill?Arc2D.PIE:Arc2D.OPEN);
				ggg.setPaint(col);
				if(fill){
					ggg.fill(drawArc2D3);
				}else{
					ggg.draw(drawArc2D3);
				}
			}
		}

	}
	
	public Point getClickCoord(ScreenPoint sc){
		return painter.getClickCoord(sc);
	}

	public void showHelp() {
		JOptionPane.showMessageDialog(null, "Welcome to SpaceTrade version ??\n"
				+ "    \nTo get started, open your inventory with 'I'. Then select an item with the cursor.\n"
				+ "Selected items can be moved with WASD. Move en engine into an open engine slot.\n"
				+ "Once you have an engine in a slot, you can click on stars and planets to move around.\n"
				+ "Use the mouse wheel to pi.getZoom() in and out. Different pi.getZoom() levels determine what you move to.\n"
				+ "    \nUsing engines to reach stars/planets requires and consumes fuel. Engines display what and how much.\n"
				+ "Fuel is shown as 'ful: n-m' where n displays cost per burn. and m displays 1/m probability of the burn per jump.\n"
				+ "Over time, engines deteriorate, using more fuel. Engines have different range capabilities. Your ship will attempt\n"
				+ "to use Engine A before it attempts to use Engine B. So order the engines in a way that suits you.\n"
				+ "    \nTraveling between features of a single planet does not use fuel. click around on features to interact with them.\n"
				+ "Yellow features represent caves. they can be mined for semi-random resources a fixed number of times before closing\n"
				+ "White features represent fields. They can be continuously foraged for the planet's primary resource.\n"
				+ "Forgaging or mining consumes one element per click. The element will be taken from the first slot available\n"
				+ "as seen from top left to bottom right of your inventory."
				+ "Blue features represent depots. each depot offers a trade of resource A --> B. You can accept the trade\n"
				+ "if you can afford it as many times as you like. With each purchase, the price will inflate.");
	}
}

//         OTHER PAINT STUFF

//gg.setBackground(Color.BLACK);

//Shape xxx = new Ellipse2D.Double(30,30,30,30);
//graph2.fill(xxx);
//graph2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//Shape drawLine = new Line2D.Float(20, 90, 55, 250);
//Shape drawArc2D = new Arc2D.Double(5, 150, 100, 100, 45, 180, Arc2D.OPEN);
//Shape drawArc2D2 = new Arc2D.Double(5, 200, 100, 100, 45, 45, Arc2D.CHORD);
//Shape drawArc2D3 = new Arc2D.Double(5, 250, 100, 100, 45, 45, Arc2D.PIE);
//Shape drawRect = new Rectangle2D.Float(300, 300, 150, 100);
//
//graph2.draw(drawArc2D2);
//graph2.draw(drawArc2D3);
//graph2.setColor(Color.GREEN);
//graph2.fill(drawRect);
