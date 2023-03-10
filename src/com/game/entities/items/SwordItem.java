package com.game.entities.items;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.game.collision.Hitbox;
import com.game.collision.Polygon;
import com.game.entities.GenericEntity;
import com.game.entities.Player;
import com.game.graphics.CollisionHandler;
import com.game.graphics.Renderer;
import com.game.graphics.Sprite;
import com.game.utilities.Vector;

import javafx.scene.image.Image;

public class SwordItem extends GenericEntity {
	private int extraDmg;

	/***
	 * A constructor for the SwordItem class. Creates a sprite that can be collected
	 * by a player to increase sword damage.
	 */
	public SwordItem(Vector spawnPos, int screenId, Renderer renderer) {
		// Fetch arrow image
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/sword.png"));
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: sword.png not found");
		}

		ArrayList<Image> imgs = new ArrayList<Image>();
		imgs.add(new Image(inputstream));

		Random rand = new Random();
		Vector size = new Vector(10.0, 50.0);

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		setEntitySprite(
				new Sprite(spawnPos, new Vector(0.0, 0.0), rand.nextInt(360), 0.0, new Vector(10.0, 50.0), imgs));
		setHitbox(hitbox);
		setEntityType(Type.ITEM);
		setExtraDmg(10);
		this.screenId = screenId;
		addEntity(screenId, this);
		SwordItem item = this;

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				thisSprite.inhibitHitboxFor(50000000);
				Player otherPlayerEntity = null;

				for (GenericEntity e : getEntities().get(screenId)) { // Find entity that collided
					if (e.getEntitySprite() == otherSprite) {
						if (e instanceof Player) { // Player collided with SwordItem
							otherPlayerEntity = (Player) e;
							otherPlayerEntity.getSfx().PlayTrack(0);
							otherPlayerEntity.setExtraSwordDmg(otherPlayerEntity.getExtraSwordDmg() + getExtraDmg());
							removeEntity(item);
							renderer.removeSprite(item.getEntitySprite());
							break;
						}
					}
				}
			}
		});
	}

	public int getExtraDmg() {
		return extraDmg;
	}

	public void setExtraDmg(int extraDmg) {
		this.extraDmg = extraDmg;
	}
}
