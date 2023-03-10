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

public class SpeedItem extends GenericEntity {
	private double extraSpeed;

	/***
	 * A constructor for the SpeedItem class. Creates a sprite that can be collected
	 * by a player to increase speed.
	 */

	public SpeedItem(Vector spawnPos, int ScreenId, Renderer renderer) {

		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/speedItem.png"));
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: speedItem.png not found");
		}

		ArrayList<Image> imgs = new ArrayList<Image>();
		imgs.add(new Image(inputstream));
		Random rand = new Random();
		Vector size = new Vector(30.0, 30.0);

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		setEntitySprite(new Sprite(spawnPos, new Vector(0.0, 0.0), rand.nextInt(360), 0.0, size, imgs));
		setHitbox(hitbox);
		setEntityType(Type.ITEM);
		setExtraSpeed(0.5);
		this.screenId = screenId;
		addEntity(screenId, this);
		SpeedItem item = this;

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				thisSprite.inhibitHitboxFor(5000000);

				for (GenericEntity e : getEntities().get(screenId)) { // Find entity that collided
					if (e.getEntitySprite() == otherSprite) {

						if (e instanceof Player) { // Player collided with DefenceItem
							((Player) e).getSfx().PlayTrack(0);
							((Player) e).setSpeed(((Player) e).getSpeed() + getExtraSpeed());

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

	public double getExtraSpeed() {
		return extraSpeed;
	}

	public void setExtraSpeed(double extraSpeed) {
		this.extraSpeed = extraSpeed;
	}
}
