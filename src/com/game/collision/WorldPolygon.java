package com.game.collision;

import java.util.List;

import com.game.utilities.Vector;

/**
 * Represents a polygon that has been translated into world space. A different
 * class for this helps the type-checker find possible logical errors.
 *
 * @author mxb1143
 */
public class WorldPolygon extends Polygon {

	// TODO: this is only used to determine if a value is 0 or 2PI, to we can
	// probably just round instead.
	/**
	 * Comparing doubles can be tricky, so set a threshold for which doubles are
	 * "equal"
	 */
	final double doubleComparisonThreshold = 0.001;

	public WorldPolygon(List<Vector> points) {
		super(points);
	}

	/**
	 * Check for intersection using the winding-number method. In this case,
	 * "collision" means that a vertex of one polygon is within the bounds of the
	 * other. See
	 * {@see https://cs.stackexchange.com/questions/28656/calculate-winding-number}.
	 * this Tis the more expensive solution, but it can be swapped out for a cheaper
	 * method should performance suffer.
	 */
	public boolean intersectsWith(WorldPolygon other) {
		return oneWayIntersectsWith(other) || other.oneWayIntersectsWith(this);
	}

	/**
	 * Where collision checking is actually done. This is only one-way, so it is
	 * done both ways in `intersectsWith`.
	 */
	private boolean oneWayIntersectsWith(WorldPolygon other) {
		for (Vector point : this.points) {
			double windingNumber = other.toLines().stream().map(line -> line.angleSubtendedBy(point)).reduce(0.0,
					(a, b) -> a + b);
			if (Math.abs(windingNumber - (2 * Math.PI)) < doubleComparisonThreshold) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calculates whether two world polygons are the same
	 * 
	 * @param obj the object this WorldPolygon is being compared with
	 * @return Whether they are the same or not (true/false)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;

		} else if (obj == null) {
			return false;

		} else if (getClass() != obj.getClass()) {
			return false;

		} else {
			WorldPolygon wp = (WorldPolygon) obj;
			return wp.points.equals(this.points);
		}
	}

}
