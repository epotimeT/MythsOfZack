package com.game.collision;

import com.game.utilities.Vector;

/**
 * A fairly simple representation of a line segment in world space.
 *
 * @author mxb1143
 */
public class Line {

	/**
	 * A vector from (0, 0) at which the line segments "starts"
	 */
	public Vector startOfSegment;

	/**
	 * A vector from (0, 0) at which the line segments "ends"
	 */
	public Vector endOfSegment;

	public Line(Vector startOfSegment, Vector endOfSegment) {
		this.startOfSegment = startOfSegment;
		this.endOfSegment = endOfSegment;
	}

	/**
	 * Calcuates the angle subtended by a point on this line.
	 * 
	 * @param point A point in world space.
	 * @return The angle subtended by point.
	 */
	public double angleSubtendedBy(Vector point) {
		Vector pointToStartOfLine = this.startOfSegment.add(point.scale(-1.0));
		Vector pointToEndOfLine = this.endOfSegment.add(point.scale(-1.0));
		return Math.acos(pointToStartOfLine.dotProduct(pointToEndOfLine)
				/ (pointToStartOfLine.magnitude() * pointToEndOfLine.magnitude()));
	}

	/**
	 * Calculates whether two lines are the same
	 * 
	 * @param obj the object this Line is being compared with
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
			Line l = (Line) obj;
			return l.startOfSegment.equals(this.startOfSegment) && l.endOfSegment.equals(this.endOfSegment);

		}
	}

}
