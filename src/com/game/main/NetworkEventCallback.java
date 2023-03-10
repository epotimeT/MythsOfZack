package com.game.main;

import com.game.logic.events.NetworkEvent;

public interface NetworkEventCallback {
	public void onEvent(NetworkEvent event);
}
