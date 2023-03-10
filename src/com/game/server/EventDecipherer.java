package com.game.server;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.game.serverGame.GameLoop;
import com.game.utilities.Semaphore;
import com.game.utilities.Vector;

/**
 * <h1>EventDecipherer</h1> This class contains the functions related to
 * deciding what actions to execute based on the packet received from the
 * client(s)
 */
public class EventDecipherer {

	private Server server;
	private GameLoop gameLoop;
	private Semaphore semReceivedName;

	private boolean withGui;
	private int badPackets = 0;
	private int startNum = 0; // holds number of players who have clicked start

	/**
	 * Creates an EventDecipherer object
	 * 
	 * @param server  - reference to the Server object
	 * @param sem1    - reference to the Semaphore used to check a name is received
	 * @param withGui - boolean of whether the ServerGUI is being used
	 */
	public EventDecipherer(Server server, Semaphore sem1, Boolean withGui) {
		this.withGui = withGui;
		this.server = server;
		this.semReceivedName = sem1;
	}

	/**
	 * This method responds to the TCP packets received from a connection
	 * 
	 * @param id       - int - the id of the client it received data from
	 * @param actionId - int - the id of what action to perform
	 * @param packet   - byte[] - any additional data received
	 */
	public void received(int id, int actionId, byte[] packet) {
		String packetStr = new String(packet, StandardCharsets.UTF_8);

		System.out.println("Connection " + id + " received data via TCP w/ actionId " + actionId);

		// LOBBY COMMANDS
		if (actionId == 1) { // Receive name from client
			server.setClientName(id, packetStr);
			System.out.println("received name1");
			semReceivedName.take();
			System.out.println("received name2");
			badPackets = 0;

		} else if (actionId == 2) { // Client is disconnecting
			server.removeClient(id);
			badPackets = 0;
		} else if (actionId == 5) { // Receive chat message from client
			// Forward to message to all clients (except one it got it from)
			server.fwdBroadcastExcClientTCPData(id, 5, packetStr);
			badPackets = 0;
		}
		// GAME COMMANDS
		else if (actionId == 6) { // Client has initialised
			gameLoop.updateClientInitialisedCount();
		} else if (actionId == 7) { // Client is attacking
			server.fwdBroadcastExcClientTCPData(id, 11, packetStr);
			badPackets = 0;
		} else if (actionId == 8) { // Client collided? (May be done on server?)

		} else if (actionId == 9) { // Client hit? (May be done on server)

		} else if (actionId == 10) { // Client dead? (May be done on server)
			String[] splitString = packetStr.split(",");

			if (splitString.length != 1) {

			} else {
				int entityId = Integer.parseInt(splitString[0]);
				gameLoop.onClientDeath(entityId);
			}

		} else if (actionId == 11) { // Client changed weapons

		} else if (actionId == 12) { // Client has started the game
			startNum++;
			server.setPressedStartBools(id, true);
			if (startNum == server.getIdCount()) {
				// Stop searching for new clients
				server.setRunningSearch(false);
				gameThread();

				// Reset bad packet counter
				badPackets = 0;
			}
		} else if (actionId == 13) {
			startNum--;
			server.setPressedStartBools(id, false);
		} else {
			// System.out.println("Bad Packet");
			System.out.println("Bad Packet w/ actionId " + actionId + " and data '" + packetStr + "'");
			badPackets++;
			if (badPackets > 5) {
				server.removeClient(id);
			}
		}
	}

	public void gameThread() {
		System.out.println("gameloop started");
		Thread thread = new Thread() {
			@Override
			public void run() {
				if (withGui) {
					gameLoop = new GameLoop(server.getIdList(), server, server.gui); // pass through clients + ids
					System.out.println("gameloop created 1");
				} else {
					gameLoop = new GameLoop(server.getIdList(), server);
					System.out.println("gameloop created");
				}

				// Start game (clients + host are informed via this method)
				if (gameLoop == null) {
					System.out.println("HEREHEREHERE");
				}

				gameLoop.startGame();
			}
		};

		// thread.setDaemon(false); //so it doesn't prevent JVM shutdown
		thread.start();
	}

	/**
	 * This method responds to the UDP packets received from a connection
	 * 
	 * @param dgPacket - DatagramPacket - the actual packet received (for ip/port
	 *                 purposes)
	 * @param actionId - int - the id of what action to perform
	 * @param data     - byte[] - any additional data received
	 */
	public void received(DatagramPacket dgPacket, int actionId, byte[] data) { // For UDP Deciphering

		if (actionId == 0) { // UDP Set-up Command
			String dataStr = new String(data, StandardCharsets.UTF_8);
			int id = Integer.parseInt(dataStr.substring(0, 1)); // TODO - check substring
			dataStr = dataStr.substring(1, dataStr.length());
			server.setServerUDPConnected(true);
			if (server.serverUDPConnection != null) {
				server.serverUDPConnection.setReturnVars(dgPacket.getPort(), dgPacket.getAddress(), id);
			}

		} else if (actionId == 1) { // Movement Command
			/*
			 * String[] splitString = dataStr.split(",");
			 * 
			 * Integer screenId = Integer.parseInt(splitString[0]); Integer entityId =
			 * Integer.parseInt(splitString[1]); double xpos =
			 * Double.parseDouble(splitString[2]); double ypos =
			 * Double.parseDouble(splitString[3]); Vector position = new Vector(xpos, ypos);
			 * Double xVector = Double.parseDouble(splitString[4]); Double yVector =
			 * Double.parseDouble(splitString[5]); Vector velocity = new Vector(xVector,
			 * yVector); double rotation = Double.parseDouble(splitString[6]); double
			 * rotationalVelocity = Double.parseDouble(splitString[7]);
			 */
			byte[] byteUdpId = new byte[4];
			for (int x = 0; x < 4; x++) {
				byteUdpId[x] = data[x];
			}
			int id = ByteBuffer.wrap(byteUdpId).getInt();

			byte[] byteScreen = new byte[4];
			for (int x = 4; x < 8; x++) {
				byteScreen[x - 4] = data[x];
			}
			int screenId = ByteBuffer.wrap(byteScreen).getInt();

			byte[] bytePlayer = new byte[4];
			for (int x = 8; x < 12; x++) {
				bytePlayer[x - 8] = data[x];
			}
			int playerId = ByteBuffer.wrap(bytePlayer).getInt();

			byte[] byteXpos = new byte[8];
			for (int x = 12; x < 20; x++) {
				byteXpos[x - 12] = data[x];
			}
			double xpos = ByteBuffer.wrap(byteXpos).getDouble();

			byte[] byteYpos = new byte[8];
			for (int x = 20; x < 28; x++) {
				byteYpos[x - 20] = data[x];
			}
			double ypos = ByteBuffer.wrap(byteYpos).getDouble();

			Vector position = new Vector(xpos, ypos);

			byte[] byteXVector = new byte[8];
			for (int x = 28; x < 36; x++) {
				byteXVector[x - 28] = data[x];
			}
			Double xVector = ByteBuffer.wrap(byteXVector).getDouble();

			byte[] byteYVector = new byte[8];
			for (int x = 36; x < 44; x++) {
				byteYVector[x - 36] = data[x];
			}
			Double yVector = ByteBuffer.wrap(byteYVector).getDouble();

			Vector velocity = new Vector(xVector, yVector);

			byte[] byteRotation = new byte[8];
			for (int x = 44; x < 52; x++) {
				byteRotation[x - 44] = data[x];
			}
			double rotation = ByteBuffer.wrap(byteRotation).getDouble();

			byte[] byteRotationalVelocity = new byte[8];
			for (int x = 52; x < 60; x++) {
				byteRotationalVelocity[x - 52] = data[x];
			}
			double rotationalVelocity = ByteBuffer.wrap(byteRotationalVelocity).getDouble();

			byte[] byteInvulnerableTime = new byte[4];
			for (int x = 60; x < 64; x++) {
				byteInvulnerableTime[x - 60] = data[x];
			}
			int invTime = ByteBuffer.wrap(byteInvulnerableTime).getInt();

			if (gameLoop != null) {
				gameLoop.onMovementVelocityUpdateEvent(id, screenId, playerId, position, velocity, rotation,
						rotationalVelocity, invTime);
			}
		}
	}

	/**
	 * Decrements the startNum attribute that keeps track of how many players are
	 * waiting to start the game
	 */
	public void decrStartNum() {
		startNum--;
	}

	/**
	 * Gets the number of bad packets this object has currently collected in a row
	 * (mainly for testing purposes)
	 * 
	 * @return badPackets - number of bad packets
	 */
	public int getBadPackets() {
		return badPackets;
	}

}
