package io;


public class ScreenPoint {
	private int x, y;

	public ScreenPoint(int x, int y){
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	@Override
	public ScreenPoint clone(){
		return new ScreenPoint(x, y);
	}

	public ScreenPoint offset(int i, int j) {
		return new ScreenPoint(x+i, y+j);
	}
	
	public boolean withinBounds(){
		return x >= 0 && x < Frame.getPainterWidth() && y >= 0 && y < Frame.getPainterHeight();
	}

	public void print() {
		System.out.println(x + "   " + y);
	}

	public double manhattan(ScreenPoint other) {
		return Math.abs(x-other.x) + Math.abs(y-other.y); 
	}
	
}
