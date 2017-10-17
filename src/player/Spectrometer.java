package player;

import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.Scanner;

import universe.Point;
import abstracts.Saver;

public class Spectrometer implements Item {
	private double factor;
	
	public Spectrometer(double factor){
		this.factor = factor;
	}
	
	public void render(Renders r, int... info) {
		ScreenPoint sp = new ScreenPoint(info[0], info[1]);
		r.renderRect(sp, p100, p100, Color.DARK_GRAY, true);
		r.renderRect(sp.offset(0, p50), p100, p40, Color.BLACK, true);

		r.renderCircle(sp.offset(p70, p80), p10, 0, Color.RED, true);
		r.renderCircle(sp.offset(p80, p80), p10, 0, Color.GREEN, true);
		r.renderCircle(sp.offset(p90, p80), p10, 0, Color.BLUE, true);
		r.renderText(sp, "Spectro-", Color.WHITE, true);
		r.renderText(sp.offset(0, p20), "meter", Color.WHITE, true);
		r.renderText(sp.offset(0, p60), String.format("%1$,.2fx", factor), Color.WHITE, false);
	}

	@Override
	public Item generateItemWithValueAt(Point p, double value) {
		double tryFactor = Math.pow(value, .85)*0.025 + 1;
		return new Spectrometer(tryFactor);
	}

	@Override
	public void wearAndTear() {
		factor = ((factor-1)*.998)+1;
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "spect" + delims[dIndex] + factor;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Spectrometer nav = new Spectrometer(scan.nextDouble());
		scan.close();
		return nav;
	}

	public double getFactor() {
		return factor;
	}
}
