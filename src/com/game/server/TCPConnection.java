package com.game.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.game.utilities.Semaphore;

/**
 * <h1>Creates a TCP Connection</h1> This class creates a TCP link between the
 * server and the client, allowing them to continually communicate
 */
public class TCPConnection extends Connection {

	private Socket socket; // this is the socket connected to the client
	private DataInputStream in;
	private DataOutputStream out;

	private int id;

	// Constructor
	/**
	 * Creates a TCPConnection object
	 * 
	 * @param socket     - the socket that the server will communicate on
	 * @param id         - the id given to this connection by the server
	 * @param decipherer - reference to the EventDecipherer object
	 * @param server     - reference to the Server object
	 * @param sem        - reference to the Semaphore object that ensures the
	 *                   connection starts
	 */
	public TCPConnection(Socket socket, int id, EventDecipherer decipherer, Server server, Semaphore sem) {
		super(decipherer, server);
		this.socket = socket;
		this.id = id;
		thisStarted = sem;

		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("IOException - TCPConnection Constructor");
		}
	}

	// Send Data to Client Method
	/**
	 * Method that sends data via the TCP connection
	 * 
	 * @param actionId - the id of the command that needs to be executed
	 * @param packet   - any actual data that needs to be sent
	 */
	@Override
	public void sendData(int actionId, String packet) {
		try {
			System.out.println(
					"Connection " + id + " is sending action id '" + actionId + "' with data '" + packet + "' via TCP");

			// Get all components of the instruction
			byte[] actionIdBytes = ByteBuffer.allocate(ACTIONID_SIZE).putInt(actionId).array();
			byte[] dataBytes = packet.getBytes(StandardCharsets.UTF_8);
			byte[] dataLenBytes = ByteBuffer.allocate(BYTES_SIZE).putInt(dataBytes.length).array();

			// Link components together using a ByteArrayOutputStream
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			bOut.write(dataLenBytes);
			bOut.write(actionIdBytes);
			bOut.write(dataBytes);

			byte[] finalPacket = bOut.toByteArray();

			// Send result
			out.write(finalPacket);
			out.flush();

		} catch (IOException e) {
			System.out.println("IOException - Connection Sending Object");
		}

	}

	/**
	 * Extension of sendData() that sends data to all clients
	 * 
	 * @param actionId - the id of the command that needs to be executed
	 * @param packet   - any actual data that needs to be sent
	 * @param con      - list of active connections
	 */
	static void broadcastData(int actionId, String packet, Connection[] con) {
		for (int i = 0; i < Server.MAX_CLIENTS; i++) {
			if (con[i] != null) {
				con[i].sendData(actionId, packet);
			}
		}
	}

	/**
	 * Extension of sendData() that sends data to all clients except one
	 * 
	 * @param actionId - the id of the command that needs to be executed
	 * @param packet   - any actual data that needs to be sent
	 * @param clientId - the id of the client to not send data to
	 * @param con      - list of active connections
	 */
	public static void broadcastDataExceptClient(int actionId, String packet, int clientId, Connection[] con) {
		for (int i = 0; i < Server.MAX_CLIENTS; i++) {
			if (con[i] != null && i != clientId) {
				con[i].sendData(actionId, packet);
			}
		}
	}

	// Shutdown TCP-related Objects
	/**
	 * Shuts down the TCP connection and the related object streams
	 */
	@Override
	public void shutdownConnection() {
		try {
			in.close(); // close inputstream
			out.close(); // close outputstream
			socket.close(); // close socket

		} catch (IOException e) {
			System.out.println("IOException - TCPConnection Shutdown Connection");
		}

	}

	// Main Thread Method
	@Override
	/**
	 * The thread that continually listens for TCP data from the client, then
	 * carries out actions based on that data
	 */
	public void run() {
		try {
			thisStarted.take();
			while (!socket.isClosed()) { // returns the connection state of the socket (false = connected)

				byte[] byteLength = new byte[BYTES_SIZE];

				System.out.println("TCP has started");

				int x = in.read(byteLength, 0, BYTES_SIZE);
				int length = ByteBuffer.wrap(byteLength).getInt();

				byte[] byteID = new byte[ACTIONID_SIZE];
				in.read(byteID, 0, ACTIONID_SIZE);
				int actionId = ByteBuffer.wrap(byteID).getInt();

				byte[] data = new byte[1];
				if (x > 0) {
					data = new byte[length];
					in.read(data, 0, length);
				}

				decipherer.received(id, actionId, data);

			}

		} catch (EOFException e) {
			System.out.println("EOFException - TCPConnection Running");

		} catch (IOException e) {
			System.out.println("IOException - TCPConnection Running"); // Client has disconnected

		}
		server.removeClient(id);
	}

}
