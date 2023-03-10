package com.game.logic;

import com.game.entities.Player;

public interface InputCommand {
	public abstract void execute(Player player);
}