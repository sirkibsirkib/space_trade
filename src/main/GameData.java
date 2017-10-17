package main;

import java.util.Scanner;

import player.Inventory;
import player.Ship;
import universe.Point;
import universe.Universe;
import abstracts.Savable;
import abstracts.Saver;

public class GameData implements Savable {
	private Universe u;
	private Ship player;
	
	public GameData(Inventory inv){
		u = new Universe();
		player = new Ship(u.startPosition(), inv);
	}

	public Ship getPlayer() {
		return player;
	}

	public Universe getUniverse() {
		return u;
	}
	
	public String save(Saver saver, char[] delims, int dIndex) {
		return u.save(saver, delims, dIndex+1) + delims[dIndex] + player.save(saver, delims, dIndex+1);
	}
	
	public Object load(Saver saver, char[] delims, int dIndex, String data) {
		Scanner scan = new Scanner(data);
		scan.useDelimiter(delims[dIndex]+"");
		GameData gd = new GameData(null);
		gd.u = (Universe) new Universe().load(saver, delims, dIndex+1, scan.next());
		gd.player = (Ship) new Ship(Point.ZERO, null).load(saver, delims, dIndex+1, scan.next());
		scan.close();
		return gd;
	}
}
