package com.game.logic.events;

public class SoundEvent extends NetworkEvent {

	public Integer entityId;
	public Integer soundId;

	public SoundEvent(Integer entityId, Integer soundId) {
		this.entityId = entityId;
		this.soundId = soundId;
	}
}