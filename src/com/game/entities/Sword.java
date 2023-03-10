package com.game.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import com.game.collision.Hitbox;
import com.game.collision.Polygon;
import com.game.graphics.CollisionHandler;
import com.game.graphics.Sprite;
import com.game.utilities.Vector;

import javafx.scene.image.Image;

public class Sword extends GenericWeapon {

	/***
	 * A constructor for the Sword class.
	 */
	public Sword() {
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/sword.png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Image> imgs = new ArrayList<Image>();
		imgs.add(new Image(inputstream));

		// Sprite(position, velocity, rotation, rotVel, size, Images)
		setEntitySprite(new Sprite(new Vector(0.0, 0.0), new Vector(0.0, 0.0), 0.0, 0.0, new Vector(10.0, 70.0), imgs));

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(Arrays.asList(new Vector(5.0, 25.0),
				new Vector(-5.0, 25.0), new Vector(-10.0, -45.0), new Vector(10.0, -45.0)))));

		setHitbox(hitbox);
		setAttackRate(0.5f);
		setDamage(40f);
		setKnockback(6.0f);
		setPosition(new Vector(10000, 10000));
		setEntityType(Type.WEAPON);
		addEntity(0, this);

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				thisSprite.inhibitHitboxFor(50000000);
			}
		});
	}

	public Sword getThisSword() {
		return this;
	}
}
