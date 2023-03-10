package com.game.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import com.game.utilities.Vector;

/**
 * <h1>UtilitiesPackage</h1> Unit tests for the classes contained in the package
 * 'Utilities'
 */
class UtilitiesPackage {
	@Rule
	private Timeout utilitiesTimeout = new Timeout(200, TimeUnit.MILLISECONDS);

	@Rule
	private double precision = CollisionPackage.precision;

	// Testing Vector
	@Test
	void testInitialVector() {
		Vector vx = new Vector();
		assertEquals(vx.x(), 0, "initial vector x value is incorrect");
		assertEquals(vx.y(), 0, "initial vector y value is incorrect");
	}

	@Test
	void testGetXY() {
		Vector vx = new Vector(1, 2);
		assertEquals(vx.x(), 1, "retrieved x value is incorrect");
		assertEquals(vx.y(), 2, "retrieved y value is incorrect");

		Vector vy = new Vector(14.54, 12.32);
		assertEquals(vy.x(), 14.54, "retrieved x value is incorrect");
		assertEquals(vy.y(), 12.32, "retrieved y value is incorrect");

	}

	@Test
	void testMagnitude() {
		Vector vx = new Vector(4, 8);
		assertEquals(vx.magnitude(), 8.94427190999916, precision, "magnitude of vector incorrect");
	}

	@Test
	void testClampXY() {
		Vector vx = new Vector(2, 3);
		assertEquals(vx.clampX(1).x(), 1, "x is clamped incorrectly (<)");
		assertEquals(vx.clampX(3).x(), 2, "x is clamped incorrectly (>)");
		assertEquals(vx.clampX(2).x(), 2, "x is clamped incorrectly (=)");

		assertEquals(vx.clampY(1).y(), 1, "y is clamped incorrectly (<)");
		assertEquals(vx.clampY(5).y(), 3, "y is clamped incorrectly (>)");
		assertEquals(vx.clampY(3).y(), 3, "y is clamped incorrectly (=)");
	}

	@Test
	void testClampMagnitude() {
		Vector vx = new Vector(3, 4);

		// Basic cases
		assertEquals(vx.clampMagnitude(6).magnitude(), 5, "magnitude is clamped incorrectly (>)");
		assertEquals(vx.clampMagnitude(5).magnitude(), 5, "magnitude is clamped incorrectly (=)");
		assertEquals(vx.clampMagnitude(2).magnitude(), 2, "magnitude is clamped incorrectly (<)");

		// Check x and y are updated correctly when clamped
		assertEquals(vx.clampMagnitude(2).x(), 1.2, "magnitude x is clamped incorrectly");
		assertEquals(vx.clampMagnitude(2).y(), 1.6, "magnitude y is clamped incorrectly");
	}

	@Test
	void testScaleToMagnitude() {
		Vector vx = new Vector(3, 4);

		assertEquals(vx.scaleToMagnitude(3).x(), 1.8, precision, "magnitude scales incorrectly");
		assertEquals(vx.scaleToMagnitude(3).y(), 2.4, precision, "magnitude scales incorrectly");
	}

	@Test
	void testClampComponentsToVector() {
		Vector vx = new Vector(3, 4);

		Vector min = new Vector(2, 2);
		Vector minX = new Vector(2, 5);
		Vector minY = new Vector(5, 2);
		Vector edge = new Vector(3, 4);
		Vector max = new Vector(5, 5);

		assertEquals(vx.clampComponentsToVector(min).x(), 2, "vector clamps incorrectly on x (min)");
		assertEquals(vx.clampComponentsToVector(min).y(), 2, "vector clamps incorrectly on y (min)");

		assertEquals(vx.clampComponentsToVector(minX).x(), 2, "vector clamps incorrectly on x (minX)");
		assertEquals(vx.clampComponentsToVector(minX).y(), 4, "vector clamps incorrectly on y (minX)");

		assertEquals(vx.clampComponentsToVector(minY).x(), 3, "vector clamps incorrectly on x (minY)");
		assertEquals(vx.clampComponentsToVector(minY).y(), 2, "vector clamps incorrectly on y (minY)");

		assertEquals(vx.clampComponentsToVector(edge).x(), 3, "vector clamps incorrectly on x (edge)");
		assertEquals(vx.clampComponentsToVector(edge).y(), 4, "vector clamps incorrectly on y (edge)");

		assertEquals(vx.clampComponentsToVector(max).x(), 3, "vector clamps incorrectly on x (max)");
		assertEquals(vx.clampComponentsToVector(max).y(), 4, "vector clamps incorrectly on y (max)");

	}

	@Test
	void testScale() {
		Vector vx = new Vector(3, 4);

		assertEquals(vx.scale(3.0).x(), 9, "vector scales incorrectly on x");
		assertEquals(vx.scale(3.0).y(), 12, "vector scales incorrectly on y");

	}

	@Test
	void testAdd() {
		Vector vx = new Vector(3, 4);
		Vector vy = new Vector(5, 8);

		assertEquals(vx.add(vy).x(), 8, "vector adds incorrectly on x");
		assertEquals(vx.add(vy).y(), 12, "vector adds incorrectly on y");

	}

	@Test
	void testReverseSign() {
		Vector vx = new Vector(3, 4);
		Vector vy = new Vector(-3, -4);
		Vector vz = new Vector(3, -4);

		assertEquals(vx.reverseSign(), new Vector(-3, -4), "Vector's sign reversed incorrectly");
		assertEquals(vy.reverseSign(), new Vector(3, 4), "Vector's sign reversed incorrectly");
		assertEquals(vz.reverseSign(), new Vector(-3, 4), "Vector's sign reversed incorrectly");
	}

	@Test
	void testUpdateXY() {
		Vector vx = new Vector(3, 4);

		assertEquals(vx.updateX(2.7).x(), 5.7, "vector updates incorrectly on x");
		assertEquals(vx.updateY(1.8).y(), 5.8, "vector updates incorrectly on y");

	}

	@Test
	void testMakeComponentsAbs() {
		Vector neg = new Vector(-3, -4);
		Vector pos = new Vector(3, 4);
		Vector negX = new Vector(-3, 4);
		Vector negY = new Vector(3, -4);

		assertEquals(neg.makeComponentsAbs().x(), 3, "vector fails on absolute x (neg)");
		assertEquals(neg.makeComponentsAbs().y(), 4, "vector fails on absolute y (neg)");

		assertEquals(pos.makeComponentsAbs().x(), 3, "vector fails on absolute x (pos)");
		assertEquals(pos.makeComponentsAbs().y(), 4, "vector fails on absolute y (pos)");

		assertEquals(negX.makeComponentsAbs().x(), 3, "vector fails on absolute x (negX)");
		assertEquals(negX.makeComponentsAbs().y(), 4, "vector fails on absolute y (negX)");

		assertEquals(negY.makeComponentsAbs().x(), 3, "vector fails on absolute x (negY)");
		assertEquals(negY.makeComponentsAbs().y(), 4, "vector fails on absolute y (negY)");

	}

	@Test
	void testMakeComponentsZeroIfNegative() {
		Vector pos = new Vector(3, 4);
		Vector neg = new Vector(-3, -4);
		Vector negX = new Vector(-3, 4);
		Vector negY = new Vector(3, -4);

		assertEquals(pos.makeComponentsZeroIfNegative().x(), 3, "vector fails on negative x (pos)");
		assertEquals(pos.makeComponentsZeroIfNegative().y(), 4, "vector fails on negative y (pos)");

		assertEquals(neg.makeComponentsZeroIfNegative().x(), 0, "vector fails on negative x (neg)");
		assertEquals(neg.makeComponentsZeroIfNegative().y(), 0, "vector fails on negative y (neg)");

		assertEquals(negX.makeComponentsZeroIfNegative().x(), 0, "vector fails on negative x (negX)");
		assertEquals(negX.makeComponentsZeroIfNegative().y(), 4, "vector fails on negative y (negX)");

		assertEquals(negY.makeComponentsZeroIfNegative().x(), 3, "vector fails on negative x (negY)");
		assertEquals(negY.makeComponentsZeroIfNegative().y(), 0, "vector fails on negative y (negY)");
	}

	@Test
	void testDotProduct() {
		Vector vx = new Vector(2, 5);
		Vector vy = new Vector(3, 1);

		assertEquals(vx.dotProduct(vy), 11, "vector dot product is incorrect");

	}

	@Test
	void testRotateClockwiseAroundOrigin() {
		Vector vx = new Vector(5, 2);

		assertEquals(vx.rotateClockwiseAroundOrigin(-20).x(), 5.382503391, precision,
				"vector rotation incorrect on x (neg)");
		assertEquals(vx.rotateClockwiseAroundOrigin(-20).y(), 0.1692845249, precision,
				"vector rotation incorrect on y (neg)");

		assertEquals(vx.rotateClockwiseAroundOrigin(0).x(), 5, precision, "vector rotation incorrect on x (zero)");
		assertEquals(vx.rotateClockwiseAroundOrigin(0).y(), 2, precision, "vector rotation incorrect on y (zero)");

		assertEquals(vx.rotateClockwiseAroundOrigin(45).x(), 2.121320344, precision,
				"vector rotation incorrect on x (norm)");
		assertEquals(vx.rotateClockwiseAroundOrigin(45).y(), 4.949747468, precision,
				"vector rotation incorrect on y (norm)");

		assertEquals(vx.rotateClockwiseAroundOrigin(390).x(), 3.330127019, precision,
				"vector rotation incorrect on x (big)");
		assertEquals(vx.rotateClockwiseAroundOrigin(390).y(), 4.232050808, precision,
				"vector rotation incorrect on y (big)");
	}

	@Test
	void testAngleBetweenTwoSprites() {
		// N.b. - the angle is read clockwise from the x-axis centered on the 1st point
		// If the 2nd point lies beneath-right of the 1st point, read from positive x
		// axis
		// If the 2nd point lies beneath-left of the 1st point, read from the negative x
		// axis
		// If the 2nd point lies above-right of the 1st point, read from the negative x
		// axis
		// If the 2nd point lies above-left of the 1st point, read from the positive x
		// axis

		Vector locationCenter = new Vector(0, 0);
		Vector locationBenRight = new Vector(1, -1);
		Vector locationBenLeft = new Vector(-1, -1);
		Vector locationUpRight = new Vector(1, 1);
		Vector locationUpLeft = new Vector(-1, 1);

		Vector vx = new Vector();

		assertEquals(vx.angleBetweenTwoSprites(locationCenter, locationBenRight), 45, precision, "angle incorrect");
		assertEquals(vx.angleBetweenTwoSprites(locationCenter, locationBenLeft), 315, precision, "angle incorrect");
		assertEquals(vx.angleBetweenTwoSprites(locationCenter, locationUpRight), 135, precision, "angle incorrect");
		assertEquals(vx.angleBetweenTwoSprites(locationCenter, locationUpLeft), 225, precision, "angle incorrect");
	}

	@Test
	void testVectorEquals() {
		Vector vx = new Vector(2, 2);
		Vector vy = new Vector(3, 1);

		assertTrue(vx.equals(vx), "Equal vectors seen as unequal");
		assertFalse(vx.equals(vy), "Unequal vectors seen as equal");

	}
}
