package com.game.logic.events;

public class WinLoseEvent extends NetworkEvent {

	public boolean didWin;

	public WinLoseEvent(boolean didWin) {
		this.didWin = didWin;
	}

}
