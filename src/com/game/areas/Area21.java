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

public class Area21 extends Areas {
	static Random rand = new Random();
	static final int screenId = 3;

	public Area21() {

	}

	@Override
	public void obstacles(Vector canvas) {

		// Top Door
		Door topDoor = new Door(TransitionDirection.UP, screenId, canvas, 0);

		Vector size = new Vector();
		Vector spawnPos = new Vector();

		// Top rock
		size = new Vector(800.0, 50.0);
		spawnPos = new Vector(200.0, 150.0);
		Rock rock = new Rock(spawnPos, size, screenId);

		// Middle left spiky rock
		size = new Vector(200.0, 75.0);
		spawnPos = new Vector(200.0, (canvas.y() / 2) - (size.y() / 2));
		SpikyRock spikyRock = new SpikyRock(spawnPos, size, screenId);

		// Middle right spiky rock
		size = new Vector(200.0, 75.0);
		spawnPos = new Vector(canvas.x() - size.x() - 200.0, (canvas.y() / 2) - (size.y() / 2));
		spikyRock = new SpikyRock(spawnPos, size, screenId);

		// Bottom rock
		size = new Vector(800.0, 50.0);
		spawnPos = new Vector(200.0, canvas.y() - (size.y() / 2) - 150.0);
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

		/*
		 * >>>>>>> 199385aacccb69f1ddaecc550944ffeafa738cac GenericMonster slime = new
		 * Slime(new Vector(), screenId); Vector size =
		 * slime.getEntitySprite().getRenderSize(); Vector[] spawnPointsMons = {new
		 * Vector((canvas.x()/2) - (size.x()/2) - 200.0, canvas.y() - 75.0), // Bottom
		 * of screen, slightly left new Vector((canvas.x()/2) - (size.x()/2) + 200.0,
		 * canvas.y() - 75.0), // Bottom of screen, slightly right new
		 * Vector((canvas.x()/2) - (size.x()/2) - 100.0, (canvas.y()/2) - (size.y()/2)),
		 * // Centre of screen, slightly right new Vector((canvas.x()/2) - (size.x()/2)
		 * + 100.0, (canvas.y()/2) - (size.y()/2))}; // Centre of screen, slightly left
		 * 
		 * for (int i = 0; i < spawnPointsMons.length; i++) { GenericMonster monster;
		 * 
		 * GenericMonster monPro =
		 * DecideSpawn.chooseMonster(DecideSpawn.mediumList(),DecideSpawn.mediumDistro()
		 * ); if (monPro instanceof Slime) { monster = new Slime((Slime) monPro,
		 * spawnPointsMons[i], screenId); } else if (monPro instanceof RockSpitter){
		 * monster = new RockSpitter((RockSpitter) monPro, spawnPointsMons[i],
		 * screenId); } else { monster = new Slime(spawnPointsMons[rand.nextInt(4)],
		 * screenId); }
		 * 
		 * //monster.setSfx(new EntitySFX(monster, gameRenderer.getCanvas(), 1));
		 * <<<<<<< HEAD } }
		 * 
		 * public void items(Vector canvas) { ======= }
		 */
	}

	public void items(Vector canvas, Renderer renderer) {
		Vector size = new Vector(50, 50);
		Vector[] spawnPointsItems = { new Vector((canvas.x() / 2) - (size.x() / 2), canvas.y() - 75.0), // Bottom of
																										// screen,
																										// middle
				new Vector((canvas.x() / 2) - (size.x() / 2), (canvas.y() / 2) - (size.y() / 2) - 100.0), // Middle of
																											// screen,
																											// top
				new Vector((canvas.x() / 2) - (size.x() / 2), (canvas.y() / 2) - (size.y() / 2) + 100.0), // Middle of
																											// screen,
																											// bottom
				new Vector(75.0, 75.0), // Top left corner
				new Vector(75.0, canvas.y() - size.y() - 75.0), // Bottom left corner
				new Vector(canvas.x() - size.x() - 75.0, 75.0), // Top right corner
				new Vector(canvas.x() - size.x() - 75.0, canvas.y() - size.y() - 75.0) }; // Bottom right corner

		ItemSpawner.spawnItem(spawnPointsItems[0], screenId, renderer);
		ItemSpawner.spawnItem(spawnPointsItems[1 + rand.nextInt(2)], screenId, renderer);
		ItemSpawner.spawnItem(spawnPointsItems[3 + rand.nextInt(2)], screenId, renderer);
		ItemSpawner.spawnItem(spawnPointsItems[5 + rand.nextInt(2)], screenId, renderer);

	}

	@Override
	public void items(Vector canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemsOffline(Vector canvas, Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void monsters(Vector canvas) {
		// TODO Auto-generated method stub

	}
}
