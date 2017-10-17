package io;

public class RectButton implements UIButton{
	private int width, height;
	private ScreenPoint topLeft;
	
	public RectButton(ScreenPoint topLeft, int width, int height){
		this.topLeft = topLeft;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public boolean isClickedAt(ScreenPoint sc) {
		return sc.getX() >= topLeft.getX() && sc.getX() <= topLeft.getX()+width &&
				sc.getY() >= topLeft.getY() && sc.getY() <= topLeft.getY()+height;
	}
}