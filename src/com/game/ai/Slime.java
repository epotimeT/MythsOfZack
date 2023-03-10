package com.game.ai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.game.collision.Hitbox;
import com.game.collision.Polygon;
import com.game.commands.ComplexEntityCommands;
import com.game.commands.GenericCommands;
import com.game.commands.MonsterEntityCommands;
import com.game.entities.ComplexEntityCollision;
import com.game.entities.GenericMonster;
import com.game.graphics.CollisionHandler;
import com.game.graphics.EdgeCollisionHandler;
import com.game.graphics.SideOfScreen;
import com.game.graphics.Sprite;
import com.game.utilities.Vector;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class Slime extends GenericMonster implements ComplexEntityCommands, MonsterEntityCommands {
	public ArrayList<Image> spritelist = createList();
	public Sprite currentsprite;
	Timeline timeline;

	/***
	 * The default constructor for the Slime class.
	 * 
	 * Creates a slime entity that spawns at the vector co-ordinates given.
	 * 
	 */
	public Slime(Vector spawnPos, int screenId) {
		this.toIdle();
		Vector size = new Vector(40.0, 40.0);
		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		currentsprite = new Sprite(spawnPos, new Vector(), size, spritelist);
		setEntitySprite(currentsprite);
		setDetectionRadius(200);
		setHitbox(hitbox);
		List<Float> stats = Arrays.asList(100f, 50f, 0f, 0f, 0f, 0.2f, 0.4f);
		setStats(stats);
		setEntityType(Type.MONSTER);
		this.screenId = screenId;
		addEntity(screenId, this);
		setSword(null);

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				ComplexEntityCollision.collision(thisSprite, otherSprite);
			}
		});
	}

	public ArrayList<Image> createList() {
		ArrayList<Image> sprites = new ArrayList<Image>();

		for (int i = 0; i <= 4; i++) {
			FileInputStream inputstream = null;
			try {
				inputstream = new FileInputStream(new File("res/graphics/slime/" + Integer.toString(i) + ".png"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sprites.add(new Image(inputstream));

		}
		return sprites;
	}

	/***
	 * A prototype constructor - creates a slime that has the desired stats, with no
	 * position or hitbox.<br>
	 * This prototype is not added to the entity list.
	 * 
	 * @param stats - The list of stats the slime has.
	 */
	public Slime(List<Float> stats) {
		this.toIdle();

		setDetectionRadius(200);
		setStats(stats);
		setEntityType(Type.MONSTER);
	}

	/***
	 * A copy constructor - creates a slime based on the parameters of the parent
	 * slime. This needs position, a sprite, and a hitbox.<br>
	 * This is added to the entity list.
	 * 
	 * @param protoype      The original slime to copy from.
	 * @param spawnLocation The (X,Y) location to spawn at.
	 */
	public Slime(Slime prototype, Vector spawnLocation, int screenId) {
		switch (prototype.getCurrState()) {
		case ALERTED:
			this.toAlerted();
			break;
		case DEAD:
			this.toDead();
			break;
		case FIGHTING:
			this.toFighting();
			break;
		case IDLE:
			this.toIdle();
			break;
		default:
			this.toIdle();
			break;
		}

		Vector size = new Vector(40.0, 40.0);
		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2)))));

		currentsprite = new Sprite(spawnLocation, new Vector(), size, spritelist);
		setEntitySprite(currentsprite);
		setHitbox(hitbox);
		setDetectionRadius(prototype.getDetectionRadius());

		// STATS!
		List<Float> stats = Arrays.asList(prototype.getHealth(), prototype.getPhysDmg(), prototype.getMagDmg(),
				prototype.getDefence(), 0.0f, (float) prototype.getSpeed(), prototype.getMaxSpeed()

		);
		setStats(stats);
		setEntityType(Type.MONSTER);
		// getEntitySprite().setPosition(getPosition());
		setPosition(spawnLocation);
		this.screenId = screenId;
		addEntity(screenId, this);

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				ComplexEntityCollision.collision(thisSprite, otherSprite);
			}
		});

		this.getEntitySprite().registerCallbackOnEdgeCollision(new EdgeCollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, SideOfScreen edge) {
				ComplexEntityCollision.screenCollision(thisSprite, edge);
			}
		});
	}

	@Override
	public void attack() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dead() {
		toDead();
		// TODO turn off collisionBox

	}

	@Override
	public void updateState() {
		if (getInvulnerableTime() > 0) {
			setInvulnerableTime(getInvulnerableTime() - 1);
		}
		if (getAtkCooldown() > 0.0) {
			setAtkCooldown((float) (getAtkCooldown() - 1.0));
		}

		this.getEntitySprite().setCount(Float.toString(getHealth()));
		switch (this.getCurrState()) {
		case ALERTED:
			break;
		case DEAD:
			break;
		case FIGHTING:
			if (getPlayerLocations(entities.get(screenId)).isEmpty()) {
				toIdle();
			} else if (getDistance(ClosestPlayer(getPlayerLocations(entities.get(screenId)))) > getDetectionRadius()) {
				toIdle();
			}
			break;
		case IDLE:
			if (getPlayerLocations(entities.get(screenId)).isEmpty()) {
				toIdle();
			} else if (getDistance(ClosestPlayer(getPlayerLocations(entities.get(screenId)))) < getDetectionRadius()) {
				toFighting();
			}
			break;
		default:
			break;

		}

		if (0 >= getHealth()) {
			toDead();
			// TODO removeSprite
			/*
			 * this.getRenderer(); if
			 * (this.getRenderer().getSprites().contains(this.getEntitySprite())) {
			 * this.getRenderer().removeSprite(getEntitySprite()); }
			 */
			// getSfx().PlayTrack(3);
		}
	}

	@Override
	protected void toIdle() {
		super.toIdle();
	}

	@Override
	public void performAction() {
		switch (this.getCurrState()) {
		case ALERTED:
			break;
		case DEAD:
			break;
		case FIGHTING:
			Vector towards = ClosestPlayer(getPlayerLocations(entities.get(screenId)));
			if (towards.x() > this.getEntitySprite().position().x()) {
				currentsprite.setCurrentSprite(3);
				GenericCommands.move(this, Direction.RIGHT);
			} else {
				if (towards.x() < this.getEntitySprite().position().x()) {
					currentsprite.setCurrentSprite(2);
					GenericCommands.move(this, Direction.LEFT);
				}
			}

			if (towards.y() > this.getEntitySprite().position().y()) {
				currentsprite.setCurrentSprite(0);
				GenericCommands.move(this, Direction.UP);
			} else {
				if (towards.y() < this.getEntitySprite().position().y()) {
					currentsprite.setCurrentSprite(1);
					GenericCommands.move(this, Direction.DOWN);
				}
			}
			facing(towards);
			// Better animation

			break;
		case IDLE:
			if (getVelocity() != new Vector() && !(this.getHitbox().isInhibited())) {
				GenericCommands.stop(this);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void facing(Vector towards) {
		double angle = towards.angleBetweenTwoSprites(getPosition(), towards);
		if (angle < 45) {
			this.getEntitySprite().setCurrentSprite(1);
		} else if (angle < 135) {
			this.getEntitySprite().setCurrentSprite(3);
		} else if (angle < 225) {
			this.getEntitySprite().setCurrentSprite(0);
		} else if (angle < 315) {
			this.getEntitySprite().setCurrentSprite(2);
		} else {
			this.getEntitySprite().setCurrentSprite(1);
		}
	}

	@Override
	public void deathAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void projAttack() {
		// TODO Auto-generated method stub

	}

	public void animate(int imageIndex) {

		timeline = new Timeline(new KeyFrame(Duration.millis(3000), new EventHandler<ActionEvent>() {
			int index = imageIndex;

			@Override
			public void handle(ActionEvent event) {
				currentsprite.setCurrentSprite(index);
			}

		}));
		timeline.setCycleCount(5);

	}

}
