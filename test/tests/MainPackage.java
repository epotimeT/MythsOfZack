package com.game.tests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import com.game.main.Client;
import com.game.main.ClientUDP;

/**
 * <h1>MainPackage</h1> Unit tests for the classes contained in the package
 * 'Main' Some of these tests require javafx to run
 */
class MainPackage {

	@Rule
	private Timeout mainTimeout = new Timeout(200, TimeUnit.MILLISECONDS);

	// Testing Client

	// connectClient(), disconnectClient(), sendByteArray(), getRunning(),
	// shutdownHook() and
	// run() cannot be tested effectively via unit testing (require a network
	// connection)

	@Test
	public void testClientName() {
		Client client = new Client(6868, "client", null);

		assertEquals(client.getName(), "client", "retreived name incorrect");
	}

	@Test
	public void testAllClientsNames() {
		Client client = new Client(6868, "client0", null);

		client.setClientName(0);
		client.setClientName(3, "client3");
		String[] expectedArray = new String[] { "client0", null, null, "client3" };
		assertArrayEquals(client.getClientNames(), expectedArray, "two clients array incorrect");

		client.setClientName(2, "client2");
		client.setClientName(1, "client1");
		expectedArray[1] = "client1";
		expectedArray[2] = "client2";
		assertArrayEquals(client.getClientNames(), expectedArray, "full clients array incorrect");

		client.setClientName(1, null);
		assertNull(client.getClientNames()[1], "replacing clients array value incorrect");
	}

	@Test
	public void testMessages() {
		Client client = new Client(6868, "client", null);

		client.setMessages("msg1");
		client.setMessages("msg2");
		client.setMessages("msg3");

		Stack<String> expectedStack = new Stack<String>();
		expectedStack.push("msg3");
		expectedStack.push("msg2");
		expectedStack.push("msg1");

		assertEquals(client.getMessages(), expectedStack, "messages stack incorrect");
	}

	// setEventCallback() and unsetEventCallback() cannot be tested effectively via
	// unit testing

	// Testing ClientUDP

	// closeSocket(), sendPacket(), run() and getRunning() cannot be tested
	// effectively
	// via unit testing (require a network connection)

	@Test
	public void testId() {
		ClientUDP clientUdp = new ClientUDP("localhost", 6868, null);

		assertEquals(clientUdp.getID(), 0, "clientID reads incorrectly at start");

		clientUdp.setID(3);
		assertEquals(clientUdp.getID(), 3, "clientID reads incorrectly after change");
	}

	// EventListener has no methods that would benefit from unit testing

	// NetworkEventCallback is an interface that cannot be tested with unit testing

	// NetworkEventHandler has no methods that would benefit from unit testing

	// Semaphore has no methods that would benefit from unit testing

}
