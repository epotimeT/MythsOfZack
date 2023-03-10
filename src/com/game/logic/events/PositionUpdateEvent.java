package com.game.logic.events;

import com.game.utilities.Vector;

public class PositionUpdateEvent extends NetworkEvent {

	public Integer screenId;
	public Integer entityId;
	public Vector newVelocity;

	public PositionUpdateEvent(Integer screenId, Integer entityId, Vector newVelocity) {
		this.screenId = screenId;
		this.entityId = entityId;
		this.newVelocity = newVelocity;
	}

}
