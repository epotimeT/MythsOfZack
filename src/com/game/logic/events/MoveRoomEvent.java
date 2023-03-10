package com.game.logic.events;

public class MoveRoomEvent extends NetworkEvent {

	public Integer entityId;
	public int newScreen;

	public MoveRoomEvent(Integer entityId, int newScreen) {
		this.entityId = entityId;
		this.newScreen = newScreen;
	}

}
