package com.game.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import com.game.server.Lobby;
import com.game.server.Server;

/**
 * <h1>ServerPackage</h1> Unit tests for the classes contained in the package
 * 'Server'
 */
class ServerPackage {
	@Rule
	private Timeout serverTimeout = new Timeout(1000, TimeUnit.MILLISECONDS);

	// Connection has no methods that would benefit from unit testing

	// EventDecipherer has no methods that would benefit from unit testing

	// Testing Lobby
	@Test
	public void testLobbyObjectState() {
		// Create lobby object
		Server server = new Server(6868);
		Lobby lobby = new Lobby(server);

		// Check values are correct via get cmds
		assertEquals(lobby.getTotalClients(), 0, "ClientsConnected starts incorrectly");

		// Clean-up
		server.shutdownServer();
	}

	@Test
	public void testLobbyClient() {
		// Create lobby object
		Server server = new Server(6868);
		Lobby lobby = new Lobby(server);

		// Test adding clients
		lobby.addClient("client0", 0);
		assertEquals(lobby.getTotalClients(), 1, "Client0 is not added");

		lobby.addClient("client1", 1);
		lobby.addClient("client2", 2);
		assertEquals(lobby.getTotalClients(), 3, "Client1 and/or client2 is not added");

		String[] testNames = new String[] { "client0", "client1", "client2", null };
		assertArrayEquals(lobby.getClientNames(), testNames, "The names list is incorrect");

		// Test removing clients
		lobby.removeClient(1);
		assertEquals(lobby.getTotalClients(), 2, "Client1 was not removed");

		testNames[1] = null;
		assertArrayEquals(lobby.getClientNames(), testNames, "The names list is incorrect");

		lobby.removeClient(2);
		lobby.removeClient(0);
		assertEquals(lobby.getTotalClients(), 0, "Client1 and/or client2 not removed");

		testNames[0] = null;
		testNames[2] = null;
		assertArrayEquals(lobby.getClientNames(), testNames, "The names list is incorrect");

		// Clean-up
		server.shutdownServer();

	}

	// Testing Server
	@Test
	public void testServerObjectState() {
		// Create server object
		Server server = new Server(6868);

		// Check start values via get cmds
		assertEquals(server.getIdCount(), 0, "IdCount starts incorrectly");
		assertArrayEquals(server.getIdList(), new int[] { 0, 0, 0, 0 }, "IdList starts incorrectly");
		assertEquals(server.getServerUDPConnected(), false, "UDPConnected starts incorrectly");

		// Clean-up
		server.shutdownServer();
	}

	// Server has no other methods that would benefit from unit testing

	// Semaphore has no methods that would benefit from unit testing

	// TCPConnection has no methods that would benefit from unit testing

	// UDPConnection has no methods that would benefit from unit testing

}
