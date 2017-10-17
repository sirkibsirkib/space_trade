package surface;

import io.HasUIButton;
import io.UIButton;
import player.Beacon;
import player.Inventory;
import player.Item;
import player.Pulses;
import universe.Point;

public interface FeatureInteractor {
	boolean usePlayerLifeSupport();
	int addToShip(Item es);
	void addUIButton(UIButton b, HasUIButton h);
	boolean shipContains(Item es);
	int removeFromShip(Item es);
	void changesTrue();
	Inventory getPlayerInventory();
	void activateBeacon(Beacon b, Point beaconPoint);
	void deactivateBeacon(Beacon b);
	int getScreenHeight();
	void addPulsing(Pulses pulses);
	void resetButtons(Feature caller);
	Inventory swapShipInventoryWith(Inventory inv);
	void clearUIButtons();
	void zoomFullyIn();
	void checkShipEquipment();
}
