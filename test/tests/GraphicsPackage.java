package com.game.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import com.game.collision.Hitbox;
import com.game.collision.Polygon;
import com.game.graphics.Renderer;
import com.game.graphics.Sprite;
import com.game.utilities.Vector;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

/**
 * <h1>GraphicsPackage</h1> Unit tests for the classes contained in the package
 * 'Graphics' Some of these tests require javafx to run
 */
class GraphicsPackage {

	@Rule
	private Timeout graphicsTimeout = new Timeout(200, TimeUnit.MILLISECONDS);

	@Rule
	private double precision = CollisionPackage.precision;

	// CollisionCallback is an interface with no implemented methods

	// CollisionCallbackHandler has no methods that would benefit from unit testing

	// EdgeCollisionCallback is an interface with no implemented methods

	// CollisionCallbackHandler has no methods that would benefit from unit testing

	// Testing Renderer
	// handle() cannot be tested effectively via unit testing

	@Test
	void testAddSprite() {
		Renderer renderer = new Renderer(new Canvas());
		Sprite sprite = new Sprite();
		Sprite sprite2 = new Sprite();

		List<Sprite> expectedSprites = List.of(sprite);
		renderer.addSprite(sprite);
		assertEquals(renderer.getSprites(), expectedSprites, "Sprite isn't added to list correctly");

		List<Sprite> expectedSprites2 = List.of(sprite, sprite2);
		renderer.addSprite(sprite2);
		assertEquals(renderer.getSprites(), expectedSprites2, "Sprite2 isn't added to list correctly");

	}

	// removeSprite(), setShouldRenderHitboxes(), setBackground() and
	// transitionBackground()
	// cannot be tested effectively via unit testing

	@Test
	void testGetCanvas() {
		Canvas canvas = new Canvas();
		Renderer renderer = new Renderer(canvas);

		assertSame(renderer.getCanvas(), canvas, "Incorrect canvas object retrieved");
	}

	// Testing Sprite
	@Test
	void testUpdatePositionWithVelocity() {
		Sprite sprite = new Sprite();
		sprite.setPosition(new Vector(10, 50));
		sprite.setVelocity(new Vector(200, 200));

		sprite.updatePositionWithVelocity(100);

		assertEquals(sprite.position(), new Vector(10.002, 50.002), "Position is updated incorrectly");
	}

	@Test
	void testUpdateRotationWithVelocity() {
		Sprite sprite = new Sprite();
		sprite.setRotationalVelocity(500);

		sprite.updateRotationWithVelocity(100);

		assertEquals(sprite.rotation(), 0.018, "Rotation is updated incorrectly");
	}

	@Test
	void testPosition() {
		Sprite sprite = new Sprite();
		sprite.setPosition(new Vector(75.12, 23.65));
		sprite.setPreferSize(new Vector(4, 4));

		assertEquals(sprite.position(), new Vector(75.12, 23.65), "Position getter/setter incorrect");

		assertEquals(sprite.getPositionCentre(), new Vector(77.12, 25.65), "Position centre incorrect");
	}

	@Test
	void testVelocity() {
		Sprite sprite = new Sprite();
		sprite.setVelocity(new Vector(234.23, 6878.3));

		assertEquals(sprite.velocity(), new Vector(234.23, 6878.3), "Velocity getter/setter incorrect");

		sprite.updateVelX(10.0);

		assertEquals(sprite.velocity(), new Vector(244.23, 6878.3), "Velocity x updater incorrect");

		sprite.updateVelY(15.0);

		assertEquals(sprite.velocity(), new Vector(244.23, 6893.3), "Velocity y updater incorrect");

	}

	@Test
	void testPreferSize() {
		Sprite sprite = new Sprite();
		sprite.setPreferSize(new Vector(15, 23));

		assertEquals(sprite.preferSize(), new Vector(15, 23), "PreferSize getter/setter incorrect");

		sprite.unsetPreferSize();

		assertNull(sprite.preferSize(), "PreferSize does not unset");
	}

	@Test
	void testRotation() {
		Sprite sprite = new Sprite();
		sprite.setRotation(345);

		assertEquals(sprite.rotation(), 345, "Rotation getter/setter incorrect when input <360");

		sprite.setRotation(360);

		assertEquals(sprite.rotation(), 0, "Rotation getter/setter incorrect when input = 360");

		sprite.setRotation(365);

		assertEquals(sprite.rotation(), 5, "Rotation getter/setter incorrect when input >360");

	}

	@Test
	void testRotationalVelocity() {
		Sprite sprite = new Sprite();
		sprite.setRotationalVelocity(3432);

		assertEquals(sprite.rotationalVelocity(), 3432, "RotationalVelocity getter/setter incorrect");
	}

	// setCurrentSprite(), setHitbox(), registerCallbackOnCollision(),
	// unsetCallbackOnCollision(),
	// registerCallbackOnEdgeCollision(), and unsetCallbackOnEdgeCollision() cannot
	// be tested
	// effectively via unit testing

	@Test
	void testGetRenderSize() {
		// This function can only be tested via unit testing with an empty Image list
		List<Image> img = List.of();
		Sprite sprite = new Sprite(new Vector(10, 10), img);
		sprite.setCurrentSprite(0);
		sprite.unsetPreferSize();

		assertEquals(sprite.getRenderSize(), new Vector(20, 20),
				"Default prefer size not returned after IndexOutOfBoundsException (prefsize null)");

		sprite.setPreferSize(new Vector(10, 10));

		assertEquals(sprite.getRenderSize(), new Vector(10, 10),
				"Prefer size not returned after IndexOutOfBoundsException (prefsize not null)");
	}

	// draw(), drawWithSprite(), drawPlaceholder() and drawHitbox() cannot be tested
	// effectively
	// via unit testing

	@Test
	void testClampPositionToCanvas() {
		Sprite sprite = new Sprite(new Vector(-4, -5));
		sprite.setPreferSize(new Vector(5, 5));

		Sprite sprite2 = new Sprite(new Vector(2, 7));
		sprite2.setPreferSize(new Vector(5, 5));

		sprite.clampPositionToCanvas(new Vector(3, 4));

		assertEquals(sprite.position(), new Vector(-2, -1), "Negative vector clamps incorrectly to canvas");

		sprite2.clampPositionToCanvas(new Vector(8, 9));

		assertEquals(sprite2.position(), new Vector(2, 4), "Positive vector clamps incorrectly to canvas");
	}

	// performCollisionCheck() cannot be tested effectively via unit testing

	@Test
	void testInhibitHitboxFor() {
		Sprite sprite = new Sprite();
		List<Vector> vectorList = List.of(new Vector(1, 1), new Vector(1, 2), new Vector(2, 2));
		List<Polygon> polygonList = List.of(new Polygon(vectorList));
		Hitbox box = new Hitbox(polygonList);
		sprite.setHitbox(box);

		sprite.inhibitHitboxFor(20000000);
		assertTrue(box.isInhibited(), "Hitbox is not inhibited when it should be");

		sprite.inhibitHitboxFor(10);
		assertFalse(box.isInhibited(), "Hitbox is inhibited when it shouldn't be");
	}

	// performScreenEdgeCollisionCheck() cannot be tested effectively via unit
	// testing

	@Test
	void testSetName() {
		Sprite sprite = new Sprite();

		sprite.setName("spriteName");

		assertEquals(sprite.name, "spriteName", "Name does not match what was set");
	}

	@Test
	void testSetCount() {
		Sprite sprite = new Sprite();

		sprite.setCount("spriteCount");

		assertEquals(sprite.count, "spriteCount", "Count does not match what was set");
	}

	@Test
	void testSetDescription() {
		Sprite sprite = new Sprite();

		sprite.setDescription("spriteDescription");

		assertEquals(sprite.description, "spriteDescription", "Description does not match what was set");
	}

	// setCountColour() cannot be tested effectively via unit testing
}
