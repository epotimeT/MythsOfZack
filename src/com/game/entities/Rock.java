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

public class Rock extends GenericEntity {
	/***
	 * A constructor for the Rock class. Creates a sprite that entities cannot pass
	 * through.
	 * 
	 * @param spawnPos - Location to spawn rock
	 * @param size     - size of rock
	 * @param screenId - screen to assign to rock
	 */
	public Rock(Vector spawnPos, Vector size, int screenId) {
		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		setEntitySprite(new Sprite(spawnPos, new Vector(), 0.0, 0.0, size, loadImage("rock")));
		setHitbox(hitbox);
		setEntityType(Type.OTHER);
		this.screenId = screenId;
		addEntity(screenId, this);

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				otherSprite.inhibitHitboxFor(5000);

				for (GenericEntity e : getEntities().get(screenId)) {
					if (e.getEntitySprite() == otherSprite) {
						Vector v = new Vector();
						Vector prevPos = otherSprite.position();

						// Angle between rock and another sprite
						double angleDeg = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
								otherSprite.getPositionCentre());

						// Calculates the angle between the centre of the rock and each one of its
						// corners

						double angOfCentreToTopRight = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
								new Vector(thisSprite.position().x() + thisSprite.getRenderSize().x(),
										thisSprite.position().y()));

						// Compares both angles to see which side the sprite collided with the rock on
						if (angleDeg <= angOfCentreToTopRight) {
							e.setPosition(new Vector(otherSprite.position().x(),
									thisSprite.position().y() - otherSprite.getRenderSize().y() - 0.1));
							if (e instanceof Player) {
								((Player) e).getSword()
										.setPosition(new Vector(
												((Player) e).getSword().getPosition().x()
														+ (e.getPosition().x() - prevPos.x()),
												((Player) e).getSword().getPosition().y()
														+ (e.getPosition().y() - prevPos.y())));
							}
							return;
						}

						double angOfCentreToBottomRight = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
								new Vector(thisSprite.position().x() + thisSprite.getRenderSize().x(),
										thisSprite.position().y() + thisSprite.getRenderSize().y()));

						if (angleDeg <= angOfCentreToBottomRight) {
							e.setPosition(
									new Vector(thisSprite.position().x() + thisSprite.getRenderSize().x() + 0.1,
											otherSprite.position().y()));
							if (e instanceof Player) {
								((Player) e).getSword()
										.setPosition(new Vector(
												((Player) e).getSword().getPosition().x()
														+ (e.getPosition().x() - prevPos.x()),
												((Player) e).getSword().getPosition().y()
														+ (e.getPosition().y() - prevPos.y())));
							}
							return;
						}

						double angOfCentreToBottomLeft = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
								new Vector(thisSprite.position().x(),
										thisSprite.position().y() + thisSprite.getRenderSize().y()));

						if (angleDeg <= angOfCentreToBottomLeft) {
							e.setPosition(new Vector(otherSprite.position().x(),
									thisSprite.position().y() + thisSprite.getRenderSize().y() + 0.1));
							if (e instanceof Player) {
								((Player) e).getSword()
										.setPosition(new Vector(
												((Player) e).getSword().getPosition().x()
														+ (e.getPosition().x() - prevPos.x()),
												((Player) e).getSword().getPosition().y()
														+ (e.getPosition().y() - prevPos.y())));
							}
							return;
						}

						double angOfCentreToTopLeft = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
								thisSprite.position());

						if (angleDeg <= angOfCentreToTopLeft) {
							e.setPosition(
									new Vector(thisSprite.position().x() - otherSprite.getRenderSize().x() - 0.1,
											otherSprite.position().y()));
							if (e instanceof Player) {
								((Player) e).getSword()
										.setPosition(new Vector(
												((Player) e).getSword().getPosition().x()
														+ (e.getPosition().x() - prevPos.x()),
												((Player) e).getSword().getPosition().y()
														+ (e.getPosition().y() - prevPos.y())));
							}
							return;
						}

						e.setPosition(new Vector(otherSprite.position().x(),
								thisSprite.position().y() - otherSprite.getRenderSize().y() - 0.1));
						if (e instanceof Player) {
							((Player) e).getSword().setPosition(new Vector(
									((Player) e).getSword().getPosition().x() + (e.getPosition().x() - prevPos.x()),
									((Player) e).getSword().getPosition().y() + (e.getPosition().y() - prevPos.y())));
						}
					}
				}
			}
		});
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
