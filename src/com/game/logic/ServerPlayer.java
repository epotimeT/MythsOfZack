package com.game.logic;

import com.game.entities.Player;
import com.game.server.TCPConnection;
import com.game.utilities.Vector;

public class ServerPlayer extends Player {
	public int udpid;
	public TCPConnection tcpConnection;

	public ServerPlayer(int udpid, TCPConnection tcpConnection, Vector position, int screenId, String name,
			int color) {
		super(position, screenId, name, color);

		this.udpid = udpid;
		this.tcpConnection = tcpConnection;
	}
}
