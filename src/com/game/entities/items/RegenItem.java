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

public class RegenItem extends GenericEntity {
	private int regenAmount;

	/***
	 * A constructor for the RegenItem class. Creates a sprite that can be collected
	 * by a player to recover health slowly up to 200.
	 */

	public RegenItem(Vector spawnPos, int screenId, Renderer renderer) {

		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/hpregen.png"));
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: hpregen.png not found");
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
		setRegenAmount(50);
		this.screenId = screenId;
		addEntity(screenId, this);

		RegenItem item = this;

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				thisSprite.inhibitHitboxFor(50000000);

				for (GenericEntity e : getEntities().get(screenId)) { // Find entity that collided
					if (e.getEntitySprite() == otherSprite) {
						if (e instanceof Player) { // Player collided with healthItem
							final Player otherPlayerEntity = (Player) e;
							otherPlayerEntity.getSfx().PlayTrack(0);

							new Thread() {
								@Override
								public void run() {
									try {
										while (getRegenAmount() > 0) {
											Thread.sleep(500);
											if (otherPlayerEntity.getHealth() < 200) {
												otherPlayerEntity.setHealth(otherPlayerEntity.getHealth() + 1);
											}
											setRegenAmount(getRegenAmount() - 1);
										}
										removeEntity(item);
										renderer.removeSprite(item.getEntitySprite());
										// RendererSupport.addRendererRemEvent(item);
									} catch (InterruptedException ex) {

									}
								}
							}.start();
							break;

						}
					}
				}
			}
		});
	}

	public int getRegenAmount() {
		return regenAmount;
	}

	public void setRegenAmount(int regenAmount) {
		this.regenAmount = regenAmount;
	}
}
