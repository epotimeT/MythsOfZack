package com.game.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * A class used to handle sending and receiving UDP packets on the Client side.
 * @author sjo948, kxg911
 *
 */
public class ClientUDP implements Runnable
{
	/**
	 * The socket used to accept DatagramPackets.
	 */
	private DatagramSocket socket;
	
	/**
	 * The server's IP address.
	 */
	private static InetAddress address;
	
	/**
	 * A byte buffer to contain any DatagramPackets that are received.
	 */
	private static byte[] buff = new byte[256];
	
	/**
	 * A boolean that tells us whether or not the object is running at this point.
	 */
	private boolean running = false;
	
	/**
	 * The EventListener for this client.
	 */
	private EventListener listener;
	
	/**
	 * The server-given id of this client.
	 */
	private int id;
	
	/**
	 * The port the server is listening on for UDP.
	 */
	private int serverPort;

	
	/**
	 * The constructor for this class.
	 * @param stringAddress The address of the server.
	 * @param sPort The server's port.
	 * @param eListener The EventListener where packets are taken to be unpacked.
	 */
	public ClientUDP(String stringAddress, int sPort, EventListener eListener) {
		listener = eListener;
		serverPort = sPort;

		try {
			socket = new DatagramSocket();
			address = InetAddress.getByName(stringAddress);
		} catch (SocketException e) {
			System.out.println("Socket Exception while creating ClientUDP");
		} catch (UnknownHostException e) {
			System.out.println("Unknown Host Exception while creating ClientUDP");
		}

	}
	
	
	/**
	 * Used to close the UDP socket at the end of use.
	 */
	public void closeSocket() {
		running = false;
		socket.close();
	}
	
	
	/**
	 * Used to set up a UDP connection.  Need to rewrite this function and comment.
	 * @param actionId This is the actionID for this packet, informing the 
	 * server of how to unpack it.
	 * @param msg This is the actual message sent by this function.
	 */
	public void sendPacket(int actionId, String msg) {

		try {

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

			byte[] textToSend = msg.getBytes(StandardCharsets.UTF_8);
			byte[] idToSend = ByteBuffer.allocate(4).putInt(actionId).array();

			int textLength = textToSend.length;// Get length of text
			byte[] byteLength = ByteBuffer.allocate(4).putInt(textLength).array();

			byteOut.write(byteLength);
			byteOut.write(idToSend);
			byteOut.write(textToSend);

			byte[] toSendArray = new byte[textLength + 8];
			toSendArray = byteOut.toByteArray();

			DatagramPacket packet = new DatagramPacket(toSendArray, toSendArray.length, address, serverPort);
			socket.send(packet);
			System.out.println("Sent packet with msg: " + msg);

		} catch (IOException e) {
			
			System.out.println("IO Exception while sending UDP packet");
		
		}
		
	}
	
	
	/**
	 * Sends a movement packet from the client to the server, including the player's current
	 * position, velocity, and rotation among other data.
	 * @param actionId The actionId of this packet, used to tell the server and other
	 * clients what it's for.
	 * @param screenId The ID of the screen on which this player is currently situated.
	 * @param playerId This player's TCP ID.  The UDP ID is also sent, but we get this
	 * directly from the global variables of this class.
	 * @param xpos The player's position on the X-axis.
	 * @param ypos The player's position on the Y-axis.
	 * @param xVector The player's speed in the X-direction.
	 * @param yVector The player's speed in the Y-direction.
	 * @param rotation The angle which the player is currently facing.
	 * @param rotationalVelocity The velocity at which the player is currently rotating.
	 * @param invulnerableTime The time for which this player is still invulnerable.
	 */
	public void sendPacket(int actionId, int screenId, int playerId, double xpos, 
			double ypos, double xVector, double yVector, double rotation, 
			double rotationalVelocity, int invulnerableTime) {
		
		try {

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			int length = 0;
			byte[] idToSend = ByteBuffer.allocate(4).putInt(actionId).array();
			
			byte[] myUdpId = ByteBuffer.allocate(4).putInt(id).array();
			byte[] byteScreenId = ByteBuffer.allocate(4).putInt(screenId).array();
			byte[] bytePlayerId = ByteBuffer.allocate(4).putInt(playerId).array();
			byte[] byteXpos = ByteBuffer.allocate(Double.BYTES).putDouble(xpos).array();
			byte[] byteYpos = ByteBuffer.allocate(Double.BYTES).putDouble(ypos).array();
			byte[] byteXvector = ByteBuffer.allocate(Double.BYTES).putDouble(xVector).array();
			byte[] byteYvector = ByteBuffer.allocate(Double.BYTES).putDouble(yVector).array();
			byte[] byteRotation = ByteBuffer.allocate(Double.BYTES).putDouble(rotation).array();
			byte[] byteRotationalVelocity = ByteBuffer.allocate(Double.BYTES).putDouble(rotationalVelocity).array();
			byte[] byteInvulnerableTime = ByteBuffer.allocate(4).putInt(invulnerableTime).array();

			length = (4 * 4) + ((Double.BYTES) * 6);
			byte[] byteLength = ByteBuffer.allocate(4).putInt(length).array();

			byteOut.write(byteLength);
			byteOut.write(idToSend);
			byteOut.write(myUdpId);
			byteOut.write(byteScreenId);
			byteOut.write(bytePlayerId);
			byteOut.write(byteXpos);
			byteOut.write(byteYpos);
			byteOut.write(byteXvector);
			byteOut.write(byteYvector);
			byteOut.write(byteRotation);
			byteOut.write(byteRotationalVelocity);
			byteOut.write(byteInvulnerableTime);

			byte[] toSendArray = new byte[length];
			toSendArray = byteOut.toByteArray();

			DatagramPacket packet = new DatagramPacket(toSendArray, toSendArray.length, address, serverPort);
			socket.send(packet);
			// System.out.println("Sent packet");

		} catch (IOException e) {
			
			System.out.println("IO Exception while sending UDP packet");
		
		}
		
	}
	
	
	/**
	 * The run function for this class.  Waits for a DatagramPacket to be received;
	 * unpacks the first few parts of that packet, then sends it to the listener, 
	 * before waiting for the next packet.
	 */
	@Override
	public void run() {

		try {

			running = true;
			System.out.println("Listening for UDP packets");

			while (running == true) {
				DatagramPacket packet = new DatagramPacket(buff, buff.length);
				socket.receive(packet);

				byte[] byteLength = new byte[4];
				for (int x = 0; x < 4; x++) {
					byteLength[x] = buff[x];
				}

				int length = ByteBuffer.wrap(byteLength).getInt();
				byte[] byteId = new byte[4];

				for (int x = 4; x < 8; x++) {
					byteId[x - 4] = buff[x];
				}

				int actionID = ByteBuffer.wrap(byteId).getInt();

				byte[] byteData = new byte[length];

				for (int x = 8; x < length + 8; x++) {
					byteData[x - 8] = buff[x];
				}
				
				listener.receivedUDP(byteData);
				
			}
			
		} catch (IOException e) {

			System.out.println("IO Exception while running");

		}
		
		closeSocket();
		
	}
	
	
	/**
	 * Used to set the UDP id of this client.
	 * @param id The id given to this client by the server.
	 */
	public void setID(int id) {
		
		System.out.println(id);
		this.id = id;
	
	}
	
	
	/**
	 * A function used to get the client's UDP ID.
	 * @return Returns the UDP ID of this client.
	 */
	public int getID() {
		
		return id;
	
	}
	
	
	/**
	 * A function to check whether or not this object is currently running.
	 * Used by the thread that updates the gui.
	 * @return Returns a boolean telling us whether or not it's running.
	 */
	public boolean getRunning() {
	
		return running;
	
	}

}
