package com.game.logic.events;

public class RotationUpdateEvent extends NetworkEvent {

	public Integer screenId;
	public Integer entityId;
	public double newRotation;

	public RotationUpdateEvent(Integer screenId, Integer entityId, double newRotation) {
		this.screenId = screenId;
		this.entityId = entityId;
		this.newRotation = newRotation;
	}

}
