package com.game.areas;

import java.util.Random;

import com.game.entities.items.ArrowItem;
import com.game.entities.items.BowArrowItem;
import com.game.entities.items.DefenceItem;
import com.game.entities.items.RegenItem;
import com.game.entities.items.RestoreHealthItem;
import com.game.entities.items.RestoreMPItem;
import com.game.entities.items.SpeedItem;
import com.game.entities.items.SwordItem;
import com.game.graphics.Renderer;
import com.game.utilities.Vector;

public class ItemSpawner {
	/**
	 * Randomly adds an item to the game renderer at a spawn position.
	 * 
	 * @param gameRenderer
	 * @param spawnPos
	 */

	public static void spawnItem(Vector spawnPos, int screenId, Renderer renderer) {
		Random rand = new Random();
		switch (rand.nextInt(7)) {
		case 0:
			ArrowItem arrowItem = new ArrowItem(spawnPos, screenId, renderer);
			break;
		// TODO addSprite
		case 1:
			SwordItem swordItem = new SwordItem(spawnPos, screenId, renderer);
			// TODO addSprite
			break;
		case 2:
			BowArrowItem bowItem = new BowArrowItem(spawnPos, screenId, renderer);
			// TODO addSprite
			break;
		case 3:
			switch (rand.nextInt(2)) {
			case 0:
				RestoreHealthItem healthItem = new RestoreHealthItem(spawnPos, screenId, renderer);
				// TODO addSprite
				break;
			case 1:
				RegenItem regenItem = new RegenItem(spawnPos, screenId, renderer);
				// TODO addSprite
				break;
			}
			break;
		case 4:
			DefenceItem defenceItem = new DefenceItem(spawnPos, screenId, renderer);
			// TODO addSprite
			break;
		case 5:
			SpeedItem speedItem = new SpeedItem(spawnPos, screenId, renderer);
			// TODO addSprite
			break;
		case 6:
			RestoreMPItem mpItem = new RestoreMPItem(spawnPos, screenId, renderer);
			// TODO addSprite
			break;
		}
	}
}
