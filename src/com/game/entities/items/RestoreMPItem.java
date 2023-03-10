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

public class RestoreMPItem extends GenericEntity {
	private int magicPointsRestored;

	/***
	 * A constructor for the RestoreMPItem class. Creates a sprite that can be
	 * collected by a player to recover magic points up to 100.
	 */

	public RestoreMPItem(Vector spawnPos, int screenId, Renderer renderer) {

		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/mpItem.png"));
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: defenceitem.png not found");
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
		setMagicPointsRestored(50);
		this.screenId = screenId;
		addEntity(screenId, this);
		RestoreMPItem item = this;

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				thisSprite.inhibitHitboxFor(50000000);
				Player otherPlayerEntity = null;

				for (GenericEntity e : getEntities().get(screenId)) { // Find entity that collided
					if (e.getEntitySprite() == otherSprite) {
						if (e instanceof Player) { // Player collided with magicPointsRestored
							otherPlayerEntity = (Player) e;
							if (otherPlayerEntity.getMagicPoints() < 100) {
								otherPlayerEntity.getSfx().PlayTrack(0);
								otherPlayerEntity
										.setMagicPoints(otherPlayerEntity.getMagicPoints() + getMagicPointsRestored());
								if (otherPlayerEntity.getMagicPoints() > 100) {
									otherPlayerEntity.setMagicPoints(100);
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

	public int getMagicPointsRestored() {
		return magicPointsRestored;
	}

	public void setMagicPointsRestored(int magicPointsRestored) {
		this.magicPointsRestored = magicPointsRestored;
	}
}
