package com.game.singleplayer;

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
import com.game.graphics.TransitionDirection;
import com.game.utilities.Vector;

import javafx.scene.image.Image;

/**
 * <h1>Door</h1> This class contains the functions related to the doors, which
 * are found in the single player mode
 */
public class Door extends GenericEntity {

	/**
	 * Creates a Door object
	 * 
	 * @param dir      - the position of the door on the screen
	 * @param screenId - the screen the door is on
	 * @param canvas   - the size of the canvas
	 * @param newRoom  - the screen the door leads to
	 */
	public Door(TransitionDirection dir, int screenId, Vector canvas, int newRoom) {
		Vector spawnPos = new Vector();
		Vector size = new Vector();
		Vector newPos = new Vector();
		Vector pSize = new Vector(50, 50);
		switch (dir) {
		case UP:
			size = new Vector(100.0, 10.0);
			spawnPos = new Vector((canvas.x() / 2) - (size.x() / 2), 0.0);
			newPos = new Vector((canvas.x() / 2) - (pSize.x() / 2), (canvas.y() - pSize.y() - 20));
			break;
		case DOWN:
			size = new Vector(100.0, 10.0);
			spawnPos = new Vector((canvas.x() / 2) - (size.x() / 2), canvas.y() - (size.y()));
			newPos = new Vector((canvas.x() / 2) - (pSize.x() / 2), 20);
			break;
		case LEFT:
			size = new Vector(10.0, 100);
			spawnPos = new Vector(0.0, (canvas.y() / 2) - (size.y() / 2));
			newPos = new Vector(canvas.x() - pSize.x() - 20, (canvas.y() / 2) - (pSize.y() / 2));
			break;
		case RIGHT:
			size = new Vector(10.0, 100);
			spawnPos = new Vector(canvas.x() - size.x(), (canvas.y() / 2) - (size.y() / 2));
			newPos = new Vector(20, (canvas.y() / 2) - (pSize.y() / 2));
			break;
		}

		final Vector finNewPos = newPos;

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		setEntitySprite(new Sprite(spawnPos, new Vector(), 0.0, 0.0, size, loadImage("door")));
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

						if (e instanceof Player) {
							e.setPosition(finNewPos);
							e.setScreenId(newRoom);
							Player p = (Player) e;
							p.getSword().setScreenId(newRoom);
						} else {
							// Angle between door and another sprite
							double angleDeg = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
									otherSprite.getPositionCentre());

							// Calculates the door between the centre of the door and each one of its
							// corners

							double angOfCentreToTopRight = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
									new Vector(thisSprite.position().x() + thisSprite.getRenderSize().x(),
											thisSprite.position().y()));

							// Compares both angles to see which side the sprite collided with the door on
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

							double angOfCentreToBottomRight = v.angleBetweenTwoSprites(
									thisSprite.getPositionCentre(),
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
								e.setPosition(new Vector(
										thisSprite.position().x() - otherSprite.getRenderSize().x() - 0.1,
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
								((Player) e).getSword()
										.setPosition(new Vector(
												((Player) e).getSword().getPosition().x()
														+ (e.getPosition().x() - prevPos.x()),
												((Player) e).getSword().getPosition().y()
														+ (e.getPosition().y() - prevPos.y())));
							}
						}
					}

				}
			}
		});
	}

	/**
	 * Loads the image of the door
	 * 
	 * @param name - the name of the image
	 * @return - an ArrayList of the image(s) related to the door
	 */
	public ArrayList<Image> loadImage(String name) {

		ArrayList<Image> sprites = new ArrayList<Image>();

		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/" + name + ".png"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		sprites.add(new Image(inputstream));

		return sprites;
	}

}
