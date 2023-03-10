package com.game.commands;

import com.game.entities.ArrowProjectile;
import com.game.entities.ComplexEntity;
import com.game.entities.GenericEntity.Type;
import com.game.entities.GenericMonster;
import com.game.entities.GenericProjectile;
import com.game.entities.Magic;
import com.game.entities.Player;
import com.game.entities.RockProjectile;
import com.game.utilities.Vector;

public class RangedCommands {

	/***
	 * Generic Use of a projectile. Fires a projectile towards the target.
	 * 
	 * @param e      - Any Generic Entity.
	 * @param target - The target vector for the arrow to go to.
	 */
	public static void projAttack(ComplexEntity e, Vector target) {

		Vector attacker = e.getEntitySprite().getPositionCentre();

		GenericProjectile projectile = e.getProjectile();
		double projRotVel = projectile.getRotVel();
		double angleDeg = attacker.angleBetweenTwoSprites(attacker, target);
		Vector projVel = new Vector(projectile.getSpeed() * Math.sin(Math.toRadians(angleDeg)),
				-1 * (projectile.getSpeed() * Math.cos(Math.toRadians(angleDeg))));

		GenericProjectile actualProjectile = projectile.shoot(attacker, projVel, angleDeg, projRotVel, e);
		actualProjectile.setOwner(e);
		e.addProjectile(actualProjectile);

		// TODO addSprite

		if (e.getEntityType() == Type.PLAYER && actualProjectile instanceof ArrowProjectile) {
			((Player) e).addArrows(-1);
			// e.getSfx().PlayTrack(4);
		} else if (e.getEntityType() == Type.PLAYER && actualProjectile instanceof Magic) {
			// e.getSfx().PlayTrack(5);
		} else if (e.getEntityType() == Type.MONSTER && actualProjectile instanceof RockProjectile) {
			// e.getSfx().PlayTrack(2);
		}

		e.setAtkCooldown(actualProjectile.getFireRate() * 60);

		if (e instanceof GenericMonster) {
			GenericMonster em = (GenericMonster) e;
			em.facing(target);
		}
	}
}
