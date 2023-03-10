package com.game.graphics;

/**
 * A simple interface that any consumer of collision events should use to
 * recieve a callback.
 */
public interface CollisionCallback {
	public void onCollision(Sprite thisSprite, Sprite otherSprite);
}
