package com.game.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import com.game.collision.Hitbox;
import com.game.collision.Line;
import com.game.collision.Polygon;
import com.game.collision.WorldPolygon;
import com.game.utilities.Vector;

/**
 * <h1>CollisionPackage</h1> Unit tests for the classes contained in the package
 * 'Collision' Some of these tests require javafx to run
 */
class CollisionPackage {
	@Rule
	private Timeout collisionTimeout = new Timeout(200, TimeUnit.MILLISECONDS);

	// This is the precision value for ALL test classes
	@Rule
	static final double precision = 1e-5;

	// Testing Hitbox
	@Test
	void testIsCollidingWith() { // TODO - how does this method work?
		List<Vector> vectorList = List.of(new Vector(1, 1), new Vector(1, 2), new Vector(2, 2));
		List<Polygon> polygonList = List.of(new Polygon(vectorList));
		Hitbox boxOne = new Hitbox(polygonList);

		List<Vector> vectorList2 = List.of(new Vector(2, 0.5), new Vector(1.25, 1.5), new Vector(0.5, 0.5));
		List<Polygon> polygonList2 = List.of(new Polygon(vectorList2));
		Hitbox boxTwo = new Hitbox(polygonList2);

		List<Vector> vectorList3 = List.of(new Vector(5, 5), new Vector(5, 7), new Vector(7, 8));
		List<Polygon> polygonList3 = List.of(new Polygon(vectorList3));
		Hitbox boxThree = new Hitbox(polygonList3);

		// For simplicity it is assumed in the follow methods that all hitboxes are in
		// the same
		// relative world space

		// These hitboxes were found to intersect visually using graphing paper
		assertTrue(boxOne.isCollidingWith(boxTwo, new Vector(1, 1), 0, new Vector(1, 1), 0),
				"The hitboxes were found to not intersect when they do");

		// These hitboxes were found to not intersect visually using graphing paper
		assertFalse(boxOne.isCollidingWith(boxThree, new Vector(1, 1), 0, new Vector(1, 1), 0),
				"The hitboxes were found to intersect when they don't");
	}

	@Test
	void testIsLessThanXAlignedAxis() {
		List<Vector> vectorList = List.of(new Vector(1, 1), new Vector(1, 2), new Vector(2, 2));
		List<Polygon> polygonList = List.of(new Polygon(vectorList));
		Hitbox boxOne = new Hitbox(polygonList);

		assertTrue(boxOne.isLessThanXAlignedAxis(2, new Vector(0, 0), 0));
		assertFalse(boxOne.isLessThanXAlignedAxis(1, new Vector(0, 0), 0));
	}

	@Test
	void testIsGreaterThanXAlignedAxis() {
		List<Vector> vectorList = List.of(new Vector(1, 1), new Vector(1, 2), new Vector(2, 2));
		List<Polygon> polygonList = List.of(new Polygon(vectorList));
		Hitbox boxOne = new Hitbox(polygonList);

		assertTrue(boxOne.isGreaterThanXAlignedAxis(1, new Vector(0, 0), 0));
		assertFalse(boxOne.isGreaterThanXAlignedAxis(2, new Vector(0, 0), 0));
	}

	@Test
	void testIsLessThanYAlignedAxis() {
		List<Vector> vectorList = List.of(new Vector(1, 1), new Vector(1, 2), new Vector(2, 2));
		List<Polygon> polygonList = List.of(new Polygon(vectorList));
		Hitbox boxOne = new Hitbox(polygonList);

		assertTrue(boxOne.isLessThanYAlignedAxis(2, new Vector(0, 0), 0));
		assertFalse(boxOne.isLessThanYAlignedAxis(1, new Vector(0, 0), 0));
	}

	@Test
	void testIsGreaterThanYAlignedAxis() {
		List<Vector> vectorList = List.of(new Vector(1, 1), new Vector(1, 2), new Vector(2, 2));
		List<Polygon> polygonList = List.of(new Polygon(vectorList));
		Hitbox boxOne = new Hitbox(polygonList);

		assertTrue(boxOne.isGreaterThanYAlignedAxis(1, new Vector(0, 0), 0));
		assertFalse(boxOne.isGreaterThanYAlignedAxis(2, new Vector(0, 0), 0));
	}

	// draw() is unable to be tested effectively via unit testing

	@Test
	void testInhibitFunctions() {
		List<Vector> vectorList = List.of(new Vector(1, 1), new Vector(1, 2), new Vector(2, 2));
		List<Polygon> polygonList = List.of(new Polygon(vectorList));
		Hitbox boxOne = new Hitbox(polygonList);

		boxOne.inhibitFor(100000000);
		assertTrue(boxOne.isInhibited(), "The hitbox should still be inhibited");

		boxOne.inhibitFor(10);
		assertFalse(boxOne.isInhibited(), "The hitbox should not be inhibited");

	}

	// Testing Line
	@Test
	void testAngleSubtendedBy() {
		Vector vx = new Vector(2, 2);
		Vector vy = new Vector(2, 0);
		Vector vz = new Vector(4, 2);

		Line lxy = new Line(vx, vy);

		// The expected angle here was found visually using graphing paper
		assertEquals(lxy.angleSubtendedBy(vz), Math.toRadians(45), precision, "Simple angle is incorrect");

		Vector vw = new Vector(3.75, -1.25);

		// The expected angle here was calculated manually using the law of cosines
		assertEquals(lxy.angleSubtendedBy(vw), Math.toRadians(26.16156644), precision, "Complex angle is incorrect");
	}

	@Test
	void testLineEquals() {
		Vector vx = new Vector(2, 2);
		Vector vy = new Vector(2, 0);
		Vector vz = new Vector(4, 6);

		Line lxy = new Line(vx, vy);
		Line lxz = new Line(vx, vz);

		assertTrue(lxy.equals(lxy), "Equal lines are not considered equal");
		assertFalse(lxy.equals(lxz), "Unequal lines are considered equal");
	}

	// Testing Polygon
	@Test
	void testPolygonSizes() {
		Vector vx = new Vector(2, 2);
		List<Vector> vectorList0 = List.of();
		List<Vector> vectorList1 = List.of(vx);
		List<Vector> vectorList2 = List.of(vx, vx);
		List<Vector> vectorList3 = List.of(vx, vx, vx);
		List<Vector> vectorList4 = List.of(vx, vx, vx, vx);

		// The largest polygon we use is 4 points

		assertNotNull(new Polygon(vectorList0), "Polygon fails when made with 0 vectors");
		assertNotNull(new Polygon(vectorList1), "Polygon fails when made with 1 vector");
		assertNotNull(new Polygon(vectorList2), "Polygon fails when made with 2 vectors");
		assertNotNull(new Polygon(vectorList3), "Polygon fails when made with 3 vectors");
		assertNotNull(new Polygon(vectorList4), "Polygon fails when made with 4 vectors");
	}

	@Test
	void testTranslateToWorldSpace() {
		Vector vw = new Vector(1, 1);
		Vector vx = new Vector(1, 2);
		Vector vy = new Vector(2, 2);
		Vector vz = new Vector(2, 1);
		List<Vector> vectorList = List.of(vw, vx, vy, vz);

		Polygon pwxyz = new Polygon(vectorList);
		WorldPolygon wp = new WorldPolygon(vectorList);

		assertEquals(pwxyz.translateToWorldSpace(new Vector(0, 0), 0), wp,
				"The polygon is not translated correctly when already ok");

		Vector va = new Vector(2, 2);
		Vector vb = new Vector(2, 3);
		Vector vc = new Vector(3, 3);
		Vector vd = new Vector(3, 2);

		List<Vector> vectorList2 = List.of(va, vb, vc, vd);
		WorldPolygon wp2 = new WorldPolygon(vectorList2);

		// The expected worldpolygon was translated manually using graphing paper
		assertEquals(pwxyz.translateToWorldSpace(new Vector(1, 1), 0), wp2,
				"The polygon is not translated correctly when offset");

		// Due to tiny rounding errors when rotating, here we will compare the
		// individual points
		// of the rotated polygon to the expected polygon, rather than using the equals
		// method
		Vector vp = new Vector(-1, 1);
		Vector vq = new Vector(-2, 1);
		Vector vr = new Vector(-2, 2);
		Vector vs = new Vector(-1, 2);
		List<Vector> vectorList3 = List.of(vp, vq, vr, vs);

		WorldPolygon wp3 = new WorldPolygon(vectorList3);
		List<Line> wp3Lines = wp3.toLines();

		// n.b - it is rotated anti-clockwise
		WorldPolygon wp4 = pwxyz.translateToWorldSpace(new Vector(0, 0), 90);
		List<Line> wp4Lines = wp4.toLines();

		// The expected worldpolygon was translated manually using graphing paper
		// It is assumed that is the startOfSegments are all correct, so should the
		// endOfSegments
		assertEquals(wp3Lines.get(0).startOfSegment.x(), wp4Lines.get(0).startOfSegment.x(), precision,
				"One of the polygon points is rotated incorrectly");
		assertEquals(wp3Lines.get(0).startOfSegment.y(), wp4Lines.get(0).startOfSegment.y(), precision,
				"One of the polygon points is rotated incorrectly");

		assertEquals(wp3Lines.get(1).startOfSegment.x(), wp4Lines.get(1).startOfSegment.x(), precision,
				"One of the polygon points is rotated incorrectly");
		assertEquals(wp3Lines.get(1).startOfSegment.y(), wp4Lines.get(1).startOfSegment.y(), precision,
				"One of the polygon points is rotated incorrectly");

		assertEquals(wp3Lines.get(2).startOfSegment.x(), wp4Lines.get(2).startOfSegment.x(), precision,
				"One of the polygon points is rotated incorrectly");
		assertEquals(wp3Lines.get(2).startOfSegment.y(), wp4Lines.get(2).startOfSegment.y(), precision,
				"One of the polygon points is rotated incorrectly");

		assertEquals(wp3Lines.get(3).startOfSegment.x(), wp4Lines.get(3).startOfSegment.x(), precision,
				"One of the polygon points is rotated incorrectly");
		assertEquals(wp3Lines.get(3).startOfSegment.y(), wp4Lines.get(3).startOfSegment.y(), precision,
				"One of the polygon points is rotated incorrectly");
	}

	@Test
	void testToLines() {
		Vector vw = new Vector(1, 1);
		Vector vx = new Vector(1, 2);
		Vector vy = new Vector(2, 2);
		Vector vz = new Vector(2, 1);
		List<Vector> vectorList = List.of(vw, vx, vy, vz);
		Polygon pwxyz = new Polygon(vectorList);
		List<Line> pwxyzLines = pwxyz.toLines();

		Line lwx = new Line(vw, vx);
		Line lxy = new Line(vx, vy);
		Line lyz = new Line(vy, vz);
		Line lzw = new Line(vz, vw);
		List<Line> lineList = List.of(lwx, lxy, lyz, lzw);

		assertEquals(pwxyzLines.size(), lineList.size(), "Not enough lines produced");

		assertTrue(Objects.equals(lineList, pwxyzLines), "At least one polygon line is incorrect");
	}

	// draw() is unable to be tested effectively via unit testing

	// Testing WorldPolygon
	@Test
	void testIntersectsWith() {
		Vector vx = new Vector(1, 1);
		Vector vy = new Vector(1, 2);
		Vector vz = new Vector(2, 2);
		List<Vector> vectorList = List.of(vx, vy, vz);
		WorldPolygon pxyz = new WorldPolygon(vectorList);

		Vector vp = new Vector(2, 0.5);
		Vector vq = new Vector(1.25, 1.5);
		Vector vr = new Vector(0.5, 0.5);
		List<Vector> vectorList2 = List.of(vp, vq, vr);
		WorldPolygon ppqr = new WorldPolygon(vectorList2);

		Vector va = new Vector(5, 5);
		Vector vb = new Vector(5, 7);
		Vector vc = new Vector(7, 8);
		List<Vector> vectorList3 = List.of(va, vb, vc);
		WorldPolygon pabc = new WorldPolygon(vectorList3);

		// These polygons were found to intersect visually using graphing paper
		assertTrue(pxyz.intersectsWith(ppqr), "Polygons were found to not intersect when they do");

		// These polygons were found to not intersect visually using graphing paper
		assertFalse(pxyz.intersectsWith(pabc), "Polygons were found to intersect when they don't");

	}

	@Test
	void testWorldPolygonEquals() {
		Vector vx = new Vector(1, 1);
		Vector vy = new Vector(1, 2);
		Vector vz = new Vector(2, 2);
		List<Vector> vectorList = List.of(vx, vy, vz);
		WorldPolygon pxyz = new WorldPolygon(vectorList);

		Vector vp = new Vector(2, 0.5);
		Vector vq = new Vector(1.25, 1.5);
		Vector vr = new Vector(0.5, 0.5);
		List<Vector> vectorList2 = List.of(vp, vq, vr);
		WorldPolygon ppqr = new WorldPolygon(vectorList2);

		assertTrue(pxyz.equals(pxyz), "Equal world polygons are not considered equal");
		assertFalse(pxyz.equals(ppqr), "Unequal world polygons are considered equal");
	}
}
