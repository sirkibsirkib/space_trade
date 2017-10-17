package abstracts;

import player.Beacon;
import player.Item;
import surface.Feature;

public interface Saver {
	void mapObject(long key, Object value);
	Object getObject(long key);
	long getKeyFor(Object o);
	long freeKey();
	Item loadItem(String data, int dIndex);
	Feature loadFeature(String data, int dIndex);
	void activateBeacon(Beacon b);
}
