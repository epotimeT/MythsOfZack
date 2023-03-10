package com.game.logic.events;

public class HitEvent extends NetworkEvent {

	public Integer entityId;
	public Integer collision;

	public HitEvent(Integer entityId, Integer collision) {
		this.entityId = entityId;
		this.collision = collision;
	}

}
