package com.game.ai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.game.collision.Hitbox;
import com.game.collision.Polygon;
import com.game.commands.GenericCommands;
import com.game.commands.RangedCommands;
import com.game.entities.ComplexEntityCollision;
import com.game.entities.GenericMonster;
import com.game.entities.GenericProjectile;
import com.game.entities.RockProjectile;
import com.game.graphics.CollisionHandler;
import com.game.graphics.EdgeCollisionHandler;
import com.game.graphics.Renderer;
import com.game.graphics.SideOfScreen;
import com.game.graphics.Sprite;
import com.game.utilities.Vector;

import javafx.scene.image.Image;

public class RockSpitter extends GenericMonster {

	Vector currentTarget = null;
	boolean justAttacked = false;
	public ArrayList<Image> spritelist = createList();
	public Sprite currentsprite;
	boolean networked = true;
	Renderer renderer;

	/***
	 * The default constructor for the RockSpitter class.
	 * 
	 * Creates a rockspitter entity that spawns at the vector co-ordinates given.
	 * 
	 */

	public RockSpitter(Vector spawnPos, int screenId) {
		this.toIdle();

		Hitbox hitbox = new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(10, -10), new Vector(10, 10), new Vector(-10, 10), new Vector(-10, -10)))));
		currentsprite = new Sprite(spawnPos, new Vector(0, 0), new Vector(100.0, 100.0), spritelist);
		setEntitySprite(currentsprite);
		setDetectionRadius(200);
		setHitbox(hitbox);
		List<Float> stats = Arrays.asList(50f, 10f, 0f, 0f, 0f, 0.5f, 1f);
		setStats(stats);
		setDetectionRadius(300);
		setProjectile(new RockProjectile());
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

	/***
	 * A prototype Constructor - creates a roskspitter with the desired stats, with
	 * no position or hitbox. <br>
	 * This prototype is not added to the entitylist.
	 * 
	 * @param stats      - The list of stats the rockspitter has.
	 * @param projectile - The projectile attached to the rockspitter.
	 */
	public RockSpitter(List<Float> stats, GenericProjectile projectile) {
		this.toIdle();
		// TODO Change Size
		Vector size = new Vector(40.0, 40.0);

		setDetectionRadius(400);
		setStats(stats);
		setProjectile(projectile);
		setEntityType(Type.MONSTER);

	}

	/***
	 * A Copy constructor - creates a rockspitter based on the parameters of the
	 * parent rockspitter.
	 * 
	 * @param prototype
	 * @param spawnLocation
	 */
	public RockSpitter(RockSpitter prototype, Vector spawnLocation, int screenId) {
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
		setProjectile(new RockProjectile());
		setEntityType(Type.MONSTER);
		setSword(null);
		setProjectile(prototype.getProjectile());
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

		this.getEntitySprite().setCurrentSprite(1);
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
		switch (getCurrState()) {
		case ALERTED:
			if (currentTarget == null) {
				// currentsprite.setCurrentSprite(1);
				toIdle();
			}
			if (currentTarget != null) {
				double xDistance = Math.abs(getPosition().x() - getCurrentTarget().x());
				double yDistance = Math.abs(getPosition().y() - getCurrentTarget().y());
				if ((xDistance <= 10 || yDistance <= 10) && getCooldownTime() == 0) {
					GenericCommands.stop(this);
					toFighting();
				}
			}
			break;
		case DEAD:
			break;
		case FIGHTING:
			if (getAtkCooldown() != 0) {
				currentTarget = null;
			}
			break;
		case IDLE:
			if (getPlayerLocations(entities.get(screenId)).isEmpty()) {
				break;
			}
			if (getDistance(getClosestPlayer()) <= getDetectionRadius()) {
				// currentsprite.setCurrentSprite(1);
				toAlerted();
			}
			break;
		default:
			break;
		}

		if (0 >= getHealth()) {
			toDead();
			getSfx().PlayTrack(3);

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
	public void performAction() {

		if (getPlayerLocations(entities.get(screenId)).size() == 0) {
			toIdle();
			return;
		}
		switch (getCurrState()) {
		case ALERTED:
			if (getPlayerLocations(entities.get(screenId)).size() != 0
					&& (currentTarget == null || getDistance(currentTarget, getClosestPlayer()) > 15)) {
				currentTarget = new Vector(getClosestPlayer().x(), getClosestPlayer().y());
			}

			double xDistance = Math.abs(getPosition().x() - getCurrentTarget().x());
			double yDistance = Math.abs(getPosition().y() - getCurrentTarget().y());

			if (getAtkCooldown() > 0) {
				break;
			}

			if (xDistance <= 10 || yDistance <= 10) {
				facing(currentTarget);
				break;
			}

			if (xDistance > yDistance) {
				// X is bigger than y, go horizontally
				if (getPosition().y() > getCurrentTarget().y()) {
					// entity is above, do go down
					currentsprite.setCurrentSprite(2);
					GenericCommands.move(this, Direction.DOWN);
				} else {
					currentsprite.setCurrentSprite(1);
					GenericCommands.move(this, Direction.UP);
				}
			} else {
				// Y is bigger/equal to x, go vertically
				if (getPosition().x() > getCurrentTarget().x()) {
					// Entity is on the right, go left
					currentsprite.setCurrentSprite(3);
					GenericCommands.move(this, Direction.LEFT);
				} else {
					currentsprite.setCurrentSprite(0);
					GenericCommands.move(this, Direction.RIGHT);

				}
			}
			// facing(currentTarget);

			break;

		case DEAD:
			break;
		case FIGHTING:
			if (currentTarget != null) {
				if (getPlayerLocations(entities.get(screenId)).isEmpty()) {
					break;
				}
				if (getDistance(currentTarget, getClosestPlayer()) > 30) {
					break;
				}
			}

			if (getAtkCooldown() == 0 && justAttacked == false) {
				projAttack();
				currentTarget = null;
				// justAttacked = true;
			}
			toAlerted();
			break;
		case IDLE:
			if (getVelocity() != new Vector() && !(this.getHitbox().isInhibited())) {
				GenericCommands.stop(this);
			}
			// currentsprite.setCurrentSprite(1);
			break;

		}

	}

	@Override
	public void deathAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void projAttack() {
		if (getAtkCooldown() != 0) {
			return;
		}
		RockSpitter rp = this;
		Vector targetLocal = new Vector(getCurrentTarget().x(), getCurrentTarget().y());
		RangedCommands.projAttack(rp, targetLocal);

		if (!networked) {
			setAtkCooldown(0);
			getSfx().PlayTrack(2);
			GenericProjectile proj = com.game.singleplayer.RangedCommands.projAttack(rp, targetLocal, renderer);
			// proj.removeEntity(proj);
			RockSpitter me = this;

			Thread waiter = new Thread() {
				@Override
				public void run() {

					List<GenericProjectile> projList = me.getProjectileList();
					long start = System.currentTimeMillis();
					long end = start + 5 * 1000;

					while (projList.contains(proj) && System.currentTimeMillis() < end) {
						projList = me.getProjectileList();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.println("removed");
					proj.removeFromRenderer(renderer);
					proj.removeEntity(proj);
				}
			};

			waiter.setDaemon(true);
			waiter.start();

		}
	}

	public Vector getCurrentTarget() {
		return currentTarget;
	}

	public ArrayList<Image> createList() {
		ArrayList<Image> sprites = new ArrayList<Image>();

		for (int i = 0; i < 4; i++) {
			FileInputStream inputstream = null;
			try {
				inputstream = new FileInputStream(new File("res/graphics/rockspitter/" + Integer.toString(i) + ".png"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sprites.add(new Image(inputstream));

		}
		return sprites;
	}

	@Override
	public void facing(Vector towards) {
		double angle = towards.angleBetweenTwoSprites(getPosition(), towards);
		if (angle < 45) {
			this.getEntitySprite().setCurrentSprite(2);
		} else if (angle < 135) {
			this.getEntitySprite().setCurrentSprite(0);
		} else if (angle < 225) {
			this.getEntitySprite().setCurrentSprite(1);
		} else if (angle < 315) {
			this.getEntitySprite().setCurrentSprite(3);
		} else {
			this.getEntitySprite().setCurrentSprite(2);
		}
	}

	public void setNetworkedFalse() {
		networked = false;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}
}
