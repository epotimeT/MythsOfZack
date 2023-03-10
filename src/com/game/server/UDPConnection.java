package com.game.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.game.utilities.Semaphore;

/**
 * <h1>UDPConnection</h1> This class sets up everything needed to communicate
 * via UDP
 */
public class UDPConnection extends Connection implements Runnable {

	/**
	 * The list of IP addresses for sending UDP data to
	 */
	public InetAddress addresses[];

	private DatagramSocket dgSocket;
	private Semaphore semUDPStarted;

	private boolean running;

	private byte[] buf = new byte[256];

	private int ports[];
	private int thisPort;

	// Constructor
	/**
	 * Creates a UDPConnction object
	 * 
	 * @param port       - the port number to listen on
	 * @param decipherer - reference to the EventDecipherer object
	 * @param server     - reference to the Server object
	 * @param sem        - reference to the Semaphore object that ensures the server
	 *                   is listening for UDP
	 */
	public UDPConnection(int port, EventDecipherer decipherer, Server server, Semaphore sem) {
		super(decipherer, server);
		thisPort = port;
		running = true;
		addresses = new InetAddress[Server.MAX_CLIENTS];
		ports = new int[Server.MAX_CLIENTS];
		this.semUDPStarted = sem;

		try {
			dgSocket = new DatagramSocket(thisPort);

		} catch (IOException e) {
			System.out.println("IOException - UDPConnection Constructor");
		}
	}

	// Send Data to Client Method
	/**
	 * Method that sends data via UDP
	 * 
	 * @param actionId - the id of the command that needs to be executed
	 * @param packet   - any actual data that needs to be sent
	 * @param id       - the id of client the data will be sent to
	 */
	@Override
	public void sendData(int actionId, String packet, int id) {
		try {
			if (addresses[id] == null) {
				System.out.println("Must receive UDP first");
			} else {
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();

				byte[] textToSend = packet.getBytes(StandardCharsets.UTF_8);
				byte[] idToSend = ByteBuffer.allocate(ACTIONID_SIZE).putInt(actionId).array();

				int textLength = textToSend.length;// Get length of text
				byte[] byteLength = ByteBuffer.allocate(BYTES_SIZE).putInt(textLength).array();

				bOut.write(byteLength);
				bOut.write(idToSend);
				bOut.write(textToSend);

				byte[] toSendArray = new byte[textLength + ACTIONID_SIZE + BYTES_SIZE];
				toSendArray = bOut.toByteArray();

				DatagramPacket finalPacket = new DatagramPacket(toSendArray, toSendArray.length, addresses[id],
						ports[id]);
				dgSocket.send(finalPacket);
				// System.out.println("Sent packet to " + id + " via UDP");
			}

		} catch (IOException e) {
			System.out.println("IOException - Connecting Sending UDP Object");
		}
	}

	// TODO - i dont know what this method does
	/**
	 * 
	 * @param actionId
	 * @param screenId
	 * @param playerId
	 * @param xpos
	 * @param ypos
	 * @param xVector
	 * @param yVector
	 * @param rotation
	 * @param rotationalVelocity
	 * @param udpid
	 */
	public void sendPacket(int actionId, int screenId, int playerId, double xpos, double ypos, double xVector,
			double yVector, double rotation, double rotationalVelocity, int udpid) {

		try {
			if (addresses[udpid] == null) {
				System.out.println("Must receive UDP first");
			} else {
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				int length = 0;
				byte[] idToSend = ByteBuffer.allocate(4).putInt(actionId).array();

				// byte[] byteLength = ByteBuffer.allocate(4).putInt(textLength).array();
				// byte[] myUdpId = ByteBuffer.allocate(4).putInt(udpid).array();
				byte[] byteScreenId = ByteBuffer.allocate(4).putInt(screenId).array();
				byte[] bytePlayerId = ByteBuffer.allocate(4).putInt(playerId).array();
				byte[] byteXpos = ByteBuffer.allocate(Double.BYTES).putDouble(xpos).array();
				byte[] byteYpos = ByteBuffer.allocate(Double.BYTES).putDouble(ypos).array();
				byte[] byteXvector = ByteBuffer.allocate(Double.BYTES).putDouble(xVector).array();
				byte[] byteYvector = ByteBuffer.allocate(Double.BYTES).putDouble(yVector).array();
				byte[] byteRotation = ByteBuffer.allocate(Double.BYTES).putDouble(rotation).array();
				byte[] byteRotationalVelocity = ByteBuffer.allocate(Double.BYTES).putDouble(rotationalVelocity)
						.array();

				length = (4 * 2) + ((Double.BYTES) * 6);
				byte[] byteLength = ByteBuffer.allocate(4).putInt(length).array();

				byteOut.write(byteLength);
				byteOut.write(idToSend);
				// byteOut.write(myUdpId);
				byteOut.write(byteScreenId);
				byteOut.write(bytePlayerId);
				byteOut.write(byteXpos);
				byteOut.write(byteYpos);
				byteOut.write(byteXvector);
				byteOut.write(byteYvector);
				byteOut.write(byteRotation);
				byteOut.write(byteRotationalVelocity);

				byte[] toSendArray = new byte[length];
				toSendArray = byteOut.toByteArray();

				DatagramPacket finalPacket = new DatagramPacket(toSendArray, toSendArray.length, addresses[udpid],
						ports[udpid]);
				dgSocket.send(finalPacket);
				// System.out.println("Sent packet to " + udpid + " via UDP");

				/*
				 * DatagramPacket packet = new DatagramPacket(toSendArray, toSendArray.length,
				 * address, serverPort); socket.send(packet); System.out.println("Sent packet");
				 */

			}

		} catch (IOException e) {
			System.out.println("IO Exception while sending UDP packet");
		}

	}

	// TODO - same as above
	/**
	 * 
	 * @param actionId
	 * @param screenId
	 * @param playerId
	 * @param xpos
	 * @param ypos
	 * @param xVector
	 * @param yVector
	 * @param rotation
	 * @param rotationalVelocity
	 */
	public void broadcastPacket(int actionId, int screenId, int playerId, double xpos, double ypos, double xVector,
			double yVector, double rotation, double rotationalVelocity) {
		for (int id = 0; id < addresses.length; id++) {
			if (addresses[id] == null) {
				continue;
			}
			sendPacket(actionId, screenId, playerId, xpos, ypos, xVector, yVector, rotation, rotationalVelocity, id);
		}
	}

	// TODO - same as above
	/**
	 * 
	 * @param actionId
	 * @param exceptionId
	 * @param screenId
	 * @param playerId
	 * @param xpos
	 * @param ypos
	 * @param xVector
	 * @param yVector
	 * @param rotation
	 * @param rotationalVelocity
	 */
	public void broadcastPacketExceptClient(int actionId, int exceptionId, int screenId, int playerId, double xpos,
			double ypos, double xVector, double yVector, double rotation, double rotationalVelocity) {
		for (int id = 0; id < addresses.length; id++) {
			if (id == exceptionId || addresses[id] == null) {
				continue;
			}
			sendPacket(actionId, screenId, playerId, xpos, ypos, xVector, yVector, rotation, rotationalVelocity, id);
		}
	}

	// Shutdown UDP-related Objects
	/**
	 * Shuts down the UDP listener and calls to close the socket
	 */
	@Override
	public void shutdownConnection() {
		running = false;
		closeDGSocket();
	}

	// Close the dgSocket (own method for calling from server emergency shutdown)
	/**
	 * Closes the socket
	 */
	public void closeDGSocket() {
		dgSocket.close();
	}

	// Main Thread Method
	/**
	 * The thread that continually listens for UDP data from all clients, then
	 * carries out actions based on that data
	 */
	@Override
	public void run() {
		System.out.println("UDP Server is Listening");
		semUDPStarted.take();
		while (running) {
			try {
				// System.out.println("Listening for a new UDP packet");
				DatagramPacket dgPacket = new DatagramPacket(buf, buf.length);
				dgSocket.receive(dgPacket);

				// Declare byte arrays (+init where possible)
				byte[] fullData = dgPacket.getData();
				byte[] byteLength = new byte[BYTES_SIZE];
				byte[] byteActionId = new byte[ACTIONID_SIZE];
				byte[] data;

				// Get ActionID and ByteLength
				for (int i = 0; i < BYTES_SIZE; i++) {
					byteLength[i] = fullData[i];
					// byteActionId[i] = fullData[i+BYTES_SIZE];
				}
				for (int i = BYTES_SIZE; i < BYTES_SIZE + ACTIONID_SIZE; i++) {
					byteActionId[i - BYTES_SIZE] = fullData[i];
				}

				// Turn their byte values into an int
				int intLength = ByteBuffer.wrap(byteLength).getInt();
				int actionId = ByteBuffer.wrap(byteActionId).getInt();

				// Init data using intLength + fill with data
				data = new byte[intLength];
				int iStart = BYTES_SIZE + ACTIONID_SIZE;
				for (int i = iStart; i < iStart + intLength; i++) {
					data[i - iStart] = fullData[i];
				}

				// Find what to do
				decipherer.received(dgPacket, actionId, data);

			} catch (IOException e) {
				System.out.println("IOException - UDPConnection Running");

			} catch (NullPointerException e) {
				System.out.println("Null Pointer - UDPConnection Running");
				running = false;
			}
		}
	}

	/**
	 * Stores the information about a client's computer for sending UDP data at a
	 * later date
	 * 
	 * @param port    - the port that the client is using
	 * @param address - the ip address of the client's computer
	 * @param id      - the id of the client
	 */
	public void setReturnVars(int port, InetAddress address, int id) {
		addresses[id] = address;
		ports[id] = port;
	}

}
