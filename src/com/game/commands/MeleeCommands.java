package com.game.commands;

import com.game.entities.ComplexEntity;
import com.game.entities.GenericEntity.Type;
import com.game.entities.GenericWeapon;
import com.game.utilities.Vector;

public class MeleeCommands {

	/**
	 * Deploys a melee weapon (e.g. sword) determined by the target (e.g. mouse
	 * click).
	 * 
	 * @param e      - Entity that deployed weapon
	 * @param target - Location in which the weapon will deploy towards
	 */
	public static void deployMeleeWeapon(ComplexEntity e, Vector target) {
		if (e.getAtkCooldown() > 0.0)
			return;
		GenericWeapon weapon = e.getSword();
		weapon.getEntitySprite().setRotationalVelocity(0.0);
		weapon.getEntitySprite().setRotation(0.0);

		double eMidX = e.getEntitySprite().getPositionCentre().x();
		double angleDeg = target.angleBetweenTwoSprites(e.getEntitySprite().getPositionCentre(), target);

		Vector disFromEntityToWeapon = new Vector();

		double xDis = ((e.getEntitySprite().getRenderSize().x() + weapon.getEntitySprite().getRenderSize().x())
				* Math.tan(Math.toRadians(angleDeg)));
		if (xDis > e.getEntitySprite().getRenderSize().x()) { // Limits size of distance
			xDis = e.getEntitySprite().getRenderSize().x();
		} else if (xDis < -(e.getEntitySprite().getRenderSize().x()
				+ weapon.getEntitySprite().getRenderSize().x())) {
			xDis = -(e.getEntitySprite().getRenderSize().x() + weapon.getEntitySprite().getRenderSize().x());
		}

		double yDis = ((e.getEntitySprite().getRenderSize().x() + weapon.getEntitySprite().getRenderSize().x())
				/ Math.tan(Math.toRadians(angleDeg)));
		if (yDis > (e.getEntitySprite().getRenderSize().x() + weapon.getEntitySprite().getRenderSize().x())) {
			yDis = (e.getEntitySprite().getRenderSize().x() + weapon.getEntitySprite().getRenderSize().x());
		} else if (yDis < -(e.getEntitySprite().getRenderSize().x()
				+ weapon.getEntitySprite().getRenderSize().x())) {
			yDis = -(e.getEntitySprite().getRenderSize().x() + weapon.getEntitySprite().getRenderSize().x());
		}

		disFromEntityToWeapon = new Vector(xDis, yDis);

		// Set position of sword and rotational velocity
		// eX + 50.0 -> Number sets the position of sword x axis to prevent it
		// overlapping the e
		if (angleDeg >= 50 && angleDeg <= 130) { // Right side attack
			weapon.getEntitySprite().setPosition(
					new Vector(eMidX + 50.0, e.getEntitySprite().position().y() - disFromEntityToWeapon.y()));
			weapon.getEntitySprite().setRotationalVelocity(weapon.getAttackRate());
			weapon.getEntitySprite().setRotation(angleDeg - 67.5);
		} else if (angleDeg >= 230 && angleDeg <= 310) { // Left side attack
			weapon.getEntitySprite().setPosition(
					new Vector(eMidX - 60.0, e.getEntitySprite().position().y() + disFromEntityToWeapon.y()));
			weapon.getEntitySprite().setRotationalVelocity(-weapon.getAttackRate());
			weapon.getEntitySprite().setRotation(angleDeg + 67.5);
		} else if (angleDeg <= 50) { // Top right side attack
			weapon.getEntitySprite().setPosition(new Vector(eMidX + disFromEntityToWeapon.x(),
					e.getEntitySprite().position().y() - (e.getEntitySprite().getRenderSize().x() + 10.0)));

			weapon.getEntitySprite().setRotationalVelocity(weapon.getAttackRate());
			weapon.getEntitySprite().setRotation(angleDeg - 67.5);
		} else if (angleDeg >= 310) { // Top left side attack
			weapon.getEntitySprite().setPosition(new Vector(eMidX + disFromEntityToWeapon.x(),
					e.getEntitySprite().position().y() - (e.getEntitySprite().getRenderSize().x() + 10.0)));

			weapon.getEntitySprite().setRotationalVelocity(-weapon.getAttackRate());
			weapon.getEntitySprite().setRotation(angleDeg + 67.5);
		} else if (angleDeg <= 180) { // Bottom right side attack
			weapon.getEntitySprite()
					.setPosition(new Vector(
							eMidX - (disFromEntityToWeapon.x() + weapon.getEntitySprite().getRenderSize().x()),
							e.getEntitySprite().position().y() + (e.getEntitySprite().getRenderSize().x() + 10.0)));

			weapon.getEntitySprite().setRotationalVelocity(-weapon.getAttackRate());
			weapon.getEntitySprite().setRotation(angleDeg + 67.5);
		} else if (angleDeg <= 230) { // // Bottom left side attack
			weapon.getEntitySprite().setPosition(new Vector(eMidX - disFromEntityToWeapon.x(),
					e.getEntitySprite().position().y() + (e.getEntitySprite().getRenderSize().x() + 10.0)));

			weapon.getEntitySprite().setRotationalVelocity(weapon.getAttackRate());
			weapon.getEntitySprite().setRotation(angleDeg - 67.5);
		}

		if (e.getEntityType() == Type.PLAYER) {
			e.getSfx().PlayTrack(1);
		}

		e.setAtkCooldown(weapon.getAttackRate() * 60);
		e.setSwordSwung(true);
	}

}