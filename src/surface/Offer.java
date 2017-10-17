package surface;

import io.RectButton;
import io.Renderable;
import io.Renders;
import io.ScreenPoint;
import io.UIButton;

import java.awt.Color;
import java.util.Scanner;

import player.ElementStack;
import player.Engine;
import player.Item;
import abstracts.Fun;
import abstracts.Savable;
import abstracts.Saver;

public class Offer implements Renderable, Savable{
	public static final double INFLATION = 0.18;
	ScreenPoint tl;
	private Item price;
	private Item reward;
	private double costValue;
	
	public Offer(Item price, Item reward, ScreenPoint tl, double costValue){
		this.tl = tl;
		this.price = price;
		this.reward = reward;
		this.costValue = costValue;
	}

	public Item getReward() {
		return reward;
	}

	public Item getPrice() {
		return price;
	}
	
	public int worsenOffer(){
		if(price instanceof ElementStack){
			ElementStack ePrice = (ElementStack) price;
			ePrice.add((int) Math.ceil(ePrice.getNumber()*INFLATION) + 3);
			return 0;
		}else if (reward instanceof ElementStack){
			ElementStack eReward = (ElementStack) price;
			if(eReward.getNumber() > 1){
				eReward.remove((int) Math.ceil(eReward.getNumber()*INFLATION));
				return 0;
			}
		}
		return 1;
	}

	@Override
	public void render(Renders r, int... info) {
		r.renderText(tl.offset(0, -19), "Trader offer:", Color.WHITE, false);
		r.renderRect(tl.offset(-Item.p5, -25), Item.p100*3+Item.p5, Item.p100+28, Color.WHITE, false);
		r.renderRect(tl, Item.p100*3, Item.p100, Color.GRAY, true);
		r.renderRect(tl.offset(Item.p100+Item.p10, 4), Item.p80,  Item.p80, Color.BLACK, true);
		r.renderText(tl.offset(Item.p100+Item.p10, Item.p40), "ACCEPT", Color.WHITE, true);
		price.render(r, tl.getX(), tl.getY());
		reward.render(r, tl.getX()+Item.p100*2, tl.getY());
	}

	@Override
	public String save(Saver saver, char[] delims, int dIndex) {
		return "" + tl.getX() + delims[dIndex] + tl.getY() + delims[dIndex] + price.save(saver, delims, dIndex+1) +
				delims[dIndex] + reward.save(saver, delims, dIndex+1) + delims[dIndex] + costValue;
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		ScreenPoint sp = new ScreenPoint(scan.nextInt(), scan.nextInt());
		Item p = saver.loadItem(scan.next(), dIndex+1);
		Item r = saver.loadItem(scan.next(), dIndex+1);
		Offer o = new Offer(p, r, sp, scan.nextDouble());
		scan.close();
		return o;
	}

	public UIButton getOfferButton() {
		return new RectButton(tl.offset(Item.p100, 0), Item.p100, Item.p100);
	}
	
	public String getOfferStringSummary(){
		return itemStringSummary(price) + itemStringSummary(reward);
	}
	
	public String itemStringSummary(Item x){
		String s = x.getClass().getName();
		if(x instanceof ElementStack){
			s += ((ElementStack) x).getElement().getSymbol();
		}else if(x instanceof Engine){
			s += ((Engine) x).getFuelCost().getElement().getSymbol();
		}
		return s;
	}

	public boolean onButton(ScreenPoint sp) {
		return sp.manhattan(tl.offset(Item.p100+Item.p50, Item.p50)) < Item.p100;
	}
}
