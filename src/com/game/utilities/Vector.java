package com.game.utilities;

// Simple structure for managing a 2-tuple pair of doubles.
public class Vector {

	private double x;
	private double y;

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/***
	 * Default Constructor - should be (0,0)
	 */
	public Vector() {

	}

	// Primitives are copied; use double rather than Double
	public double x() {
		return this.x;
	}

	public double y() {
		return this.y;
	}

	public double magnitude() {
		return Math.sqrt(Math.pow(this.y, 2.0) + Math.pow(this.x, 2.0));
	}

	public Vector clampX(double max) {
		return new Vector(Math.min(this.x, max), this.y);
	}

	public Vector clampY(double max) {
		return new Vector(this.x, Math.min(this.y, max));
	}

	public Vector clampMagnitude(double max) {
		double magnitude = this.magnitude();
		if (magnitude > max) {
			return new Vector((this.x / magnitude) * max, (this.y / magnitude) * max);
		}
		return this;
	}

	public Vector scaleToMagnitude(double magnitude) {
		return new Vector((this.x / this.magnitude()) * magnitude, (this.y / this.magnitude()) * magnitude);
	}

	public Vector clampComponentsToVector(Vector max) {
		return new Vector(Math.min(this.x, max.x), Math.min(this.y, max.y));
	}

	public Vector scale(Double factor) {
		return new Vector(this.x * factor, this.y * factor);
	}

	public Vector add(Vector other) {
		return new Vector(this.x + other.x, this.y + other.y);
	}

	public Vector reverseSign() {
		return new Vector(-this.x, -this.y);
	}

	public Vector updateX(double x) {
		this.x = this.x + x;
		return this;
	}

	public Vector updateY(double y) {
		this.y = this.y + y;
		return this;
	}

	public Vector makeComponentsAbs() {
		return new Vector(Math.abs(this.x), Math.abs(this.y));
	}

	public Vector makeComponentsZeroIfNegative() {
		return new Vector(Math.max(this.x, 0), Math.max(this.y, 0));
	}

	public double dotProduct(Vector other) {
		return this.x * other.x + this.y * other.y;
	}

	public Vector rotateClockwiseAroundOrigin(double rotation) {
		double radRotation = degreesToRadians(rotation);
		return new Vector((x * Math.cos(radRotation)) - (y * Math.sin(radRotation)),
				(x * Math.sin(radRotation)) + (y * Math.cos(radRotation)));
	}

	private double degreesToRadians(double degrees) {
		return degrees * ((2 * Math.PI) / 360.0);
	}

	/***
	 * Calculates the angle between sprite 1 and sprite 2. The angle starts from the
	 * y axis above sprite 1 and goes clockwise.
	 */
	public double angleBetweenTwoSprites(Vector sprite1, Vector sprite2) {

		/*
		 * Calculate angle between sprite1 and sprite2 - y axis being flipped adds
		 * complexity | / | / | / | / |0/ Calculates angle between y axis and sprite2
		 * going clockwise |/ (0,1)
		 */

		double angleDeg = 0;
		if (sprite2.y() < sprite1.y()) {
			angleDeg = Math.toDegrees(Math.atan((sprite2.x() - sprite1.x()) / (sprite2.y() - sprite1.y()))) * -1;
			if (angleDeg < 0) {
				angleDeg = 360 + angleDeg;
			}
		} else {
			angleDeg = 180 - (Math.toDegrees(Math.atan((sprite2.x() - sprite1.x()) / (sprite2.y() - sprite1.y()))));
		}
		return angleDeg;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;

		} else if (obj == null) {
			return false;

		} else if (getClass() != obj.getClass()) {
			return false;

		} else {
			Vector v = (Vector) obj;
			return Double.compare(v.x, this.x) == 0 && Double.compare(v.y, this.y) == 0;

		}
	}
}
