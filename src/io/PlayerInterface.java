package io;

import main.GameData;
import player.Ship;
import universe.Point;

public interface PlayerInterface {
	double getZoom();
	Ship getPlayer();
	GameData getGameData();
	Point getView();
	boolean drawBeacons();
	boolean drawJumps();
	boolean drawOrbits();
	boolean drawGalacticCenter();
}
