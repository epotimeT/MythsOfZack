package com.game.logic.events;

public class PlayerAssignmentEvent extends NetworkEvent {

	public Integer screenId;
	public Integer entityId;

	public PlayerAssignmentEvent(Integer screenId, Integer entityId) {
		this.screenId = screenId;
		this.entityId = entityId;
	}

}
