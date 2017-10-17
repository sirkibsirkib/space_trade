package surface;

import io.HasUIButton;
import io.RectButton;
import io.Renderable;
import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.Scanner;

import player.Beacon;
import player.Inventory;
import player.Item;
import player.Pulses;
import universe.Planet;
import abstracts.Fun;
import abstracts.Log;
import abstracts.Saver;

public class Vault extends Feature implements HasUIButton, Pulses {
	private Inventory inv;
	public final static String DRAW_TITLE_STRING = "Vault";
	private ScreenPoint tl;
	private Arrow lArrow, rArrow;
	private boolean pulsing;
	private double drawSize;

	public Vault(double x, double y, Planet parent) {
		super(x, y, Feature.RADIUS, parent);
		tl = new ScreenPoint(Item.p100*6+100, 50);
		inv = Inventory.generateInventoryWithSlots(1 + Fun.rng(6), tl, DRAW_TITLE_STRING);
		lArrow = new Arrow(true, tl.offset(-100, 0));
		rArrow = new Arrow(false, tl.offset(-100, 40));
		pulsing = false;
		if(parent != null){
			calcDrawSize();
		}
	}

	private void calcDrawSize() {
		drawSize = RADIUS * (Math.sqrt(inv.getXStacks() * inv.getYStacks() + 2))/2.5;
	}

	@Override
	public void render(Renders r, int... info) {
		if(info[0] == 1){
			double drawWidth = pulsing? drawSize*.8 : drawSize;
			r.renderCircle(this, drawWidth, 0, Color.RED, true);
			r.renderCircle(this, drawWidth, 0, Color.DARK_GRAY, false);
		}else{
			r.renderCircle(this, drawSize/2, 0, Color.RED, true);
		}
	}

	@Override
	public void UIClickAt(FeatureInteractor fi, ScreenPoint sp) {
		inv.UIClickAt(fi, sp);
	}

	@Override
	public Renderable[] getButtonVisual() {
		return inv.getButtonVisual();
	}

	@Override
	public boolean interact(FeatureInteractor fi) {
		fi.addUIButton(inv.getButton(), this);
		lArrow.addUIButton(fi);
		rArrow.addUIButton(fi);
		fi.addPulsing(this);
		pulsing = true;
		return true;
	}
	
	private Vault getOuter(){
		return this;
	}
	
	private class Arrow implements HasUIButton, Renderable{
		boolean left;
		ScreenPoint tl;
		
		Arrow(boolean left, ScreenPoint tl){
			this.left = left;
			this.tl = tl;
		}

		public void addUIButton(FeatureInteractor fi) {
			fi.addUIButton(new RectButton(tl, 50, 30), this);
		}

		@Override
		public void render(Renders r, int... info) {
			r.renderRect(tl, 50, 30, Color.WHITE, true);
			if(left){
				r.renderTriangle(tl.offset(-15, 15), tl.offset(0, 0), tl.offset(0, 30), Color.WHITE, true);
			}else{
				r.renderTriangle(tl.offset(65, 15), tl.offset(50, 0), tl.offset(50, 30), Color.WHITE, true);
			}
		}

		@Override
		public void UIClickAt(FeatureInteractor fi, ScreenPoint sp) {
			if(left){
				Item x = inv.removeSelected();
				if(x != null){
					Log.vaultWithdrawLog(x);
					fi.getPlayerInventory().add(x);
					if(x instanceof Beacon){
						fi.deactivateBeacon((Beacon) x);
						Log.vaultDeactivateBeacon(parent);
					}
				}
			}else{
				Item x = fi.getPlayerInventory().removeSelected();
				if(x != null){
					Log.vaultDepositLog(x);
					inv.add(x);
					if(x instanceof Beacon){
						fi.activateBeacon((Beacon) x, getOuter());
						Log.vaultActivateBeacon(parent);
					}
				}
			}
			fi.changesTrue();
		}

		@Override
		public Renderable[] getButtonVisual() {
			return new Renderable[]{this};
		}

		@Override
		public void UIReleaseAt(FeatureInteractor fi, ScreenPoint sp) {
			// TODO Auto-generated method stub
			
		}
	}

	public Inventory getInventory() {
		return inv;	
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "vault" + delims[dIndex] + x + delims[dIndex] + y + delims[dIndex] + inv.save(saver, delims, dIndex+1);
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Vault vault = new Vault(scan.nextDouble(), scan.nextDouble(), null);
		vault.inv = (Inventory) new Inventory(1, 1, null, null).load(saver, delims, dIndex+1, scan.next());
		for(Beacon b : vault.inv.getActiveBeacons()){
			saver.activateBeacon(b);
			b.setBeaconPoint(vault);
		}
		vault.calcDrawSize();
		scan.close();
		return vault;
	}

	@Override
	public void pulseOff() {
		pulsing = false;
	}

	@Override
	public boolean isPulsingNow() {
		return pulsing;
	}

	@Override
	public void UIReleaseAt(FeatureInteractor fi, ScreenPoint sp) {
		//nothing
	}
}
