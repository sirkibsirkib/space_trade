package player;

import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.Scanner;

import universe.Point;
import abstracts.Fun;
import abstracts.Saver;

public class Astrometer implements Item {
	private int sensitivity, drawRad;
	
	public Astrometer(int sensitivity){
		this.sensitivity = sensitivity;
		drawRad = (int) (p30/Math.pow(sensitivity,.2)+1);
	}
	
	public void render(Renders r, int... info) {
		ScreenPoint sp = new ScreenPoint(info[0], info[1]);
		r.renderRect(sp, p90, p100, Color.DARK_GRAY, true);
		r.renderRect(new ScreenPoint(info[0]+p50, info[1]+p10), p5, p60, Color.BLUE, true);
		r.renderCircle(new ScreenPoint(info[0]+p50, info[1]+p70), drawRad, 0, Color.GREEN, true);
		r.renderText(sp, "Astrometer", Color.WHITE, true);
		r.renderText(sp.offset(0, p30), "" + sensitivity, Color.WHITE, false);
	}

	public double getSensitivity() {
		return sensitivity;
	}

	@Override
	public Item generateItemWithValueAt(Point p, double value) {
		return new Astrometer((int) (Math.pow(value, .96)) + Ship.BASE_ASTROMETER_LIMIT);
	}
	
	public void wearAndTear(){
		if(Fun.chance(300/sensitivity+4) && sensitivity > Ship.BASE_ASTROMETER_LIMIT){
			sensitivity--;
		}
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "astro" + delims[dIndex] + sensitivity;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Astrometer ast = new Astrometer(scan.nextInt());
		scan.close();
		return ast;
	}
}
