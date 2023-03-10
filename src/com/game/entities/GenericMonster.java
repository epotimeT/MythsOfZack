package com.game.entities;

import java.util.ArrayList;
import java.util.List;

import com.game.commands.GenericCommands;
import com.game.utilities.Vector;

public abstract class GenericMonster extends ComplexEntity implements GenericCommands {

	private float detectionRadius;
	private final float playerOffset = 25f;

	public abstract void updateState();

	public abstract void performAction();

	public abstract void deathAnimation();

	public float getDetectionRadius() {
		return detectionRadius;
	}

	public void setDetectionRadius(float detectionRadius) {
		this.detectionRadius = detectionRadius;
	}

	/***
	 * A Function used to get the offset positions of the players currently in the
	 * game.
	 * 
	 * @param entities - The list of entities currently in the game.
	 * @return A list of vectors, representing the players' positions.
	 */
	public List<Vector> getPlayerLocations(List<GenericEntity> entities) {
		ArrayList<Vector> out = new ArrayList<>();
		for (GenericEntity e : entities) {
			if (e instanceof Player) {
				Vector pos = new Vector(e.getPosition().x() + playerOffset, e.getPosition().y() + playerOffset);
				out.add(pos);
			}
		}
		return out;
	}

	/***
	 * A function that calculates which of its inputs is closest to its location.
	 * 
	 * @param vList - The list of vectors, most commonly the list of player
	 *              locations.
	 * @return The vector that is the closest to the monster's current vector.
	 */
	public Vector ClosestPlayer(List<Vector> vList) {
		double smallestDist = -1;
		Vector out = null;
		for (Vector playerPosition : vList) {
			double currDist = getDistance(playerPosition);
			if (smallestDist == -1) {
				smallestDist = currDist;
				out = playerPosition;
			} else if (currDist < smallestDist) {
				smallestDist = currDist;
				out = playerPosition;
			}
		}
		return out;
	}

	/***
	 * Gets the ordinal distance between the monster's current vector, and another.
	 * 
	 * @param other - The other vector used for the calculation.
	 * @return A double representing the distance between the 2 points.
	 */
	public double getDistance(Vector other) {
		Vector thisPos = this.getPosition();
		double x = Math.abs(other.x() - thisPos.x());
		double y = Math.abs(other.y() - thisPos.y());
		return Math.hypot(x, y);
	}

	/***
	 * Gets the ordinal distance between 2 vectors.
	 * 
	 * @param one   - the first vector used for the calculation.
	 * @param other - The other vector used for the calculation.
	 * @return A double representing the distance between the 2 points.
	 */
	public double getDistance(Vector one, Vector other) {
		double x = Math.abs(other.x() - one.x());
		double y = Math.abs(other.y() - one.y());
		return Math.hypot(x, y);
	}

	/**
	 * Gets the vector of the closest player using the default entity list.
	 * 
	 * @return A vector representing the location of the closest player.
	 */
	public Vector getClosestPlayer() {
		return ClosestPlayer(getPlayerLocations(getEntities().get(screenId)));
	}

	// State machines
	public enum State {
		IDLE, ALERTED, FIGHTING, DEAD
	}

	State currState;

	public State getCurrState() {
		return this.currState;
	}

	protected void toDead() {
		this.currState = State.DEAD;
	}

	public abstract void facing(Vector towards);

	protected void toIdle() {
		this.currState = State.IDLE;
	}

	protected void toAlerted() {
		this.currState = State.ALERTED;
	}

	protected void toFighting() {
		this.currState = State.FIGHTING;
	}

	/***
	 * Initialises a generic monster.
	 * 
	 * @param defaultState Sets up the default state of the monster.
	 */
	public GenericMonster(State defaultState) {
		currState = defaultState;
	}

	/***
	 * The default constructor.
	 * 
	 * Just has the state set to IDLE. Transitions need to be called in child
	 * classes.
	 */
	public GenericMonster() {
		currState = State.IDLE;
	}

	/***
	 * The default death function. Simply calls the removeEntity() function on
	 * itself.
	 */
	public void death() {
		this.removeEntity(this);
	}

}
