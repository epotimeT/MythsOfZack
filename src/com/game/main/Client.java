package com.game.main;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Stack;

import com.game.screens.GUI;
import com.game.utilities.Semaphore;

/**
 * A class used to maintain a connection between the client and the server,
 * sending all necessary data across it.
 * 
 * @author sjo948, kxg911
 *
 */
public class Client implements Runnable { // runnable needed to support threading

	/**
	 * Host ip address.
	 */
	private String host;

	/**
	 * Port on which server is running.
	 */
	private int port;

	/**
	 * Object used for UDP communication over server.
	 */
	public ClientUDP myUDP;

	/**
	 * For communicating with server. 
	 */
	private Socket socket;

	/**
	 * For receiving data from server.
	 */
	private DataInputStream in;

	/**
	 * For sending data to server.
	 */
	private DataOutputStream out;

	/**
	 * For breaking out of or staying in run method.
	 */
	private boolean running = false;

	/**
	 * For deciphering data.
	 */
	private EventListener listener;

	/**
	 * The name this client has given themselves.
	 */
	private String name;

	/**
	 * The game's GUI. Allows us to display data there.
	 */
	private GUI gui;

	/**
	 * Stops shut-down method being called twice.
	 */
	private boolean shuttingDown;

	/**
	 * For updating the lobby GUI with names.
	 */
	private String[] clientNames = new String[4];

	/**
	 * A stack of messages sent from other clients while in the lobby. Used to
	 * display them on the GUI.
	 */
	Stack<String> messages = new Stack<String>();

	/**
	 * For ensuring the player's name loads before the lobby does.
	 */
    private Semaphore sem1;
	
	/**
	 * The constructor for this class.
	 * @param host The ip address of the server.
	 * @param port The port on which the server is being run.
	 * @param name The name of the player.
	 * @param gui The gui on which the player is playing.
	 */
	public Client(String host, int port, String name, GUI gui) {
		
		this.host = host;
		this.port = port;
		this.name = name;
		this.gui = gui;

		// Initialises semaphore
		sem1 = new Semaphore();

	}

	/**
	 * Constructor in the event that this client is also running the server.
	 * @param port The port on which the server is being run.
	 * @param name The name of the player.
	 * @param gui The gui on which the player is playing.
	 */
	public Client(int port, String name, GUI gui) {

		this.host = "localhost";
		this.port = port;
		this.name = name;
		this.gui = gui;

		// Initialises semaphore
		sem1 = new Semaphore();

	}

	/**
	 * Method to connect the client to the server.
	 * @return Returns true if the client is connected, false if not.
	 */
	public boolean connectClient() {

		try {

			System.out.println("Connecting to server");
			shutdownHook();
			shuttingDown = false; // set shuttingDown property
			socket = new Socket(host, port); // initialise socket
			in = new DataInputStream(socket.getInputStream()); // initialise inputstream
			out = new DataOutputStream(socket.getOutputStream()); // initialise outputstream
			listener = new EventListener(this, sem1, gui); // initialise eventlistener

			System.out.println("Creating thread");

			myUDP = new ClientUDP(host, port, listener);
			Thread udp = new Thread(myUDP);
			udp.setDaemon(true);
			udp.start();
			
			Thread tcp = new Thread(this);
			tcp.setDaemon(true);
			tcp.start(); // create new thread to run the client networking on

			boolean passed = sem1.release();
			
			if(!passed) {
				
				disconnectClient();
				return false;

			}

			return true;

		} catch (ConnectException e) {

			System.out.println("ConnectException - Connecting");
			// Tell player no server has been found and to try again
			return false;

		} catch (IOException e) {

			System.out.println("IOException - Connecting");
			return false;

		}

	}

	/**
	 * Method to disconnect from the server.
	 */
	public void disconnectClient() {
		try {

			if (!shuttingDown) {

				sendByteArray(2, "");

				shuttingDown = true;
				System.out.println("Client disconnecting");

				// ensure running is false
				running = false;
				myUDP.closeSocket();

				// close inputstream
				in.close();

				// close outputstream
				out.close();

				// close socket
				socket.close();
				
				for (int i=0; i<4; i++) {
				
					clientNames[i] = null;
				
				}

				System.out.println("Client has disconnected");

			}

		} catch (IOException e) {

			System.out.println("IOException - Disconnecting\n" + e);

		}

	}

	/**
	 * Method to send data to the server.  Converts data to String, and String to byte array.
	 * @param actionID The actionID of this packet; tells the EventListener object what's happened.
	 * @param text The rest of the packet data, put into a string.
	 */
	public void sendByteArray(int actionID, String text) {
		
		try {

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

			// Put the text in a byte array
			byte[] textToSend = text.getBytes(StandardCharsets.UTF_8);
			byte[] idToSend = ByteBuffer.allocate(4).putInt(actionID).array();

			// Get length of text
			int textLength = textToSend.length;
			byte[] byteLength = ByteBuffer.allocate(4).putInt(textLength).array();

			byteOut.write(byteLength);
			byteOut.write(idToSend);
			byteOut.write(textToSend);

			// Create array to hold both length of text and text itself
			byte[] toSendArray = new byte[8 + textLength];
			toSendArray = byteOut.toByteArray();

			// data written to the object stream
			out.write(toSendArray, 0, (textLength + 8));
			out.flush();
			System.out.println("Client sent actionID " + actionID + " with data '" + text + "'.");

		} catch (IOException e) {

			System.out.println("IOException - Sending Object");

		}

	}

	/*
	 * public void sendByteArray(int actionID, int screenId, int playerId, double
	 * xpos, double ypos, double xVector, double yVector, double rotation, double
	 * rotationalVelocity) { //sendByteArray for attack
	 * 
	 * try {
	 * 
	 * ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	 * 
	 * //Put the text in a byte array byte[] idToSend =
	 * ByteBuffer.allocate(4).putInt(actionID).array();
	 * 
	 * //Get length of text int textLength = textToSend.length; byte[] byteLength =
	 * ByteBuffer.allocate(4).putInt(textLength).array();
	 * 
	 * byte[] byteScreenId = ByteBuffer.allocate(4).putInt(screenId).array(); byte[]
	 * bytePlayerId = ByteBuffer.allocate(4).putInt(playerId).array(); byte[]
	 * byteXpos = ByteBuffer.allocate(Double.BYTES).putDouble(xpos).array(); byte[]
	 * byteYpos = ByteBuffer.allocate(Double.BYTES).putDouble(ypos).array(); byte[]
	 * byteXvector = ByteBuffer.allocate(Double.BYTES).putDouble(xVector).array();
	 * byte[] byteYvector =
	 * ByteBuffer.allocate(Double.BYTES).putDouble(yVector).array(); byte[]
	 * byteRotation = ByteBuffer.allocate(Double.BYTES).putDouble(rotation).array();
	 * byte[] byteRotationalVelocity =
	 * ByteBuffer.allocate(Double.BYTES).putDouble(rotationalVelocity).array();
	 * 
	 * byteOut.write(byteLength); byteOut.write(idToSend);
	 * byteOut.write(textToSend);
	 * 
	 * //Create array to hold both length of text and text itself byte[] toSendArray
	 * = new byte[8+textLength]; toSendArray = byteOut.toByteArray();
	 * 
	 * //data written to the object stream out.write(toSendArray, 0,
	 * (textLength+8)); out.flush(); System.out.println("Client sent actionID " +
	 * actionID + " with data '" + text + "'.");
	 * 
	 * } catch (IOException e) {
	 * 
	 * System.out.println("IOException - Sending Object");
	 * 
	 * }
	 * 
	 * }
	 */

	/**
	 * Helps gui know when client has disconnected, or to check whether connection is good.
	 * @return Whether or not the client has disconnected.
	 */
	public boolean getRunning() {

		boolean udpRunning = myUDP.getRunning();

		if (running == true && udpRunning == true) {

			return true;

		} else {

			return false;

		}

	}

	/**
	 * Shut-down Hook (for when JVM is forced to close - ensures the sockets are shut).
	 */
	public void shutdownHook() {

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {

				disconnectClient();
				System.out.println("Client has disconnected");

			}

		});

	}

	/**
	 * Main thread method for receiving TCP packets.
	 */
	@Override
	public void run() {

		try {

			running = true; // start listening for data
			System.out.println("About to listen");

			while (running) {

				try {

					byte[] byteLength = new byte[4];
					in.read(byteLength, 0, 4); // attempt to read any data in stream
					int length = ByteBuffer.wrap(byteLength).getInt();
					byte[] byteID = new byte[4];
					in.read(byteID, 0, 4);
					int actionId = ByteBuffer.wrap(byteID).getInt();
					byte[] data = new byte[length];
					in.read(data, 0, length);
					listener.received(actionId, data); // once received, forward to listener to decipher

				} catch (SocketException e) {

					System.out.println("SocketException - Running");

					// ensure client disconnects if issue with protocol
					disconnectClient();

				}

			}

		} catch (IOException e) {

			System.out.println("IOException - Running");
			disconnectClient();

		}

	}

	/**
	 * Used to get the client's name.
	 * 
	 * @return Returns this client's name as a string.
	 */
	public String getName() { // gets name of this player

		return name;

	}

	/**
	 * Used to get the names of all players.
	 * 
	 * @return Returns a string array of all of the players' names, in ID order.
	 */
	public String[] getClientNames() {

		return clientNames;

	}

	/**
	 * Used to get a stack of messages sent in the lobby.
	 * @return A copy of the stack.
	 */
	public Stack<String> getMessages(){
    	Stack<String> messagesCpy = new Stack<String>();

    	int messagesSize = messages.size();
    	
    	if (!messages.empty()) {
    	
    		for (int i = 0; i < messagesSize; i++) {
        	
    			messagesCpy.push(messages.pop());
        	
    		}
    	
    	}
    	
        return messagesCpy;
    
    }
    
    
	/**
	 * Used to push a message to the stack.
	 * @param message The message to be pushed
	 */
	public void setMessages(String message){
    	
        messages.push(message);
        
    }
     
	
	/**
	 * Sets the other clients' names and IDs.
	 * @param id The client's integer ID on the server.
	 * @param name The client's name.
	 */
	public void setClientName(int id, String name) {
		
		clientNames[id] = name;

		if (name == null) { //client left
			
		}
	
	}

	/**
	 * Sets the name of the client in the lobby.
	 * @param id the id of the player on the server.
	 */
	public void setClientName(int id) {
		
		clientNames[id] = this.name;
	
	}

	/**
	 * Sets an event callback for a packet received by the "run" method.
	 * @param callback The callback being made.
	 */
    public void setEventCallback(NetworkEventCallback callback) {
	
        this.listener.setCallback(callback);
    
    }

    
    /**
     * Unsets the previously set event callback.
     */
    public void unsetEventCallback() {
    
    	this.listener.unsetCallback();
    
    }

}
