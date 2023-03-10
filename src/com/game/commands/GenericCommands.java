package com.game.commands;

import com.game.entities.ComplexEntity;
import com.game.utilities.Vector;

public interface GenericCommands {

	// Null Command
	public static void nullCommand() {
		;
	}

	// movement

	public enum Direction {
		UP, DOWN, RIGHT, LEFT
	}

	/***
	 * A Command facilitating basic orthogonal movement.
	 * 
	 * @param e   The entity evoking the command.
	 * @param dir The direction of movement, either UP, DOWN, LEFT or RIGHT.
	 */
	public static void move(ComplexEntity e, Direction dir) {
		double speed = e.getSpeed();
		double maxSpeed = e.getMaxSpeed();
		// System.out.println(Double.toString(e.getVelocity().x()) + " " +
		// Double.toString(e.getVelocity().y()));
		switch (dir) {
		case DOWN:
			if (e.getVelocity().y() > -maxSpeed) {
				e.getEntitySprite().updateVelY(-speed);
			}
			break;
		case LEFT:
			if (e.getVelocity().x() > -maxSpeed) {
				e.getEntitySprite().updateVelX(-speed);
			}
			break;
		case RIGHT:
			if (e.getVelocity().x() < maxSpeed) {
				e.getEntitySprite().updateVelX(speed);
			}
			break;
		case UP:
			if (e.getVelocity().y() < maxSpeed) {
				e.getEntitySprite().updateVelY(speed);
			}
			break;
		default:
			break;
		}
		e.setVelocity(e.getEntitySprite().velocity());
	}

	public static void stop(ComplexEntity e) {
		e.setVelocity(new Vector());
		e.getEntitySprite().setVelocity(new Vector());
	}

}
