package com.game.collision;

import java.util.List;

import com.game.utilities.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A description of a hittable area, which consists of a list of
 * {@link com.game.collision.Polygon}. The stored fields represent a hitbox that
 * is centered at (0, 0), and it is translated to world space before any actual
 * collision detection takes place.
 *
 * @author mxb1143
 */
public class Hitbox {

	private List<Polygon> components;
	/**
	 * A timestamp, before which all collision detection will automatically fail.
	 */
	private long inhibitUntil;

	public Hitbox(List<Polygon> components) {
		this.components = components;
		this.inhibitUntil = System.nanoTime();
	}

	/**
	 * The heart of the collision detection, this translates two hitboxes to world
	 * space and checks for collisions.
	 * 
	 * @param other               The other hitbox.
	 * @param thisWorldPosition The coordinates to which the hitbox center will be
	 *                            translated before collision checks.
	 * @param thisRotation       The rotation that will be applied to this hitbox
	 *                            before collision checks, in degrees.
	 * @param thisWorldPosition The coordinates to which the hitbox center of the
	 *                            other hitbox will be translated before collision
	 *                            checks.
	 * @param thisRotation       The rotation that will be applied to the other
	 *                            hitbox before collision checks, in degrees.
	 * @return If this and other are colliding.
	 */
	public boolean isCollidingWith(Hitbox other, Vector thisWorldPosition, double thisRotation,
			Vector otherWorldPosition, double otherRotation) {
		if (isInhibited()) {
			return false;
		}
		for (Polygon polygon : components) {
			for (Polygon otherPolygon : other.components) {
				if (polygon.translateToWorldSpace(thisWorldPosition, thisRotation).intersectsWith(
						otherPolygon.translateToWorldSpace(otherWorldPosition, otherRotation))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * An axis-aligned collision check, useful for checking if any part of a hitbox
	 * is beyond a certain threshold
	 */
	public boolean isLessThanXAlignedAxis(double x, Vector thisWorldPosition, double thisRotation) {
		if (isInhibited()) {
			return false;
		}
		for (Polygon polygon : components) {
			for (Vector point : polygon.translateToWorldSpace(thisWorldPosition, thisRotation).points) {
				if (point.x() < x) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * An axis-aligned collision check, useful for checking if any part of a hitbox
	 * is beyond a certain threshold
	 */
	public boolean isGreaterThanXAlignedAxis(double x, Vector thisWorldPosition, double thisRotation) {
		if (isInhibited()) {
			return false;
		}
		for (Polygon polygon : components) {
			for (Vector point : polygon.translateToWorldSpace(thisWorldPosition, thisRotation).points) {
				if (point.x() > x) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * An axis-aligned collision check, useful for checking if any part of a hitbox
	 * is beyond a certain threshold
	 */
	public boolean isLessThanYAlignedAxis(double y, Vector thisWorldPosition, double thisRotation) {
		if (isInhibited()) {
			return false;
		}
		for (Polygon polygon : components) {
			for (Vector point : polygon.translateToWorldSpace(thisWorldPosition, thisRotation).points) {
				if (point.y() < y) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * An axis-aligned collision check, useful for checking if any part of a hitbox
	 * is beyond a certain threshold
	 */
	public boolean isGreaterThanYAlignedAxis(double y, Vector thisWorldPosition, double thisRotation) {
		if (isInhibited()) {
			return false;
		}
		for (Polygon polygon : components) {
			for (Vector point : polygon.translateToWorldSpace(thisWorldPosition, thisRotation).points) {
				if (point.y() > y) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Allows a hitbox to be drawn for debugging purposes.
	 */
	public void draw(GraphicsContext gc, Vector thisWorldPosition, double thisRotation) {
		components.forEach(polygon -> polygon.translateToWorldSpace(thisWorldPosition, thisRotation).draw(gc,
				isInhibited() ? Color.BLUE : Color.RED));
	}

	/**
	 * Prevent a hitbox colliding for a specified duration from now.
	 * 
	 * @param nanoseconds How long to inhibit this hitbox, in nanoseconds.
	 */
	public void inhibitFor(long nanoseconds) {
		inhibitUntil = System.nanoTime() + nanoseconds;
	}

	/**
	 * Check if this hitbox is still inhibited.
	 */
	public boolean isInhibited() {
		return System.nanoTime() < inhibitUntil;
	}

}
