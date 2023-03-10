package com.game.logic.events;

public class DeathEvent extends NetworkEvent {

	public Integer entityId;

	public DeathEvent(Integer entityId) {
		this.entityId = entityId;
	}

}
