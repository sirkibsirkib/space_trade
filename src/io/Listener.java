package io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Listener implements KeyListener, MouseListener, MouseWheelListener{
	private Inputs ins;
	private UIMap uiMap;

	public Listener(Inputs ins, UIMap uiMap){
		this.ins = ins;
		this.uiMap = uiMap;
	}
	
	public void keyPressed(KeyEvent e) {
		ins.press(e.getKeyCode());
		e.consume();
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		ins.scroll(e.getWheelRotation());
		e.consume();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//huh. Only called if cursor is stationary
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		ScreenPoint sc = new ScreenPoint(e.getX(), e.getY());
		if(!uiMap.clickAt(sc, true)){ //tries to click button, returns if button clicked
			ins.click(sc);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		ScreenPoint sc = new ScreenPoint(e.getX(), e.getY());
		if(!uiMap.clickAt(sc, false)){ //tries to click button, returns if button clicked
//			ins.click(sc);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
