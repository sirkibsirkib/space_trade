package io;

import java.util.HashMap;
import java.util.Map;

public class UIMap implements Renderable{
	private Map<UIButton, HasUIButton> map;
	private HasUIButton waiting;
	private ScreenPoint clickAt;
	private boolean pressedNotReleased;
	
	public UIMap(){
		map = new HashMap<>();
	}
	
	public void add(UIButton b, HasUIButton h){
		map.put(b, h);
	}
	
	public void remove(UIButton b){
		map.remove(b);
	}
	
	public boolean clickAt(ScreenPoint click, boolean pressedNotReleased){
		clickAt = click;
		this.pressedNotReleased = pressedNotReleased;
		for(UIButton u : map.keySet()){
			if(u.isClickedAt(click)){
				waiting = map.get(u);
				return true;
			}
		}
		return false;
	}
	
	public void render(Renders r, int... info) {
		for(UIButton u : map.keySet()){
			if(map.get(u).getButtonVisual() == null){
				continue;
			}
			for(Renderable x : map.get(u).getButtonVisual()){
				x.render(r);
			}
		}
	}
	
	public boolean hasWaiting(){
		return waiting != null;
	}
	
	public HasUIButton getWaiting(){
		return waiting;
	}
	
	public void clearWaiting(){
		waiting = null;
	}
	
	public void clearUI(){
		map.clear();
	}
	
	public boolean getPressedNotReleased(){
		return pressedNotReleased;
	}
	
	public ScreenPoint getClick(){
		return clickAt;
	}

	public boolean isMapped(HasUIButton h) {
		return map.containsValue(h);
	}

	public boolean isClear() {
		return map.size() == 0;
	}
}
