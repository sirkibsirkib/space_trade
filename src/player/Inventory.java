package player;

import io.Frame;
import io.HasUIButton;
import io.RectButton;
import io.Renderable;
import io.Renders;
import io.ScreenPoint;
import io.UIButton;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import surface.FeatureInteractor;
import universe.Element;
import abstracts.Fun;
import abstracts.Savable;
import abstracts.Saver;


public class Inventory implements Renderable, HasUIButton, Savable{
	private static final int CELL_SIZE = Item.p100;
	
	private ScreenPoint tl;
	private Item[][] stacks;
	private Slot selectedSlot;
	private List<Slot> blocked;
	private String drawTitleString;
	private UIButton myUIButton;
	
	private boolean itemPickedUp;
	
	public Inventory(int xStacks, int yStacks, ScreenPoint tl, String drawTitleString){
		stacks = new Item[xStacks][yStacks];
		blocked = new ArrayList<>();
		this.tl = tl;
		this.drawTitleString = drawTitleString;
		if(tl != null){
			setMyUIButton(tl);
		}
		itemPickedUp = false;
	}

	private void setMyUIButton(ScreenPoint tl) {
		myUIButton = new RectButton(tl, stacks.length*CELL_SIZE, stacks[0].length*CELL_SIZE);
	}
	
	public void expand(){
		if(blocked.size() > 0){
			blocked.remove(0);
		}
		//TODO make it expand even more
	}
	
	public void block(int i, int j){
		if(!blocked(i, j)){
			blocked.add(new Slot(i, j));
		}
	}
	
	public boolean getItemPickedUp(){
		return itemPickedUp;
	}
	
	public int add(Item x){
		if(x instanceof Stackable){
			Stackable remainder = addStackable(((Stackable) x).duplicate());
			if(remainder == null){
				return 0;
			}
			return remainder.getNumber();
		}else{
			return addUnstackable(x);
		}
	}
		
	
	private int addUnstackable(Item x) {
		for(int i = 0; i < stacks.length; i++){
			for(int j = 0; j < stacks[0].length; j++){
				if(blocked(i, j)){
					continue;
				}
				if(stacks[i][j] == null){
					stacks[i][j] = x;
					return 0;
				}
			}
		}
		return 1;
	}

	private Stackable addStackable(Stackable es) {
		if(es == null){
			return null;
		}
		es = addToExisting(es);
		if(es != null){
			for(Slot s : listEmptySlots()){
				if(!blocked(s)){
					if(!es.isOverCap()){
						stacks[s.x][s.y] = es;
						return null;
					}
					stacks[s.x][s.y] = es.removeUpToCap();
				}
				
			}
		}
		return es;
	}

	private boolean blocked(Slot s) {
		return blocked(s.x, s.y);
	}

	private List<Slot> listEmptySlots() {
		List<Slot> slots = new ArrayList<>();
		for(int i = 0; i < stacks.length; i++){
			for(int j = 0; j < stacks[0].length; j++){
				if(stacks[i][j] == null){
					slots.add(new Slot(i, j));
				}
			}
		}
		return slots;
	}

	private Stackable addToExisting(Stackable es){
		for(Stackable x : listStackables()){
			if(x instanceof Stackable){
				Stackable s = (Stackable) x;
				es = s.tryAddReturnRemain(es);
				if(es == null){
					return null;
				}
			}
		}
		return es;
	}

	public int getXStacks() {
		return stacks.length;
	}
	
	public int getYStacks() {
		return stacks[0].length;
	}

	public Item getStackAt(int i, int j) {
		return stacks[i][j];
	}

	public boolean contains(Item x) {
		if(x instanceof Stackable){
			return containsStackable((Stackable) x);
		}else{
			//TODO
			throw new Error();
		}
	}
	
	private boolean containsStackable(Stackable x) {
		int count = 0;
		for(Stackable s : listStackables()){
			if(s.matches(x)){
				count += s.getNumber();
			}
		}
		return count >= x.getNumber();
	}

	public double[] engineRanges(){
		List<Double> ranges = new ArrayList<>();
		double max = 0;
		for(Item x : listItems()){
			if(x instanceof Engine){
				Engine eng = (Engine) x;
				if(eng.range > max){
					ranges.add(eng.range);
					max = eng.range;
				}
			}
		}
		double[] array = new double[ranges.size()];
		for(int i = 0; i < ranges.size(); i++){
			array[i] = ranges.get(i);
		}
		return array;
	}
	
	private Slot findSmallestStackOf(Element e){
		int least = Integer.MAX_VALUE;
		Slot smallest = null;
		for(int i = 0; i < stacks.length; i++){
			for(int j = 0; j < stacks[0].length; j++){
				if(stacks[i][j] instanceof ElementStack){
					ElementStack next = (ElementStack) stacks[i][j];
					if(next.getElement() == e && least > next.getNumber()){
						smallest = new Slot(i, j);
						least = next.getNumber();
					}
				}
			}
		}
		return smallest;
	}
	
	public int remove(Item x) {
		if(x instanceof ElementStack){
			return removeElementStack((ElementStack) x);
		}else{
			//TODO add non-es item
			throw new Error();
		}
	}
	
	private int removeElementStack(ElementStack es) {
		if(es == null){
			return 0;
		}
		int remain = es.getNumber();
		while(true){
			Slot slot = findSmallestStackOf(es.getElement());
			if(slot == null){
				return remain;
			}
			ElementStack smallest = (ElementStack) stacks[slot.x][slot.y];
			int remove = Fun.min(smallest.getNumber(), remain);
			smallest.remove(remove);
			if(smallest.getNumber() == 0){
				stacks[slot.x][slot.y] = null;
			}
			remain -= remove;
			if(remain == 0){
				return 0;
			}
		}
	}
	
	@Override
	public void render(Renders r, int... info) {
		r.renderText(new ScreenPoint(tl.getX(), tl.getY()-18), drawTitleString, Color.WHITE, false);
		r.renderRect(new ScreenPoint(tl.getX()-3, tl.getY()-25), CELL_SIZE*stacks.length+6, CELL_SIZE*stacks[0].length+28, Color.WHITE, false);
//		r.renderRect(new ScreenPoint(tl.getX(), tl.getY()), CELL_SIZE*stacks.length, CELL_SIZE*stacks[0].length, Color.GRAY, true);
		boolean lifeSupportFound = false;
		boolean astrometerFound = false;
		int enginesFound = 0;
		for(int i = 0; i < stacks.length; i++){
			for(int j = 0; j < stacks[0].length; j++){
				if(!blocked(i,j)){
					r.renderRect(new ScreenPoint(tl.getX() + CELL_SIZE*i, tl.getY() + CELL_SIZE*j), CELL_SIZE, CELL_SIZE, Color.GRAY, true);
				}
				Item item = stacks[i][j];
				r.renderRect(new ScreenPoint(tl.getX() + CELL_SIZE*i, tl.getY() + CELL_SIZE*j), CELL_SIZE, CELL_SIZE, Color.BLACK, false);
				if(item instanceof LifeSupport && !lifeSupportFound){
					r.renderRect(new ScreenPoint(tl.getX() + CELL_SIZE*i, tl.getY() + CELL_SIZE*j), CELL_SIZE, CELL_SIZE, Color.WHITE, true);
					lifeSupportFound = true;
				}
				if(item instanceof Astrometer && !astrometerFound){
					r.renderRect(new ScreenPoint(tl.getX() + CELL_SIZE*i, tl.getY() + CELL_SIZE*j), CELL_SIZE, CELL_SIZE, Color.WHITE, true);
					astrometerFound = true;
				}
				if(item != null){
					item.render(r, tl.getX() + CELL_SIZE*i, tl.getY() + CELL_SIZE*j);
				}
				if(item instanceof Engine){
					r.renderRect(new ScreenPoint(tl.getX() + CELL_SIZE*i+Item.p70, tl.getY() + CELL_SIZE*j), Item.p30, Item.p30, Frame.jumpRangeColors[enginesFound], true);
					r.renderText(new ScreenPoint(tl.getX() + CELL_SIZE*i+Item.p70, tl.getY() + CELL_SIZE*j+Item.p10), (char)('A' + enginesFound) + "", Color.BLACK, false);
					enginesFound++;
				}
			}
		}
		if(selectedSlot != null){
			r.renderRect(new ScreenPoint(tl.getX()-Item.p5 + CELL_SIZE*selectedSlot.x, tl.getY()-Item.p5 + CELL_SIZE*selectedSlot.y), Item.p110, Item.p110, Color.YELLOW, false);
		}
	}
	
	public static Inventory generateInventoryWithSlots(int numSlots, ScreenPoint tl, String drawTitleString){
		int width = (int) (Math.sqrt(numSlots)+.5);
		int height = (int) Math.ceil(1.0*numSlots/width);
		int blocked = width*height - numSlots;
		Inventory inv = new Inventory(width, height, tl, drawTitleString);
		for(int x = 0; x < blocked; x++){
			inv.block(width-1, height-1 - x);
		}
		return inv;
	}
	
	private boolean blocked(int x, int y) {
		for(int i = 0; i < blocked.size(); i++){
			if(blocked.get(i).x == x && blocked.get(i).y == y){
				return true;
			}
		}
		return false;
	}

	private Item getSelected(){
		if(selectedSlot == null){
			return null;
		}
		return stacks[selectedSlot.x][selectedSlot.y];
	}

	public void clickAt(ScreenPoint sc) {
		int selectedX = (sc.getX()-tl.getX())/CELL_SIZE;
		int selectedY = (sc.getY()-tl.getY())/CELL_SIZE;
		if(selectedX < stacks.length && selectedY < stacks[0].length && stacks[selectedX][selectedY] != null){
			selectedSlot = new Slot(selectedX, selectedY);
			itemPickedUp = true;
		}else{
			selectedSlot = null;
		}
	}
	
	public boolean deselect(){
		boolean hadSomethingSelected = selectedSlot != null;
		selectedSlot = null;
		return hadSomethingSelected;
	}

	public boolean moveSelectedItem(int xMove, int yMove) {
		if(getSelected() != null){
			if(withinStackIndexes(selectedSlot.x+xMove, selectedSlot.y+yMove)){
				Slot movedSlot = selectedSlot.offset(xMove, yMove);
				Item other = stacks[movedSlot.x][movedSlot.y];
				stacks[movedSlot.x][movedSlot.y] = getSelected();
				stacks[selectedSlot.x][selectedSlot.y] = other;
				selectedSlot = movedSlot;
				return true;
			}
		}
		return false;
	}
	
	private boolean withinStackIndexes(int i, int j) {
		return i >= 0 && i < stacks.length &&
				j >= 0 && j < stacks[0].length;
	}

	public boolean deleteSelected() {
		if(getSelected() != null){
			stacks[selectedSlot.x][selectedSlot.y] = null;
			selectedSlot = null;
			return true;
		}
		return false;
	}
	
	public boolean hasItemSelected(){
		return getSelected() != null;
	}
	
	public void swap(int x, int y, int q, int w){
		try{
			Item hold = stacks[x][y];
			stacks[x][y] = stacks[q][w];
			stacks[q][w] = hold;
		}catch(IndexOutOfBoundsException e){
			return;
		}
	}
	
	
	
	///// INNER //////
	



	private class Slot{
		int x;
		int y;
		
		Slot(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		Slot offset(int xOffset, int yOffset){
			return new Slot(x + xOffset, y + yOffset);
		}
	}
	
	public boolean useLifeSupport(LifeSupport ls){
		ElementStack fuel = ls.getFuel();
		if(contains(fuel)){
			if(Fun.chance(ls.getChance())){
				remove(fuel);
			}
			ls.wearAndTear();
			return true;
		}
		return false;
	}
	
	public Item removeSelected(){
		if(selectedSlot == null){
			return null;
		}
		Item x = stacks[selectedSlot.x][selectedSlot.y];
		stacks[selectedSlot.x][selectedSlot.y] = null;
		selectedSlot = null;
		return x;
	}

	@Override
	public void UIClickAt(FeatureInteractor fi, ScreenPoint sp) {
		clickAt(sp);
		fi.changesTrue();
	}

	@Override
	public Renderable[] getButtonVisual() {
		return new Renderable[]{this};
	}
	
	public UIButton getButton(){
		return myUIButton;
	}

	public String save(Saver saver, char[] delims, int dIndex) {
		String string = "" + stacks.length + delims[dIndex] + stacks[0].length +
				delims[dIndex] + tl.getX() + delims[dIndex] + tl.getY() + delims[dIndex] + drawTitleString;
		for(int i = 0; i < stacks.length; i++){
			for(int j = 0; j < stacks[0].length; j++){
				string += delims[dIndex];
				if(stacks[i][j] != null){
					string += stacks[i][j].save(saver, delims, dIndex+1);
				}else{
					string += ' ';
				}
			}	
		}
		return string + delims[dIndex];
	}

	@Override
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		Inventory inv = new Inventory(scan.nextInt(), scan.nextInt(), new ScreenPoint(scan.nextInt(), scan.nextInt()), scan.next());
		for(int i = 0; i < inv.stacks.length; i++){
			for(int j = 0; j < inv.stacks[0].length; j++){
				inv.stacks[i][j] = saver.loadItem(scan.next(), dIndex+1);
			}	
		}
		inv.setMyUIButton(inv.tl);
		scan.close();
		return inv;
	}

	public List<Beacon> getActiveBeacons() {
		List<Beacon> beacons = new ArrayList<>();
		for(int i = 0; i < stacks.length; i++){
			for(int j = 0; j < stacks[0].length; j++){
				if(stacks[i][j] != null && stacks[i][j] instanceof Beacon){
					beacons.add((Beacon) stacks[i][j]);
				}
			}
		}
		return beacons;
	}
	
	public int firstAbleEngineIndex(double range){
		int index = 0;
		for(int i = 0; i < stacks.length; i++){
			for(int j = 0; j < stacks[0].length; j++){
				if(stacks[i][j] instanceof Engine){
					Engine next = (Engine) stacks[i][j];
					if(next.getJumpRange() >= range && contains(next.getFuelCost())){
						return index;
					}
					index++;
				}
			}
		}
		return -1;
	}
	
	public Engine getNthEngine(int n){
		int index = 0;
		for(int i = 0; i < stacks.length; i++){
			for(int j = 0; j < stacks[0].length; j++){
				if(stacks[i][j] instanceof Engine){
					Engine next = (Engine) stacks[i][j];
					if(index == n){
						return next;
					}
					index++;
				}
			}
		}
		return null;
	}

	public List<Item> listItems() {
		List<Item> itemList = new ArrayList<>();
		for(int i = 0; i < stacks.length; i++){
			for(int j = 0; j < stacks[0].length; j++){
				if(blocked(i, j)){
					continue;
				}
				if(stacks[i][j] != null){
					itemList.add(stacks[i][j]);
				}
			}
		}
		return itemList;
	}
	
	public List<Stackable> listStackables() {
		List<Stackable> stackables = new ArrayList<>();
		for(Item x : listItems()){
			if(x instanceof Stackable){
				stackables.add((Stackable) x);
			}
		}
		return stackables;
	}

	public void empty() {
		for(int i = 0; i < stacks.length; i++){
			for(int j = 0; j < stacks[0].length; j++){
				stacks[i][j] = null;
				selectedSlot = null;
			}
		}
	}
	
	public ScreenPoint getTL(){
		return tl;
	}
	
	public String getDrawTitleString(){
		return drawTitleString;
	}
	
	public void reskin(ScreenPoint tl, String drawTitleString){
		this.tl = tl;
		this.drawTitleString = drawTitleString;
		setMyUIButton(tl);
	}

	@Override
	public void UIReleaseAt(FeatureInteractor fi, ScreenPoint sc) {
		int selectedX = (sc.getX()-tl.getX())/CELL_SIZE;
		int selectedY = (sc.getY()-tl.getY())/CELL_SIZE;
		if(selectedSlot != null && itemPickedUp && selectedX < stacks.length &&
				selectedY < stacks[0].length && !blocked(selectedX, selectedY)){
			swap(selectedX, selectedY, selectedSlot.x, selectedSlot.y);
			itemPickedUp = false;
			selectedSlot.x = selectedX;
			selectedSlot.y = selectedY;
			fi.changesTrue();
		}
	}
	
	public int countItems(){
		int count = 0;
		for(Item x : listItems()){
			if(x != null){
				count++;
			}
		}
		return count;
	}

	public int getNumBlocked() {
		return blocked.size();
	}
}
