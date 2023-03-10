package com.game.graphics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import com.game.collision.Hitbox;
import com.game.collision.Polygon;
import com.game.utilities.Vector;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * The heart of the graphics stuff. This contains the main rendering loop that
 * will run every frame.
 *
 * @author mxb1143
 */
public class Renderer extends AnimationTimer {

	/**
	 * The JavaFX canvas onto which everything gets drawn.
	 */
	private Canvas canvas;
	/**
	 * The JavaFX GraphicsContext of the canvas; this isn't strictly necessary.
	 */
	private GraphicsContext gc;
	/**
	 * The list of all sprites that are to be rendered.
	 */
	private List<Sprite> sprites;
	private long lastTimeHandleCalled;
	private boolean shouldRenderHitboxes;
	private Sprite background;
	private Sprite newBackground;
	private boolean shouldClampSprites = false;
	/**
	 * Sprites in this queue will be de-queued after rendering a frame and the
	 * removed from the rendering list.
	 */
	private Queue<Sprite> toRemove;

	public Renderer(Canvas canvas) {
		this.canvas = canvas;
		this.gc = canvas.getGraphicsContext2D();
		this.sprites = new ArrayList<Sprite>();
		this.lastTimeHandleCalled = System.nanoTime();
		this.shouldRenderHitboxes = false;
		this.background = null;
		this.newBackground = null;
		this.toRemove = new ArrayDeque<>();
	}

	/**
	 * This gets called at a regular interval by JavaFX. This is the main rendering
	 * loop, that clears the screen, draws all sprites (and optionally hitboxes),
	 * and performs other tasks such as updating position based on velocity and
	 * performing collision checks. Finally, this is where sprites are removed from
	 * the rendering list.
	 * 
	 * @param time Is the current timestamp in nanoseconds.
	 */
	@Override
	public void handle(long time) {
		// How long has it been since we last got called?
		long delta = time - lastTimeHandleCalled;
		this.lastTimeHandleCalled = time;

		if (newBackground != null) {
			background.updatePositionWithVelocity(delta);
			newBackground.updatePositionWithVelocity(delta);
			background.draw(gc);
			newBackground.draw(gc);
			newBackground.performScreenEdgeCollisionCheck(canvas.getWidth(), canvas.getHeight());
			return;
		}

		// First, clear the screen.
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		if (background != null) {
			background.draw(gc);
		}
		// Then move and redraw sprites.
		sprites.forEach(sprite -> {
			sprite.updatePositionWithVelocity(delta);
			sprite.performScreenEdgeCollisionCheck(canvas.getWidth(), canvas.getHeight());
			if (shouldClampSprites) {
				sprite.clampPositionToCanvas(new Vector(this.canvas.getWidth(), this.canvas.getHeight()));
			}
			sprite.updateRotationWithVelocity(delta);

			// sprites.forEach(otherSprite -> { if (otherSprite != sprite) {
			// sprite.performCollisionCheck(otherSprite);
			// }});
			sprite.draw(gc);
			if (shouldRenderHitboxes) {
				sprite.drawHitbox(gc);
			}
		});
		while (!toRemove.isEmpty()) {
			sprites.remove(toRemove.remove());
		}

	}

	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}

	public void removeSprite(Sprite sprite) {
		toRemove.add(sprite);
	}

	public void setShouldRenderHitboxes(boolean state) {
		this.shouldRenderHitboxes = state;
	}

	public void setShouldClampSprites(boolean state) {
		this.shouldClampSprites = state;
	}

	/**
	 * Set the background; do not use a transition.
	 */
	public void setBackground(Image background) {
		this.background = new Sprite(new Vector(0.0, 0.0), new Vector(0.0, 0.0),
				new Vector(canvas.getWidth(), canvas.getHeight()), Arrays.asList(background));
	}

	/**
	 * Transition the background to a new background.
	 */
	public void transitionBackground(Image newBackground, TransitionDirection direction, double velocity) {
		SideOfScreen edgeToFinish;
		switch (direction) {
		case UP:
			this.newBackground = new Sprite(new Vector(0.0, canvas.getHeight()), new Vector(0.0, -1.0 * velocity),
					new Vector(canvas.getWidth(), canvas.getHeight()), Arrays.asList(newBackground));
			this.background.setVelocity(new Vector(0.0, -1.0 * velocity));
			edgeToFinish = SideOfScreen.TOP;
			break;
		case DOWN:
			this.newBackground = new Sprite(new Vector(0.0, -1.0 * canvas.getHeight()), new Vector(0.0, velocity),
					new Vector(canvas.getWidth(), canvas.getHeight()), Arrays.asList(newBackground));
			this.background.setVelocity(new Vector(0.0, velocity));
			edgeToFinish = SideOfScreen.BOTTOM;
			break;
		case LEFT:
			this.newBackground = new Sprite(new Vector(canvas.getWidth(), 0.0), new Vector(-1.0 * velocity, 0.0),
					new Vector(canvas.getWidth(), canvas.getHeight()), Arrays.asList(newBackground));
			this.background.setVelocity(new Vector(-1.0 * velocity, 0.0));
			edgeToFinish = SideOfScreen.LEFT;
			break;
		case RIGHT:
			this.newBackground = new Sprite(new Vector(-1.0 * canvas.getWidth(), 0.0), new Vector(velocity, 0.0),
					new Vector(canvas.getWidth(), canvas.getHeight()), Arrays.asList(newBackground));
			this.background.setVelocity(new Vector(velocity, 0.0));
			edgeToFinish = SideOfScreen.RIGHT;
			break;
		default:
			edgeToFinish = SideOfScreen.TOP; // make the compiler happy despite enumerating all options
		}

		double width = canvas.getWidth() / 2;
		double height = canvas.getHeight() / 2;
		this.newBackground.setHitbox(new Hitbox(
				Arrays.asList(new Polygon(Arrays.asList(new Vector(width, height), new Vector(-1.0 * width, height),
						new Vector(-1.0 * width, -1.0 * height), new Vector(width, -1.0 * height))))));

		Renderer renderer = this;
		this.newBackground.registerCallbackOnEdgeCollision(new EdgeCollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, SideOfScreen edge) {
				if (edge == edgeToFinish) {
					renderer.background = renderer.newBackground;
					renderer.newBackground = null;
					renderer.background.setVelocity(new Vector(0.0, 0.0));
					renderer.background.setPosition(new Vector(0.0, 0.0));
				}
			}
		});

	}

	public List<Sprite> getSprites() {
		return sprites;
	}

	public Canvas getCanvas() {
		return canvas;
	}
}
