package com.game.areas;

import java.util.Random;

import com.game.ai.DecideSpawn;
import com.game.ai.RockSpitter;
import com.game.ai.Slime;
import com.game.entities.GenericMonster;
import com.game.entities.Rock;
import com.game.entities.SpikyRock;
import com.game.graphics.Renderer;
import com.game.graphics.TransitionDirection;
import com.game.singleplayer.Door;
import com.game.utilities.Vector;

public class Area11 extends Areas {
	static Random rand = new Random();
	static final int screenId = 0;

	public Area11() {

	}

	@Override
	public void obstacles(Vector canvas) {
		// Top Door
		Door topDoor = new Door(TransitionDirection.UP, screenId, canvas, 1); // TOP ROOM

		// Left Door
		Door leftDoor = new Door(TransitionDirection.LEFT, screenId, canvas, 4); // LEFT ROOM

		// Right Door
		Door rightDoor = new Door(TransitionDirection.RIGHT, screenId, canvas, 2); // RIGHT ROOM

		// Bottom Door
		Door bottomDoor = new Door(TransitionDirection.DOWN, screenId, canvas, 3); // BOTTOM ROOM

		// Middle, top left spiky rock
		Vector size = new Vector(100.0, 100.0);
		Vector spawnPos = new Vector((canvas.x() / 2) - (size.x() / 2) - 150.0,
				(canvas.y() / 2) - (size.y() / 2) - 150.0);
		SpikyRock spikyRock = new SpikyRock(spawnPos, size, screenId);

		// Middle, top right spiky rock
		size = new Vector(100.0, 100.0);
		spawnPos = new Vector((canvas.x() / 2) - (size.x() / 2) + 150.0, (canvas.y() / 2) - (size.y() / 2) - 150.0);
		spikyRock = new SpikyRock(spawnPos, size, screenId);

		// Middle, bottom right spiky rock
		size = new Vector(100.0, 100.0);
		spawnPos = new Vector((canvas.x() / 2) - (size.x() / 2) + 150.0, (canvas.y() / 2) - (size.y() / 2) + 150.0);
		spikyRock = new SpikyRock(spawnPos, size, screenId);

		// Middle, bottom left spiky rock
		size = new Vector(100.0, 100.0);
		spawnPos = new Vector((canvas.x() / 2) - (size.x() / 2) - 150.0, (canvas.y() / 2) - (size.y() / 2) + 150.0);
		spikyRock = new SpikyRock(spawnPos, size, screenId);

		// Top left rocks
		size = new Vector(100.0, 50.0);
		spawnPos = new Vector(100.0, 100.0);
		Rock rock = new Rock(spawnPos, size, screenId);

		size = new Vector(50.0, 49.9);
		spawnPos = new Vector(100.0, 150.1);
		rock = new Rock(spawnPos, size, screenId);

		// Top right rocks
		size = new Vector(100.0, 50.0);
		spawnPos = new Vector(canvas.x() - size.x() - 100.0, 100.0);
		rock = new Rock(spawnPos, size, screenId);

		size = new Vector(50.0, 49.9);
		spawnPos = new Vector(canvas.x() - size.x() - 100.0, 150.1);
		rock = new Rock(spawnPos, size, screenId);

		// Bottom right rocks
		size = new Vector(100.0, 50.0);
		spawnPos = new Vector(canvas.x() - size.x() - 100.0, canvas.y() - size.y() - 100.0);
		rock = new Rock(spawnPos, size, screenId);

		size = new Vector(50.0, 49.9);
		spawnPos = new Vector(canvas.x() - size.x() - 100.0, canvas.y() - size.y() - 150.1);
		rock = new Rock(spawnPos, size, screenId);

		// Bottom left rocks
		size = new Vector(100.0, 50.0);
		spawnPos = new Vector(100.0, canvas.y() - size.y() - 100.0);
		rock = new Rock(spawnPos, size, screenId);

		size = new Vector(50.0, 49.9);
		spawnPos = new Vector(100.0, canvas.y() - size.y() - 150.1);
		rock = new Rock(spawnPos, size, screenId);
	}

	@Override
	public void monstersOffline(Vector canvas, Renderer renderer) {
		Vector size = new Vector(50, 50);

		Vector[] spawnPointsMons = {
				new Vector((canvas.x() / 2) - (size.x() / 2) - 150.0, (canvas.y() / 2) - (size.y() / 2)), // 0 Left
																											// centre
				new Vector((canvas.x() / 2) - (size.x() / 2), (canvas.y() / 2) - (size.y() / 2) - 100.0), // 1 Top
																											// centre
				new Vector((canvas.x() / 2) - (size.x() / 2) + 150.0, (canvas.y() / 2) - (size.y() / 2)), // 2 Right
																											// centre
				new Vector((canvas.x() / 2) - (size.x() / 2), (canvas.y() / 2) - (size.y() / 2) + 100.0), // 3 Bottom
																											// centre

				new Vector(250, 250), // 4 Top left
				new Vector(canvas.x() - size.x() - 250, 250), // 5 Top right
				new Vector(canvas.x() - size.x() - 250, canvas.y() - size.y() - 250), // 6 Bottom right
				new Vector(250, canvas.y() - size.y() - 250) }; // 7 Bottom left

		for (int i = 0; i < 4; i++) {
			GenericMonster monster;

			GenericMonster monPro = DecideSpawn.chooseMonster(DecideSpawn.rockSpitterList(),
					DecideSpawn.rockSpitterDistro());
			if (monPro instanceof RockSpitter) {
				monster = new RockSpitter((RockSpitter) monPro, spawnPointsMons[i], screenId);
				RockSpitter mon = (RockSpitter) monster;
				mon.setNetworkedFalse();
				mon.setRenderer(renderer);
			}

			// monster.setSfx(new EntitySFX(monster, gameRenderer.getCanvas(), 1));
		}

		for (int i = 4; i < spawnPointsMons.length; i++) {
			GenericMonster monster;

			GenericMonster monPro = DecideSpawn.chooseMonster(DecideSpawn.slimeList(), DecideSpawn.slimeDistro());
			if (monPro instanceof Slime) {
				monster = new Slime((Slime) monPro, spawnPointsMons[i], screenId);
			}

			// monster.setSfx(new EntitySFX(monster, gameRenderer.getCanvas(), 1));
		}
	}

	@Override
	public void itemsOffline(Vector canvas, Renderer renderer) {
		Vector size = new Vector(50, 50);
		Vector[] spawnPointsItems = { new Vector((canvas.x() / 2) - (size.x() / 2), (canvas.y() / 2) - (size.y() / 2)), // Centre
																														// of
																														// screen
				new Vector(40.0, 40.0), // Top left
				new Vector(canvas.x() - size.x() - 40.0, 40.0), // Top right
				new Vector(canvas.x() - size.x() - 40.0, canvas.y() - size.y() - 40.0), // Bottom right
				new Vector(40.0, canvas.y() - size.y() - 40.0) }; // Bottom left

		ItemSpawner.spawnItem(spawnPointsItems[0], screenId, renderer);
		ItemSpawner.spawnItem(spawnPointsItems[1], screenId, renderer);
		ItemSpawner.spawnItem(spawnPointsItems[2], screenId, renderer);
		ItemSpawner.spawnItem(spawnPointsItems[3], screenId, renderer);
		ItemSpawner.spawnItem(spawnPointsItems[4], screenId, renderer);
	}

	@Override
	public void items(Vector canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void monsters(Vector canvas) {
		// TODO Auto-generated method stub

	}
}
