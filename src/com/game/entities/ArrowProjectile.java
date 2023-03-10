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

public class ArrowProjectile extends GenericProjectile {

	public ArrowProjectile() {
		this.setSpeed(9f);
	}

	/***
	 * A constructor for the ArrowProjectile class. Creates an arrow projectile that
	 * spawns inside the player.
	 * 
	 * @param arrowPos   - Position of arrow
	 * @param arrowVelXY - Arrow velocity
	 * @param angleDeg   - Arrow angle
	 */
	public ArrowProjectile(Vector arrowPos, Vector arrowVelXY, Double angleDeg) {
		// Fetch arrow image
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/arrow.png"));
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException: arrow.png not found");
		}

		ArrayList<Image> imgs = new ArrayList<Image>();
		imgs.add(new Image(inputstream));

		Vector size = new Vector(10.0, 30.0);

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		// Sprite(position, velocity, rotation, rotVel, size, Images)
		setEntitySprite(new Sprite(arrowPos, arrowVelXY, angleDeg, 0.0, size, imgs));
		setHitbox(hitbox);
		setFireRate(1f);
		setDamage(40f);
		setMaxSpeed(9.0f);
		setRotVel(0.0f);
		setSize(size);
		setAngleDeg(angleDeg);
		setKnockback(3.0f);
		setEntityType(Type.PROJECTILE);

		// Create collision events between arrow and entities
		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				thisSprite.inhibitHitboxFor(5000000);
				for (GenericEntity e : getEntities().get(screenId)) {
					if (e.getEntitySprite() == otherSprite) {
						if (e.getEntityType() == Type.PLAYER || e.getEntityType() == Type.MONSTER)
							return; // Check if arrow collided with complex entity
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

	public ArrowProjectile getThisArrow() {
		return this;
	}

	@Override
	public GenericProjectile shoot(Vector pos, Vector vel, Double angleDeg, Double rotVel, ComplexEntity e) {
		ArrowProjectile arrow = new ArrowProjectile(pos, vel, angleDeg);
		arrow.setOwner(e);
		return arrow;
	}
}
