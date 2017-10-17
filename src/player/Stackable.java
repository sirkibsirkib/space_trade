package player;

public interface Stackable extends Item{
	public int getStackCap();
	public int getNumber();
	public Stackable tryAddReturnRemain(Stackable s);
	public Stackable removeUpToCap();
	public boolean isOverCap();
	public boolean matches(Stackable s);
	public Stackable duplicate();
}
