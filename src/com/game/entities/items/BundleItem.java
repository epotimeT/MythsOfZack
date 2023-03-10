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
import com.game.graphics.Sprite;
import com.game.utilities.Vector;

import javafx.scene.image.Image;

public class BundleItem extends GenericEntity {
	private float[] buffList;

	/***
	 * A constructor for the BundleItem class. Creates a sprite that can be
	 * collected by a player to increase stats. Dropped by dead players who have
	 * picked up items.
	 */
	public BundleItem(Vector spawnPos, float[] buffs, int screenId) {
		// Fetch h bundleItemage
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/bundleItem.png"));
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: bundleItem.png not found");
		}

		ArrayList<Image> imgs = new ArrayList<Image>();
		imgs.add(new Image(inputstream));

		Vector size = new Vector(40.0, 40.0);

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		setEntitySprite(new Sprite(spawnPos, new Vector(0.0, 0.0), 0.0, 0.0, size, imgs));
		setHitbox(hitbox);
		setEntityType(Type.ITEM);
		setBuffs(buffs);
		this.screenId = screenId;
		addEntity(screenId, this);

		BundleItem item = this;

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				thisSprite.inhibitHitboxFor(5000000);

				for (GenericEntity e : getEntities().get(screenId)) { // Find entity that collided
					if (e.getEntitySprite() == otherSprite) {

						if (e instanceof Player) { // Player collided with BundleItem
							Player otherPlayerEntity = (Player) e;
							otherPlayerEntity.getSfx().PlayTrack(0);
							otherPlayerEntity
									.setExtraArrowDmg(otherPlayerEntity.getExtraArrowDmg() + (int) getBuffs()[0]);
							otherPlayerEntity.addArrows((int) (otherPlayerEntity.getArrows() + getBuffs()[1]));
							otherPlayerEntity.setPhysDef(otherPlayerEntity.getDefence() + getBuffs()[2]);
							otherPlayerEntity.setSpeed(otherPlayerEntity.getSpeed() + getBuffs()[3]);
							otherPlayerEntity
									.setExtraSwordDmg(otherPlayerEntity.getExtraSwordDmg() + (int) getBuffs()[4]);

							break;
						}
					}
				}
			}
		});
	}

	public float[] getBuffs() {
		return buffList;
	}

	public void setBuffs(float[] buffs) {
		this.buffList = buffs;
	}
}
