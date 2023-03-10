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

public class SpikyRock extends GenericEntity {
	private float spikeDamage;

	/***
	 * A constructor for the SpikyRock class. Creates a sprite that the player can
	 * collide with to take damage and knockback.
	 * 
	 * @param spawnPos - Location to spawn rock
	 * @param size     - size of rock
	 * @param screenId - screen to assign to rock
	 */
	public SpikyRock(Vector spawnPos, Vector size, int screenId) {
		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		setEntitySprite(new Sprite(spawnPos, new Vector(), 0.0, 0.0, size, loadImage("spikyrock")));
		setHitbox(hitbox);
		setSpikeDamage(10f);
		setEntityType(Type.OTHER);
		this.screenId = screenId;
		addEntity(screenId, this);

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				otherSprite.inhibitHitboxFor(50000);

				for (GenericEntity e : getEntities().get(screenId)) { // Find out if player collided with spiky rock
					if (e.getEntitySprite() == otherSprite) {
						if (e instanceof Player) { // Player collided with spike rock
							Player otherPlayerEntity = (Player) e;

							// otherPlayerEntity.getSfx().PlayTrack(2);

							otherPlayerEntity.setHealth(otherPlayerEntity.getHealth() - getSpikeDamage()); // Player
																											// takes
																											// damage
																											// from
																											// spike

							Vector v = new Vector();

							double playerKnockback = 3.0;

							double angleDeg = v.angleBetweenTwoSprites(otherSprite.getPositionCentre(),
									thisSprite.getPositionCentre());

							final Vector vel = new Vector(-1 * (playerKnockback * Math.sin(Math.toRadians(angleDeg))),
									playerKnockback * Math.cos(Math.toRadians(angleDeg)));

							((Player) e).updatePlayerSwordVel(vel);

							otherPlayerEntity.setInvulnerableTime(25);

							new Thread() {
								@Override
								public void run() {
									try {
										Thread.sleep((100));
										((Player) e).updatePlayerSwordVel(vel.scale(0.5).reverseSign());
										Thread.sleep((100));
										((Player) e).updatePlayerSwordVel(vel.scale(0.5).reverseSign());
									} catch (InterruptedException ex) {
									}
								}
							}.start();
							break;
						} else if (e.entityType == Type.MONSTER) {
							Vector v = new Vector();

							double angleDeg = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
									otherSprite.getPositionCentre());

							double angOfCentreToTopRight = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
									new Vector(thisSprite.position().x() + thisSprite.getRenderSize().x(),
											thisSprite.position().y()));

							if (angleDeg <= angOfCentreToTopRight) {
								e.setPosition(new Vector(otherSprite.position().x(),
										thisSprite.position().y() - otherSprite.getRenderSize().y() - 0.1));
								return;
							}

							double angOfCentreToBottomRight = v.angleBetweenTwoSprites(
									thisSprite.getPositionCentre(),
									new Vector(thisSprite.position().x() + thisSprite.getRenderSize().x(),
											thisSprite.position().y() + thisSprite.getRenderSize().y()));

							if (angleDeg <= angOfCentreToBottomRight) {
								e.setPosition(
										new Vector(thisSprite.position().x() + thisSprite.getRenderSize().x() + 0.1,
												otherSprite.position().y()));
								return;
							}

							double angOfCentreToBottomLeft = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
									new Vector(thisSprite.position().x(),
											thisSprite.position().y() + thisSprite.getRenderSize().y()));

							if (angleDeg <= angOfCentreToBottomLeft) {
								e.setPosition(new Vector(otherSprite.position().x(),
										thisSprite.position().y() + thisSprite.getRenderSize().y() + 0.1));
								return;
							}

							double angOfCentreToTopLeft = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
									thisSprite.position());

							if (angleDeg <= angOfCentreToTopLeft) {
								e.setPosition(new Vector(
										thisSprite.position().x() - otherSprite.getRenderSize().x() - 0.1,
										otherSprite.position().y()));
								return;
							}

							e.setPosition(new Vector(otherSprite.position().x(),
									thisSprite.position().y() - otherSprite.getRenderSize().y() - 0.1));
						}
					}
				}
			}
		});
	}

	public float getSpikeDamage() {
		return spikeDamage;
	}

	public void setSpikeDamage(float spikeDamage) {
		this.spikeDamage = spikeDamage;
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
