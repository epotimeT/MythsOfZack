package com.game.graphics;

import java.util.ArrayList;
import java.util.List;

import com.game.collision.Hitbox;
import com.game.utilities.Vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Represents a single on screen sprite.
 * 
 * @link com.game.graphics.Renderer
 *
 * @author mxb1143
 */
public class Sprite {

	/**
	 * Fallback for the size of a sprite should a placeholder be needed and
	 * preferSize is not set.
	 */
	private static final double PLACEHOLDER_WIDTH = 20;
	/**
	 * Fallback for the size of a sprite should a placeholder be needed and
	 * preferSize is not set.
	 */
	private static final double PLACEHOLDER_HEIGHT = 20;

	private Vector position;
	private Vector velocity;
	private double rotation;
	private double rotationalVelocity;
	/**
	 * An optional preferred rendering size.
	 * 
	 * This will transform the image to fit. If unwanted, set to null.
	 */
	private Vector preferSize;
	/**
	 * A list of all images to use; these can be swapped out as needed.
	 */
	private List<Image> sprites;
	/**
	 * Which of the `sprites` to use.
	 */
	private int currentSprite;
	private Hitbox hitbox;
	private CollisionCallback callbackOnCollision;
	private EdgeCollisionCallback callbackOnScreenEdgeCollision;
	public String name;
	public String count;
	public String description;
	Color countcolor = Color.WHITE;

	public Sprite() {
		this.position = new Vector(0, 0);
		this.velocity = new Vector(0, 0);
		this.sprites = new ArrayList<Image>();
	}

	public Sprite(Vector position) {
		this.position = position;
		this.velocity = new Vector(0, 0);
		this.sprites = new ArrayList<Image>();
	}

	public Sprite(Vector position, List<Image> sprites) {
		this.position = position;
		this.velocity = new Vector(0, 0);
		this.sprites = sprites;
	}

	public Sprite(Vector position, Vector velocity, List<Image> sprites) {
		this.position = position;
		this.velocity = velocity;
		this.sprites = sprites;
	}

	// If you would prefer to scale your sprite, you can specify the final size
	// here.
	public Sprite(Vector position, Vector velocity, Vector preferSize, List<Image> sprites) {
		this.position = position;
		this.velocity = velocity;
		this.preferSize = preferSize;
		this.sprites = sprites;
	}

	public Sprite(Vector position, Vector velocity, double rotation, List<Image> sprites) {
		this.position = position;
		this.velocity = velocity;
		this.rotation = rotation;
		this.sprites = sprites;
	}

	public Sprite(Vector position, Vector velocity, double rotation, double rotationalVelocity, List<Image> sprites) {
		this.position = position;
		this.velocity = velocity;
		this.rotation = rotation;
		this.rotationalVelocity = rotationalVelocity;
		this.sprites = sprites;
	}

	public Sprite(Vector position, Vector velocity, double rotation, double rotationalVelocity, Vector preferSize,
			List<Image> sprites) {
		this.position = position;
		this.velocity = velocity;
		this.rotation = rotation;
		this.rotationalVelocity = rotationalVelocity;
		this.preferSize = preferSize;
		this.sprites = sprites;
	}

	/**
	 * Update `position` according to the amount of time that has passed and the
	 * current velocity.
	 *
	 * @param delta The time that has passed, in nanoseconds.
	 */
	public void updatePositionWithVelocity(long delta) {
		position = position.add(velocity.scale(delta / 10000000.0)); // move 100 px/sec
	}

	/**
	 * Update `rotation` according to the amount of time that has passed and the
	 * current rotationalVelocity.
	 *
	 * @param delta The time that has passed, in nanoseconds.
	 */
	public void updateRotationWithVelocity(long delta) {
		rotation += rotationalVelocity * (delta / 1000000000.0) * 360.0; // move one rotation/sec
		rotation = rotation % 360;
	}

	public Vector position() {
		return position;
	}

	public Vector velocity() {
		return velocity;
	}

	public Vector preferSize() {
		return preferSize;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation % 360;
	}

	public double rotation() {
		return rotation;
	}

	public double rotationalVelocity() {
		return rotationalVelocity;
	}

	public void setRotationalVelocity(double velocity) {
		this.rotationalVelocity = velocity;
	}

	public void setPreferSize(Vector preferSize) {
		this.preferSize = preferSize;
	}

	public void unsetPreferSize() {
		this.preferSize = null;
	}

	public void setCurrentSprite(int spriteId) {
		this.currentSprite = spriteId;
	}

	public void setHitbox(Hitbox hitbox) {
		this.hitbox = hitbox;
	}

	public void registerCallbackOnCollision(CollisionCallback callback) {
		this.callbackOnCollision = callback;
	}

	public void unsetCallbackOnCollision() {
		this.callbackOnCollision = null;
	}

	public void registerCallbackOnEdgeCollision(EdgeCollisionCallback callback) {
		this.callbackOnScreenEdgeCollision = callback;
	}

	public void unsetCallbackOnEdgeCollision() {
		this.callbackOnScreenEdgeCollision = null;
	}

	/**
	 * Calculates the actual size of the sprite that will be rendered, considering
	 * the current image and preferred size.
	 *
	 * @return The actual final size of this sprite that will be rendered.
	 */
	public Vector getRenderSize() {
		try {
			if (preferSize == null) {
				Image sprite = sprites.get(currentSprite);
				return new Vector(sprite.getWidth(), sprite.getHeight());
			} else {
				return preferSize;
			}

		} catch (IndexOutOfBoundsException e) {
			if (preferSize == null) {
				return new Vector(PLACEHOLDER_WIDTH, PLACEHOLDER_HEIGHT);
			} else {
				return preferSize;
			}
		}
	}

	public void draw(GraphicsContext gc) {
		gc.save();
		Vector renderSize = getRenderSize();
		gc.translate(position.x() + (renderSize.x() / 2), position.y() + (renderSize.y() / 2));
		gc.rotate(rotation);
		Vector offset;
		offset = renderSize.scale(-0.5);
		try {
			drawWithSprite(gc, sprites.get(currentSprite), offset);
		} catch (IndexOutOfBoundsException e) {
			drawPlaceholder(gc, offset);
		}
		gc.restore();
	}

	private void drawWithSprite(GraphicsContext gc, Image sprite, Vector offset) {
		Vector size = getRenderSize();
		if (name != null) {
			gc.setFill(Color.BLACK);
			gc.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
			gc.fillText(name, offset.x() + (size.x() / 3), offset.y() - 10);
		}
		if (count != null) {
			gc.setFill(countcolor);
			gc.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
			gc.fillText(count, offset.x() - (count.length()), offset.y() - 10);
		}
		if (description != null) {
			gc.setFill(Color.WHITE);
			gc.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
			gc.fillText(description, offset.x() - (description.length() * 8.5), offset.y() + (size.y() / 2));
		}
		gc.drawImage(sprite, offset.x(), offset.y(), size.x(), size.y());
	}

	private void drawPlaceholder(GraphicsContext gc, Vector offset) {
		Vector size = getRenderSize();
		gc.fillRect(offset.x(), offset.y(), size.x(), size.y());
	}

	public void drawHitbox(GraphicsContext gc) {
		if (hitbox != null) {
			hitbox.draw(gc, convertWorldPositionToHitboxPosition(), this.rotation);
		}
	}

	public void clampPositionToCanvas(Vector screenSize) {
		this.position = this.position.makeComponentsZeroIfNegative();
		this.position = this.position.clampComponentsToVector(
				new Vector(screenSize.x() - getRenderSize().x(), screenSize.y() - getRenderSize().y()));
	}

	public void performCollisionCheck(Sprite other) {
		if (this.callbackOnCollision != null && this.hitbox != null && other.hitbox != null
				&& this.hitbox.isCollidingWith(other.hitbox, convertWorldPositionToHitboxPosition(),
						this.rotation, other.convertWorldPositionToHitboxPosition(), other.rotation)) {
			this.callbackOnCollision.onCollision(this, other);
		}
	}

	/**
	 * A small shim that reconciles the fact that sprite coordinated refer to the
	 * top left of the sprite but hitbox coordinates the centre.
	 */
	private Vector convertWorldPositionToHitboxPosition() {
		return position.add(getRenderSize().scale(0.5));
	}

	/**
	 * @link com.game.collision.Hitbox
	 */
	public void inhibitHitboxFor(long nanoseconds) {
		hitbox.inhibitFor(nanoseconds);
	}

	/**
	 * Checks if any part of the hitbox is outside of screen bounds, and calls the
	 * relevant callback.
	 *
	 * @param width  The screen width.
	 * @param height The screen height.
	 */
	public void performScreenEdgeCollisionCheck(double width, double height) {
		if (hitbox == null) {
			return;
		}
		if (callbackOnScreenEdgeCollision == null) {
			return;
		}
		if (hitbox.isLessThanXAlignedAxis(0, this.convertWorldPositionToHitboxPosition(), this.rotation)) {
			callbackOnScreenEdgeCollision.onCollision(this, SideOfScreen.LEFT);
		}
		if (hitbox.isGreaterThanXAlignedAxis(width, this.convertWorldPositionToHitboxPosition(),
				this.rotation)) {
			callbackOnScreenEdgeCollision.onCollision(this, SideOfScreen.RIGHT);
		}
		if (hitbox.isLessThanYAlignedAxis(0, this.convertWorldPositionToHitboxPosition(), this.rotation)) {
			callbackOnScreenEdgeCollision.onCollision(this, SideOfScreen.TOP);
		}
		if (hitbox.isGreaterThanYAlignedAxis(height, this.convertWorldPositionToHitboxPosition(),
				this.rotation)) {
			callbackOnScreenEdgeCollision.onCollision(this, SideOfScreen.BOTTOM);
		}
	}

	public void updateVelX(Double x) {
		velocity = velocity.updateX(x);
	}

	public void updateVelY(Double y) {
		velocity = velocity.updateY(y);
	}

	public Vector getPositionCentre() {
		return position.add(getRenderSize().scale(0.5));
	}

	public void setName(String text) {
		name = text;
	}

	public void setCount(String text) {
		count = text;
	}

	public void setDescription(String text) {
		description = text;
	}

	public void setCountColor(Color color) {
		countcolor = color;
	}

	public String getCount() {
		return count;
	}

	public String getDescription() {
		return description;
	}
}
