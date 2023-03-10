package com.game.areas;

import java.util.ArrayList;
import java.util.List;

import com.game.graphics.Renderer;
import com.game.utilities.Vector;

/**
 * <h1>AreasSupport</h1> This class creates and initialises all the components
 * of the areas that will be used in the game
 */
public class AreasSupport {

	private List<Areas> areas = new ArrayList<Areas>();

	public AreasSupport() {

	}

	public void createAreas(Vector canvasSize, Renderer gameRenderer) {
		areas.add(new Area11());
		areas.add(new Area01());
		areas.add(new Area10());
		areas.add(new Area12());
		areas.add(new Area21());
		for (int i = 0; i < areas.size(); i++) {
			areas.get(i).obstacles(canvasSize);
			areas.get(i).monstersOffline(canvasSize, gameRenderer);
			areas.get(i).itemsOffline(canvasSize, gameRenderer);
		}
	}
}
