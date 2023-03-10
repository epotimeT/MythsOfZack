package com.game.entities;

import com.game.graphics.Sprite;
import com.game.utilities.Vector;

public interface EntityCommonStats {
	public double getHealth();

	public double getAtkDmg();

	public double getMgcDmg();

	public Vector getVelocity();

	public double getTopSpeed();

	public Sprite getSprite();
}
