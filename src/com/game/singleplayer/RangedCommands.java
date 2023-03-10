package com.game.singleplayer;

import com.game.entities.ArrowProjectile;
import com.game.entities.ComplexEntity;
import com.game.entities.GenericEntity.Type;
import com.game.entities.GenericMonster;
import com.game.entities.GenericProjectile;
import com.game.entities.Magic;
import com.game.entities.Player;
import com.game.entities.RockProjectile;
import com.game.graphics.Renderer;
import com.game.utilities.Vector;

public class RangedCommands {

	Renderer renderer;

	/***
	 * Generic Use of a projectile. Fires a projectile towards the target.
	 * 
	 * @param e      - Any Generic Entity.
	 * @param target - The target vector for the arrow to go to.
	 */
	public static GenericProjectile projAttack(ComplexEntity e, Vector target, Renderer renderer) {

		Vector attacker = e.getEntitySprite().getPositionCentre();

		GenericProjectile projectile = e.getProjectile();

		double projRotVel = projectile.getRotVel();
		double angleDeg = attacker.angleBetweenTwoSprites(attacker, target);
		Vector projVel = new Vector(projectile.getSpeed() * Math.sin(Math.toRadians(angleDeg)),
				-1 * (projectile.getSpeed() * Math.cos(Math.toRadians(angleDeg))));

		GenericProjectile actualProjectile = projectile.shoot(attacker, projVel, angleDeg, projRotVel, e);
		actualProjectile.setOwner(e);
		e.addProjectile(actualProjectile);
		// actualProjectile.setSfx();

		if (e.getEntityType() == Type.PLAYER && actualProjectile instanceof ArrowProjectile) {
			Player p = (Player) e;
			p.addArrows(-1);
			actualProjectile.setScreenId(p.getScreenId());
			System.out.println("proj id " + actualProjectile.getScreenId());
			actualProjectile.pubAddEntity();
			actualProjectile.addToRenderer(renderer);

			e.getSfx().PlayTrack(4);
		} else if (e.getEntityType() == Type.PLAYER && actualProjectile instanceof Magic) {
			Player p = (Player) e;
			actualProjectile.setScreenId(p.getScreenId());
			actualProjectile.addToRenderer(renderer);
			actualProjectile.pubAddEntity();

			e.getSfx().PlayTrack(5);
		} else if (e.getEntityType() == Type.MONSTER && actualProjectile instanceof RockProjectile) {
			GenericMonster m = (GenericMonster) e;
			actualProjectile.setScreenId(m.getScreenId());
			actualProjectile.addToRenderer(renderer);
			actualProjectile.pubAddEntity();
		} else {
			System.out.println("not recognized");
		}

		e.setAtkCooldown(actualProjectile.getFireRate() * 60);

		if (e instanceof GenericMonster) {
			GenericMonster em = (GenericMonster) e;
			em.facing(target);
		}

		return actualProjectile;
	}
}
