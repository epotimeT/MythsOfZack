package com.game.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import com.game.collision.Hitbox;
import com.game.collision.Polygon;
import com.game.graphics.CollisionHandler;
import com.game.graphics.EdgeCollisionHandler;
import com.game.graphics.SideOfScreen;
import com.game.graphics.Sprite;
import com.game.utilities.Vector;

import javafx.scene.image.Image;

public class RockProjectile extends GenericProjectile {

	/***
	 * The Default constructor. <br>
	 * Creates a rockProjectile prototype that can be copied when shoot() is used.
	 */
	public RockProjectile() {
		setSpeed(1f);
		setDamage(40f);
	}

	/***
	 * Constructor.<br>
	 * Creates a rockprojectile with the desired speed and damage.
	 * 
	 * @param speed
	 * @param damage
	 */
	public RockProjectile(float speed, float damage) {
		setSpeed(speed);
		setDamage(damage);
	}

	RockProjectile(Vector arrowPos, Vector velXY, Double angleDeg) {
		// FileInputStream inputstream = null;

		ArrayList<Image> imgs = loadImage("rockprojectile");

		Vector size = new Vector(10d, 10d);

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		setEntitySprite(new Sprite(arrowPos, velXY, angleDeg, 0.0, size, imgs));
		setHitbox(hitbox);
		setFireRate(4f);
		setMaxSpeed(9.0f);
		setRotVel(0.0f);
		setSize(new Vector(10d, 10d));
		setAngleDeg(angleDeg);
		setKnockback(0.0f);
		setEntityType(Type.PROJECTILE);
		GenericProjectile thisRP = this;

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				thisSprite.inhibitHitboxFor(50000000);

				for (GenericEntity e : getEntities().get(screenId)) { // Find entity that collided - if slime or enemy
																		// player then remove arrow from screen
					if (e.getEntitySprite() == otherSprite) { // Hit slime or player
						if (thisRP.getOwner() == e)
							return;
						return;
					}
				}
			}
		});

		// Collision between arrow and wall
		this.getEntitySprite().registerCallbackOnEdgeCollision(new EdgeCollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, SideOfScreen edge) {
				removeFromOwner();
			}
		});

	}

	@Override
	public GenericProjectile shoot(Vector pos, Vector vel, Double angleDeg, Double rotVel, ComplexEntity e) {
		RockProjectile rock = new RockProjectile(pos, vel, angleDeg);
		rock.setSpeed(getSpeed());
		rock.setDamage(getDamage());
		rock.setOwner(e);
		return rock;
	}

	public ArrayList<Image> loadImage(String name) {

		ArrayList<Image> sprites = new ArrayList<Image>();

		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/" + name + ".png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sprites.add(new Image(inputstream));

		return sprites;
	}

}
