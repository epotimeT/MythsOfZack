package com.game.logic.events;

import com.game.utilities.Vector;

public class VelocityUpdateEvent extends NetworkEvent {

	public Integer screenId;
	public Integer entityId;
	public Vector newVelocity;

	public VelocityUpdateEvent(Integer screenId, Integer entityId, Vector newVelocity) {
		this.screenId = screenId;
		this.entityId = entityId;
		this.newVelocity = newVelocity;
	}

}
