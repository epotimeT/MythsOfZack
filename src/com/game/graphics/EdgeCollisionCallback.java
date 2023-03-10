package com.game.graphics;

/**
 * A simple interface that any consumer of collision events for the screen edges
 * should use to recieve a callback.
 */
public interface EdgeCollisionCallback {
	public void onCollision(Sprite thisSprite, SideOfScreen edge);
}
