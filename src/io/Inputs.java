package io;

import java.util.HashMap;
import java.util.Map;

public class Inputs {
	private Map<Integer, Boolean> keys;
	private ScreenPoint clickEvent;
	private int scroll;
	
	public Inputs(){
		scroll = 0;
		keys = new HashMap<>();
	}
	
	public void press(int c){
		keys.put(c, true);
	}
	
	public void release(int c){
		keys.remove(c);
	}
	
	public boolean isPressed(int c){
		return keys.get(c) != null;
	}
	
	public boolean consume(int c){
		boolean pressed = isPressed(c);
		release(c);
		return pressed;
	}
	
	public void reset(){
		clickEvent = null;
		scroll = 0;
		keys.clear();
	}

	public void click(ScreenPoint e) {
		clickEvent = e;
	}
	
	public ScreenPoint getClickEvent(){
		return clickEvent;
	}
	
	public void scroll(int rotation){
		scroll = rotation;
	}
	
	public int getScroll(){
		return scroll;
	}
}
