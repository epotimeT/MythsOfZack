package com.game.logic.events;

public class NewEntityEvent extends NetworkEvent {

	public Integer screenId;
	public EntitySpecifier which;
	public Object extraData;
	public Integer color;
	public String name;

	public NewEntityEvent(Integer screenId, EntitySpecifier which, String name, Integer color) {
		this.screenId = screenId;
		this.which = which;
		this.name = name;
		this.color = color;
	}

	public NewEntityEvent(Integer screenId, EntitySpecifier which, Object extraData, String name, Integer color) {
		this.screenId = screenId;
		this.which = which;
		this.extraData = extraData;
		this.name = name;
		this.color = color;

	}

}
