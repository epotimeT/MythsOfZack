package com.game.entities;

import java.util.List;
import java.util.stream.Collectors;

import com.game.entities.GenericEntity.Type;
import com.game.graphics.SideOfScreen;
import com.game.graphics.Sprite;
import com.game.logic.ServerPlayer;
import com.game.utilities.Vector;

public class ComplexEntityCollision {
	private static GenericEntity retriever = new Retriever();
	private static boolean isOffline = false; // TODO

	/***
	 * The collision method for complex entities, consisting of monsters and
	 * players. It will perform an action if a player or monster collides with a
	 * player, monster or weapon.
	 * 
	 * @param thisSprite  - sprite in collision
	 * @param otherSprite - sprite in collision
	 */
	public static void collision(Sprite thisSprite, Sprite otherSprite) {
		GenericEntity thisEntity = null;
		GenericEntity otherEntity = null;
		List<GenericEntity> entities = retriever.getEntities().stream().flatMap(List::stream)
				.collect(Collectors.toList());
		for (GenericEntity e : entities) { // Find entity of this sprite
			if (e.getEntitySprite() == thisSprite) {
				thisEntity = e;
				break;
			}
		}

		for (GenericEntity e : retriever.getEntities().get(thisEntity.getScreenId())) { // Find entity of other sprite
			if (e.getEntitySprite() == otherSprite) {
				otherEntity = e;
				break;
			}
		}

		if (thisEntity == null || otherEntity == null) {
			return;
		}

		for (GenericEntity e : entities) {
			if (e instanceof ComplexEntity) {
				for (GenericProjectile projectile : ((ComplexEntity) e).getProjectileList()) {
					if (projectile.getEntitySprite() == otherSprite && e.getEntitySprite() != thisSprite) {
						thisSprite.inhibitHitboxFor(50000);
						entityProjectileCollision((ComplexEntity) thisEntity, thisSprite, projectile,
								(ComplexEntity) e);
						return;
					}
				}
				if (((ComplexEntity) e).getSword() != null) {
					if (((ComplexEntity) e).getSword().getEntitySprite() == otherSprite
							&& e.getEntitySprite() != thisSprite) { // Sword collided with complex entity
						thisSprite.inhibitHitboxFor(50000);
						if (((ComplexEntity) thisEntity).getInvulnerableTime() > 0)
							return;

						if (thisEntity.getEntityType() == Type.MONSTER)
							MonHitEntitySwordCollision((ComplexEntity) thisEntity, thisSprite, (ComplexEntity) e);

						if (thisEntity instanceof ServerPlayer) {
							ServerPlayer player = (ServerPlayer) thisEntity;
							player.tcpConnection.sendData(9, Integer.toString(1) + ","
									+ Integer.toString(retriever.getEntities().get(e.getScreenId()).indexOf(e)));
						}
						// entitySwordCollision((ComplexEntity) thisEntity, thisSprite, (ComplexEntity)
						// e);
						return;
					}
				}
			}
		}

		if (thisEntity == null || otherEntity == null)
			return;

		if (thisEntity.getEntityType() == Type.MONSTER && otherEntity.getEntityType() == Type.MONSTER) {
			thisSprite.inhibitHitboxFor(50000);
			otherSprite.inhibitHitboxFor(50000);
			sameEntityTypeCollision(thisSprite, otherSprite, thisEntity, otherEntity);

		} else if (thisEntity.getEntityType() == Type.MONSTER && otherEntity.getEntityType() == Type.PLAYER) {
			thisSprite.inhibitHitboxFor(50000);
			otherSprite.inhibitHitboxFor(50000);
			if (((ComplexEntity) otherEntity).getInvulnerableTime() > 0)
				return;

			MonHitMonPlayerCollision((ComplexEntity) thisEntity, thisSprite, (Player) otherEntity, otherSprite);

			if (otherEntity instanceof ServerPlayer) {
				ServerPlayer player = (ServerPlayer) otherEntity;
				player.tcpConnection.sendData(9, Integer.toString(0) + ","
						+ Integer.toString(retriever.getEntities().get(thisEntity.getScreenId()).indexOf(thisEntity)));
			} else if (otherEntity instanceof Player && thisEntity instanceof ComplexEntity) {
				Player player = (Player) otherEntity;
				ComplexEntity monster = (ComplexEntity) thisEntity;
				PlayerHitMonPlayerCollision(monster, thisSprite, player, otherSprite);

			}
			// monPlayerCollision((ComplexEntity) thisEntity, thisSprite, (Player)
			// otherEntity, otherSprite);
		}
	}

	/***
	 * Performs damage and knockback on a player, caused by a monster.
	 * 
	 * @param thisEntity  - Attacking monster
	 * @param thisSprite  - monster sprite
	 * @param otherEntity - player taking damage
	 * @param otherSprite - player sprite
	 */
	public static void PlayerHitMonPlayerCollision(ComplexEntity thisEntity, Sprite thisSprite, Player otherEntity,
			Sprite otherSprite) {
		otherEntity.setInvulnerableTime(25);

		otherEntity
				.setHealth(otherEntity.getHealth() - (thisEntity.getPhysDmg() / (1 + (otherEntity.getDefence() / 10)))); // Player
																															// takes
																															// damage
																															// from
																															// monster

		Vector v = new Vector();
		double playerKnockback = 3.0;

		double angleDeg = v.angleBetweenTwoSprites(otherSprite.getPositionCentre(), thisSprite.getPositionCentre());

		final Vector playerVel = new Vector(-1 * (playerKnockback * Math.sin(Math.toRadians(angleDeg))),
				playerKnockback * Math.cos(Math.toRadians(angleDeg)));

		if (otherEntity.timeline != null) {
			if (otherEntity.timeline != null) {
				otherEntity.timeline.pause();
			}
			otherEntity.currentsprite.setCurrentSprite(12);

			if (otherEntity.timeline != null) {
				otherEntity.timeline.play();
			}
		}

		otherEntity.updatePlayerSwordVel(playerVel);

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
					otherEntity.updatePlayerSwordVel(playerVel.scale(0.5).reverseSign());
					Thread.sleep(100);
					otherEntity.updatePlayerSwordVel(playerVel.scale(0.5).reverseSign());
				} catch (InterruptedException ex) {
				}
			}
		}.start();
	}

	/***
	 * Performs knockback on a monster and damages player.
	 * 
	 * @param thisEntity  - Attacking monster
	 * @param thisSprite  - monster sprite
	 * @param otherEntity - player taking damage
	 * @param otherSprite - player sprite
	 */
	public static void MonHitMonPlayerCollision(ComplexEntity thisEntity, Sprite thisSprite, Player otherEntity,
			Sprite otherSprite) {
		otherEntity.setInvulnerableTime(25);
		otherEntity
				.setHealth(otherEntity.getHealth() - (thisEntity.getPhysDmg() / (1 + (otherEntity.getDefence() / 10)))); // Player
																															// takes
																															// damage
																															// from
																															// monster
		Vector v = new Vector();
		double monKnockback = 3.0;

		double angleDeg = v.angleBetweenTwoSprites(otherSprite.getPositionCentre(), thisSprite.getPositionCentre());

		Vector monVel = new Vector(monKnockback * Math.sin(Math.toRadians(angleDeg)),
				-1 * (monKnockback * Math.cos(Math.toRadians(angleDeg))));

		thisSprite.updateVelX(monVel.x());
		thisSprite.updateVelY(monVel.y());
	}

	/***
	 * Performs damage and knockback on a player, caused by a monster. Monster also
	 * takes knockback.
	 * 
	 * @param thisEntity  - Attacking monster
	 * @param thisSprite  - monster sprite
	 * @param otherEntity - player taking damage
	 * @param otherSprite - player sprite
	 */
	public static void monPlayerCollision(ComplexEntity thisEntity, Sprite thisSprite, Player otherEntity,
			Sprite otherSprite) {
		if (otherEntity.getInvulnerableTime() > 0)
			return;

		otherEntity.setInvulnerableTime(25);
		// ((ComplexEntity) otherEntity).getSfx().PlayTrack(2);
		otherEntity
				.setHealth(otherEntity.getHealth() - (thisEntity.getPhysDmg() / (1 + (otherEntity.getDefence() / 10)))); // Player
																															// takes
																															// damage
																															// from
																															// monster

		Vector v = new Vector();
		double monKnockback = 5.0;
		double playerKnockback = 3.0;

		double angleDeg = v.angleBetweenTwoSprites(otherSprite.getPositionCentre(), thisSprite.getPositionCentre());

		Vector monVel = new Vector(monKnockback * Math.sin(Math.toRadians(angleDeg)),
				-1 * (monKnockback * Math.cos(Math.toRadians(angleDeg))));
		final Vector playerVel = new Vector(-1 * (playerKnockback * Math.sin(Math.toRadians(angleDeg))),
				playerKnockback * Math.cos(Math.toRadians(angleDeg)));

		if (otherEntity.timeline != null) {
			if (otherEntity.timeline != null) {
				otherEntity.timeline.pause();
			}
			otherEntity.currentsprite.setCurrentSprite(12);

			if (otherEntity.timeline != null) {
				otherEntity.timeline.play();
			}
		}

		thisSprite.updateVelX(monVel.x());
		thisSprite.updateVelY(monVel.y());
		otherEntity.updatePlayerSwordVel(playerVel);

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
					otherEntity.updatePlayerSwordVel(playerVel.scale(0.5).reverseSign());
					Thread.sleep(100);
					otherEntity.updatePlayerSwordVel(playerVel.scale(0.5).reverseSign());
				} catch (InterruptedException ex) {
				}
			}
		}.start();
	}

	/***
	 * Bounces monsters away from each other.
	 * 
	 * @param thisSprite  - first entity sprite
	 * @param otherSprite - second entity sprite
	 * @param thisEntity   - first entity
	 * @param otherEntity  - second entity
	 */
	public static void sameEntityTypeCollision(Sprite thisSprite, Sprite otherSprite, GenericEntity thisEntity,
			GenericEntity otherEntity) {
		Vector v = new Vector();
		double e1Knockback = 1.0;
		double e2Knockback = 1.0;

		double angleDeg = v.angleBetweenTwoSprites(thisSprite.getPositionCentre(),
				otherSprite.getPositionCentre());

		Vector e2Vel = new Vector(e1Knockback * Math.sin(Math.toRadians(angleDeg)),
				-1 * (e1Knockback * Math.cos(Math.toRadians(angleDeg))));
		Vector e1Vel = new Vector(-1 * (e2Knockback * Math.sin(Math.toRadians(angleDeg))),
				e2Knockback * Math.cos(Math.toRadians(angleDeg)));

		thisSprite.updateVelX(e1Vel.x());
		thisSprite.updateVelY(e1Vel.y());
		otherSprite.updateVelX(e2Vel.x());
		otherSprite.updateVelY(e2Vel.y());
	}

	/***
	 * Player hit by a sword. Takes damage and knockback.
	 * 
	 * @param thisEntity  - player hit by sword
	 * @param thisSprite  - player hit by sword sprite
	 * @param otherEntity - entity who owns sword
	 */
	public static void PlayerHitEntitySwordCollision(ComplexEntity thisEntity, Sprite thisSprite,
			ComplexEntity otherEntity) {
		thisEntity.setInvulnerableTime(25);

		Vector v = new Vector();

		double knockback = otherEntity.getSword().getKnockback();

		thisEntity.setHealth(
				thisEntity.getHealth() - ((otherEntity.getSword().getDamage() + otherEntity.getExtraSwordDmg())
						/ (1 + (thisEntity.getDefence() / 10))));

		double angleDeg = v.angleBetweenTwoSprites(otherEntity.getEntitySprite().getPositionCentre(),
				thisSprite.getPositionCentre());

		Vector vel = new Vector(knockback * Math.sin(Math.toRadians(angleDeg)),
				-1 * (knockback * Math.cos(Math.toRadians(angleDeg))));

		if (thisEntity instanceof Player) {
			((Player) thisEntity).updatePlayerSwordVel(vel);
			Player player = (Player) thisEntity;
			if (player.timeline != null) {
				if (player.timeline != null) {
					player.timeline.pause();
				}
				player.currentsprite.setCurrentSprite(12);

				if (player.timeline != null) {
					player.timeline.play();
				}
			}

			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(100);
						((Player) thisEntity).updatePlayerSwordVel(vel.scale(0.5).reverseSign());
						Thread.sleep(100);
						((Player) thisEntity).updatePlayerSwordVel(vel.scale(0.5).reverseSign());
					} catch (InterruptedException ex) {
					}
				}
			}.start();
		}
	}

	/***
	 * Monster hit by a sword. Takes damage and knockback.
	 * 
	 * @param thisEntity  - monster hit by sword
	 * @param thisSprite  - monster hit by sword sprite
	 * @param otherEntity - entity who owns sword
	 */
	public static void MonHitEntitySwordCollision(ComplexEntity thisEntity, Sprite thisSprite,
			ComplexEntity otherEntity) {
		thisEntity.setInvulnerableTime(6);

		Vector v = new Vector();

		double knockback = 4.0;

		thisEntity.setHealth(
				thisEntity.getHealth() - ((otherEntity.getSword().getDamage() + otherEntity.getExtraSwordDmg())
						/ (1 + (thisEntity.getDefence() / 10))));

		// System.out.println("sword collision, isOffline = "+isOffline); TODO
		if (isOffline) {
			thisEntity.getSfx().PlayTrack(1);
		}

		double angleDeg = v.angleBetweenTwoSprites(otherEntity.getEntitySprite().getPositionCentre(),
				thisSprite.getPositionCentre());

		Vector vel = new Vector(knockback * Math.sin(Math.toRadians(angleDeg)),
				-1 * (knockback * Math.cos(Math.toRadians(angleDeg))));

		thisSprite.updateVelX(vel.x());
		thisSprite.updateVelY(vel.y());
	}

	/***
	 * Entity hit by a sword. Takes damage and knockback.
	 * 
	 * @param thisEntity  - entity hit by sword
	 * @param thisSprite  - entity hit by sword sprite
	 * @param otherEntity - entity who owns sword
	 */
	public static void entitySwordCollision(ComplexEntity thisEntity, Sprite thisSprite, ComplexEntity otherEntity) {
		// System.out.println("sword collision, isOffline = "+isOffline);
		if (thisEntity.getInvulnerableTime() > 0)
			return;

		thisEntity.setInvulnerableTime(25);

		/*
		 * if (isOffline) { if (thisEntity.getEntityType() == Type.PLAYER) {
		 * ((ComplexEntity) thisEntity).getSfx().PlayTrack(2); } else {
		 * System.out.println("hit mon w sword sound"); ((ComplexEntity)
		 * thisEntity).getSfx().PlayTrack(1); } }
		 */

		Vector v = new Vector();

		double knockback = otherEntity.getSword().getKnockback();

		thisEntity.setHealth(
				thisEntity.getHealth() - ((otherEntity.getSword().getDamage() + otherEntity.getExtraSwordDmg())
						/ (1 + (thisEntity.getDefence() / 10))));

		double angleDeg = v.angleBetweenTwoSprites(otherEntity.getEntitySprite().getPositionCentre(),
				thisSprite.getPositionCentre());

		Vector vel = new Vector(knockback * Math.sin(Math.toRadians(angleDeg)),
				-1 * (knockback * Math.cos(Math.toRadians(angleDeg))));

		if (thisEntity instanceof Player) {
			((Player) thisEntity).updatePlayerSwordVel(vel);
			Player player = (Player) thisEntity;
			if (player.timeline != null) {
				if (player.timeline != null) {
					player.timeline.pause();
				}
				player.currentsprite.setCurrentSprite(12);

				if (player.timeline != null) {
					player.timeline.play();
				}
			}

			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(100);
						((Player) thisEntity).updatePlayerSwordVel(vel.scale(0.5).reverseSign());
						Thread.sleep(100);
						((Player) thisEntity).updatePlayerSwordVel(vel.scale(0.5).reverseSign());
					} catch (InterruptedException ex) {
					}
				}
			}.start();
		} else {
			thisSprite.updateVelX(vel.x());
			thisSprite.updateVelY(vel.y());
		}
	}

	/***
	 * Entity hit by a projectile. Takes damage and knockback.
	 * 
	 * @param thisEntity      - entity hit by projectile
	 * @param thisSprite      - entity hit by projectile sprite
	 * @param otherProjectile - projectile that hit entity
	 * @param otherEntity     - entity who owns projectile
	 */
	public static void entityProjectileCollision(ComplexEntity thisEntity, Sprite thisSprite,
			GenericProjectile otherProjectile, ComplexEntity otherEntity) {

		if (!(otherProjectile instanceof Magic)) // System.out.println("");
			otherProjectile.removeFromOwner();

		if (otherProjectile.getOwner().getEntityType() == Type.MONSTER && thisEntity.getEntityType() == Type.MONSTER)
			return;
		if (thisEntity.getInvulnerableTime() > 0)
			return;

		thisEntity.setInvulnerableTime(25);

		if (thisEntity.getEntityType() == Type.PLAYER) {

			thisEntity.getSfx().PlayTrack(2);
		} else {
			thisEntity.getSfx().PlayTrack(1);
		}

		double knockback = otherProjectile.getKnockback();

		if (otherProjectile instanceof ArrowProjectile) {
			thisEntity
					.setHealth(thisEntity.getHealth() - ((otherProjectile.getDamage() + otherEntity.getExtraArrowDmg())
							/ (1 + (thisEntity.getDefence() / 10))));
		} else if (otherProjectile instanceof Magic) {
			thisEntity
					.setHealth(thisEntity.getHealth() - ((otherProjectile.getDamage() + otherEntity.getExtraMagicDmg())
							/ (1 + (thisEntity.getDefence() / 10))));
		} else {
			thisEntity.setHealth(
					thisEntity.getHealth() - (otherProjectile.getDamage() / (1 + (thisEntity.getDefence() / 10))));
		}

		double angleDeg = otherProjectile.getAngleDeg();

		Vector vel = new Vector(knockback * Math.sin(Math.toRadians(angleDeg)),
				-1 * (knockback * Math.cos(Math.toRadians(angleDeg))));

		if (thisEntity instanceof Player) {
			((Player) thisEntity).updatePlayerSwordVel(vel);

			Player player = (Player) thisEntity;
			if (player.timeline != null) {
				if (player.timeline != null) {
					player.timeline.pause();
				}
				player.currentsprite.setCurrentSprite(12);

				if (player.timeline != null) {
					player.timeline.play();
				}
			}

			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(100);
						((Player) thisEntity).updatePlayerSwordVel(vel.scale(0.5).reverseSign());
						Thread.sleep(100);
						((Player) thisEntity).updatePlayerSwordVel(vel.scale(0.5).reverseSign());
					} catch (InterruptedException ex) {
					}
				}
			}.start();
		} else {
			thisSprite.updateVelX(vel.x());
			thisSprite.updateVelY(vel.y());
		}
	}

	/***
	 * Entity sprite collided with an edge of a screen.
	 * 
	 * @param thisSprite - entity colliding with screen edge
	 * @param edge        - side of screen collided with
	 */
	public static void screenCollision(Sprite thisSprite, SideOfScreen edge) {
		thisSprite.inhibitHitboxFor(50000);
		GenericEntity thisEntity = null;
		List<GenericEntity> entities = retriever.getEntities().stream().flatMap(List::stream)
				.collect(Collectors.toList());
		for (GenericEntity e : entities) { // Find entity of this sprite
			if (e.getEntitySprite() == thisSprite) {
				thisEntity = e;
				break;
			}
		}

		if (thisEntity == null)
			return;

		Vector prevPos = thisEntity.getPosition();
		if (edge == SideOfScreen.LEFT) {
			thisEntity.setPosition(new Vector(0.0, thisEntity.getPosition().y()));
		} else if (edge == SideOfScreen.RIGHT) {
			thisEntity.setPosition(new Vector(1200 - thisSprite.preferSize().x(), thisEntity.getPosition().y()));
		} else if (edge == SideOfScreen.BOTTOM) {
			thisEntity.setPosition(new Vector(thisEntity.getPosition().x(), 700 - thisSprite.preferSize().y()));
		} else if (edge == SideOfScreen.TOP) {
			thisEntity.setPosition(new Vector(thisEntity.getPosition().x(), 0.0));
		}

		if (thisEntity instanceof Player) {
			Player player = (Player) thisEntity;
			player.getSword().setPosition(new Vector(player.getSword().getPosition().x(),
					player.getSword().getPosition().y() + (player.getPosition().y() - prevPos.y())));

		}
	}

	public static void setIsOffline(Boolean state) {
		isOffline = state;
	}

}
