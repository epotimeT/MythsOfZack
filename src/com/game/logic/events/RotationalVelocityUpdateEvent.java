package com.game.logic.events;

public class RotationalVelocityUpdateEvent extends NetworkEvent {

	public Integer screenId;
	public Integer entityId;
	public double newRotationalVelocity;

	public RotationalVelocityUpdateEvent(Integer screenId, Integer entityId, double newRotationalVelocity) {
		this.screenId = screenId;
		this.entityId = entityId;
		this.newRotationalVelocity = newRotationalVelocity;
	}

}
