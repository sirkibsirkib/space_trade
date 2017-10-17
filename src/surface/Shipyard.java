package surface;

import io.HasUIButton;
import io.RectButton;
import io.Renderable;
import io.Renders;
import io.ScreenPoint;

import java.awt.Color;
import java.util.Scanner;

import player.ElementStack;
import player.Inventory;
import player.Item;
import player.Pulses;
import universe.Planet;
import abstracts.Fun;
import abstracts.Log;
import abstracts.Saver;

public class Shipyard extends Feature implements HasUIButton, Pulses {
	private Inventory inv;
	public final static String DRAW_TITLE_STRING = "Shipyard";
	private ScreenPoint tl;
	private WithdrawButton withdraw;
	private PurchaseButton purchase;
	private boolean pulsing, purchased;
	private double drawSize, value;
	private ElementStack cost;

	public Shipyard(double x, double y, Planet parent) {
		super(x, y, Feature.RADIUS, parent);
		tl = new ScreenPoint(Item.p100*6+100, 50);
		inv = new Inventory(Fun.rng(3)+1, 1+Fun.rng(2), tl, DRAW_TITLE_STRING);
		withdraw = new WithdrawButton(tl.offset(-100, 0));
		purchase = new PurchaseButton(tl.offset(-Item.p100*2-30, 40));
		pulsing = false;
		if(parent != null){
			value = 50 + Fun.rdg((distanceTo(ZERO) + 0.15)*1_000);
			cost = (ElementStack) new ElementStack(null, 0).generateItemWithValueAt(this, value);
			int numSlots = (int) (Math.pow(value, .38)*2.1)+4;
			inv = Inventory.generateInventoryWithSlots(numSlots, tl, "Shipyard Stock:");
			purchased = false;
			calcDrawSize();
		}
	}
	
	private void calcDrawSize(){
		drawSize = RADIUS * (Math.sqrt(inv.getXStacks()*inv.getYStacks() - inv.getNumBlocked()) + 4)/10;
	}

	@Override
	public void render(Renders r, int... info) {
		if(info[0] == 1){
			double drawWidth = pulsing? drawSize*.8 : drawSize;
			r.renderCircle(this, drawWidth, 0, Color.GREEN, true);
			r.renderCircle(this, drawWidth, 0, Color.DARK_GRAY, false);
		}else{
			r.renderCircle(this, drawSize/2, 0, Color.GREEN, true);
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
		if(purchased){
			withdraw.addUIButton(fi);
		}else{
			purchase.addUIButton(fi);
		}
		fi.addPulsing(this);
		pulsing = true;
		return true;
	}
	
	private Shipyard getOuter(){
		return this;
	}
	
	private class PurchaseButton implements HasUIButton, Renderable{
		ScreenPoint tl;
		
		PurchaseButton(ScreenPoint tl){
			this.tl = tl;
		}

		public void addUIButton(FeatureInteractor fi) {
			fi.addUIButton(new RectButton(tl.offset(Item.p100, 0), Item.p100, Item.p100), this);
		}

		@Override
		public void render(Renders r, int... info) {
			r.renderText(tl.offset(0, -19), "Buy Cost:", Color.WHITE, false);
			r.renderRect(tl.offset(-Item.p5, -25), Item.p100*2+Item.p5, Item.p100+28, Color.WHITE, false);
			r.renderRect(tl, Item.p100*2, Item.p100, Color.GRAY, true);
			r.renderRect(tl.offset(Item.p100+Item.p10, 4), Item.p80,  Item.p80, Color.BLACK, true);
			r.renderText(tl.offset(Item.p100+Item.p10, Item.p40), "ACCEPT", Color.WHITE, true);
			cost.render(r, tl.getX(), tl.getY());
		}

		@Override
		public void UIClickAt(FeatureInteractor fi, ScreenPoint sp) {
			if(fi.shipContains(cost)){
				fi.removeFromShip(cost);
				fi.addPulsing(getOuter());
				fi.zoomFullyIn();
				inv.reskin(inv.getTL(), "Old Ship:");
				inv = fi.swapShipInventoryWith(inv);
//				fi.closeAllUI();
				//TODO when you click inv doesn't close
				fi.resetButtons(null);
				Log.shipPurchased(cost);
				purchased = true;
			}else{

				Log.cantAffordShipPurchase(cost);
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
	
	private class WithdrawButton implements HasUIButton, Renderable{
		ScreenPoint tl;
		
		WithdrawButton(ScreenPoint tl){
			this.tl = tl;
		}

		public void addUIButton(FeatureInteractor fi) {
			fi.addUIButton(new RectButton(tl, 50, 30), this);
		}

		@Override
		public void render(Renders r, int... info) {
			r.renderRect(tl, 50, 30, Color.WHITE, true);
			r.renderTriangle(tl.offset(-15, 15), tl.offset(0, 0), tl.offset(0, 30), Color.WHITE, true);
		}

		@Override
		public void UIClickAt(FeatureInteractor fi, ScreenPoint sp) {
			Item x = inv.removeSelected();
			if(x != null){
				fi.getPlayerInventory().add(x);
				Log.shipyardWithdraw(x);
				fi.checkShipEquipment();
				if(inv.countItems() == 0){
					purchased = false;
					fi.clearUIButtons();
					Log.shipyardStorageEmpty();
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
		return "shipy" + delims[dIndex] + x + delims[dIndex] + y + delims[dIndex] + (purchased?1:0) + delims[dIndex] +
				value + delims[dIndex] + inv.save(saver, delims, dIndex+1) + delims[dIndex] + cost.save(saver, delims, dIndex+1);
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		scan.next();
		Shipyard ship = new Shipyard(scan.nextDouble(), scan.nextDouble(), null);
		ship.purchased = scan.next().equals("1")?true:false;
		ship.value = scan.nextDouble();
		ship.inv = (Inventory) new Inventory(1, 1, null, null).load(saver, delims, dIndex+1, scan.next());
		ship.cost = (ElementStack) new ElementStack(null, 0).load(saver, delims, dIndex+1, scan.next());
		ship.calcDrawSize();
		scan.close();
		return ship;
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
