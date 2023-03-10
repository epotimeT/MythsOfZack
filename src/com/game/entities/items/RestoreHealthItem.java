package com.game.entities.items;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import com.game.collision.Hitbox;
import com.game.collision.Polygon;
import com.game.entities.GenericEntity;
import com.game.entities.Player;
import com.game.graphics.CollisionHandler;
import com.game.graphics.Renderer;
import com.game.graphics.Sprite;
import com.game.utilities.Vector;

import javafx.scene.image.Image;

public class RestoreHealthItem extends GenericEntity {
	private int extraHealth;

	/***
	 * A constructor for the RestoreHealthItem class. Creates a sprite that can be
	 * collected by a player to recover health instantly up to 200.
	 */

	public RestoreHealthItem(Vector spawnPos, int screenId, Renderer renderer) {
		// Fetch arrow image
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/healthItem.png"));
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: healthItem.png not found");
		}

		ArrayList<Image> imgs = new ArrayList<Image>();
		imgs.add(new Image(inputstream));

		Vector size = new Vector(40.0, 40.0);

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		setEntitySprite(new Sprite(spawnPos, new Vector(0.0, 0.0), 0.0, 0.0, new Vector(40.0, 40.0), imgs));
		setHitbox(hitbox);
		setEntityType(Type.ITEM);
		setExtraHealth(50);
		this.screenId = screenId;
		addEntity(screenId, this);

		RestoreHealthItem item = this;

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				thisSprite.inhibitHitboxFor(50000000);
				Player otherPlayerEntity = null;

				for (GenericEntity e : getEntities().get(screenId)) { // Find entity that collided
					if (e.getEntitySprite() == otherSprite) {
						if (e instanceof Player) { // Player collided with healthItem
							otherPlayerEntity = (Player) e;
							if (otherPlayerEntity.getHealth() < 200) {
								otherPlayerEntity.getSfx().PlayTrack(0);
								otherPlayerEntity.setHealth(otherPlayerEntity.getHealth() + getExtraHealth());
								if (otherPlayerEntity.getHealth() > 200) {
									otherPlayerEntity.setHealth(200);
								}
							}

							removeEntity(item);
							renderer.removeSprite(item.getEntitySprite());
							// RendererSupport.addRendererRemEvent(item);
							break;
						}
					}
				}
			}
		});
	}

	public int getExtraHealth() {
		return extraHealth;
	}

	public void setExtraHealth(int extraHealth) {
		this.extraHealth = extraHealth;
	}
}
