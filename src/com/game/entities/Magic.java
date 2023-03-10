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
import com.game.sound.EntitySFX;
import com.game.utilities.Vector;

import javafx.scene.image.Image;

public class Magic extends GenericProjectile {
	private boolean detonated = false;
	private EntitySFX sfx;

	public Magic() {
		this.setSpeed(3.0f);
		setRotVel(2.0f);
	}

	/***
	 * A constructor for the Magic class. Creates a magic object that spawns inside
	 * the player.
	 * 
	 * @param magicPos   - Position to spawn the magic
	 * @param magicVelXY - Magic velocity
	 * @param angleDeg   - Angle to move magic
	 * @param rotVel     - Magic rotational velocity
	 */
	public Magic(Vector magicPos, Vector magicVelXY, Double angleDeg, Double rotVel) {
		Vector size = new Vector(10.0, 10.0);

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		setEntitySprite(new Sprite(magicPos, magicVelXY, angleDeg, rotVel, size, loadImage("magic")));
		setHitbox(hitbox);
		setFireRate(2.5f);
		setDamage(100f);
		setSpeed(3.0f);
		setRotVel(2.0f);
		setSize(size);
		setAngleDeg(angleDeg);
		setKnockback(3.0f);
		setEntityType(Type.PROJECTILE);

		setSfx(new EntitySFX(this, 1200f, 2));
		// setSfx(new EntitySFX((GenericEntity) this, gameRenderer.getCanvas(), 2));

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				thisSprite.inhibitHitboxFor(500000);
				if (isDetonated())
					return;
				for (GenericEntity e : getEntities().get(screenId)) {
					if (e.getEntitySprite() == otherSprite) { // Check if magic collided with owner
						if (getOwner() == e)
							return;
					}
				}
				detonateMagic(thisSprite);
			}
		});

		this.getEntitySprite().registerCallbackOnEdgeCollision(new EdgeCollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, SideOfScreen edge) {
				if (isDetonated())
					return;
				detonateMagic(thisSprite);
			}
		});
	}

	public Magic getThisMagic() {
		return this;
	}

	/**
	 * Sets the magic velocity to 0 and increases the size of the magic projectile
	 * and sets a 0.4 second time. After 0.4 seconds, it will disappear.
	 * 
	 * @param thisSprite - magic sprite
	 */
	public void detonateMagic(Sprite thisSprite) {
		setDetonated(true);
		Vector size = new Vector(100.0, 100.0);

		getSfx().PlayTrack(0);

		thisSprite.setPosition(
				new Vector(thisSprite.position().x() - (size.x() / 2), thisSprite.position().y() - (size.y() / 2)));
		thisSprite.setVelocity(new Vector(0.0, 0.0));
		thisSprite.setPreferSize(size);

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));
		setHitbox(hitbox);

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep((400));
					removeFromOwner();
				} catch (InterruptedException ex) {
				}
			}
		}.start();
	}

	@Override
	public GenericProjectile shoot(Vector pos, Vector vel, Double angleDeg, Double rotVel, ComplexEntity e) {
		Magic magic = new Magic(pos, vel, angleDeg, rotVel);
		magic.setOwner(e);
		return magic;
	}

	public EntitySFX getSfx() {
		return sfx;
	}

	public void setSfx(EntitySFX sfx) {
		this.sfx = sfx;
	}

	public boolean isDetonated() {
		return detonated;
	}

	public void setDetonated(boolean detonated) {
		this.detonated = detonated;
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
