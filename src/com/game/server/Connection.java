package com.game.server;

import com.game.utilities.Semaphore;

/**
 * <h1>Connection</h1> This abstract class is the outline for the classes that
 * keep track of network connections (i.e. TCP and UDP)
 */
public abstract class Connection implements Runnable {

	protected static final int ACTIONID_SIZE = 4;
	protected static final int CLIENTID_SIZE = 4;
	protected static final int BYTES_SIZE = 4;

	protected EventDecipherer decipherer; // to decipher received data
	protected Server server; // to call server methods
	protected Semaphore thisStarted;

	// Constructor
	/**
	 * Creates a Connection object
	 * 
	 * @param decipherer - reference to the EventDecipherer object
	 * @param server     - reference to the Server object
	 */
	public Connection(EventDecipherer decipherer, Server server) {
		this.decipherer = decipherer;
		this.server = server;
	}

	// Send Data Method via TCP
	/**
	 * Outline for the TCP send data method (helps to ensure everyone uses the
	 * correct parameters)
	 * 
	 * @param actionId - the id of the command being sent
	 * @param packet   - the actual data that needs to be sent
	 */
	public void sendData(int actionId, String packet) {
		System.out.println("Error - this method has not been initialised\n" + "Are you calling the wrong method args?");
	}

	// Send Data Method via UDP
	/**
	 * Outline for the UDP send data method (helps to ensure everyone uses the
	 * correct parameters)
	 * 
	 * @param actionId - the id of the command being sent
	 * @param packet   - the actual data that needs to be sent
	 * @param id       - the id of the UDP connection
	 */
	public void sendData(int actionId, String packet, int id) {
		System.out.println("Error - this method has not been initialised\n" + "Are you calling the wrong method args?");
	}

	// Shut-down Method
	/**
	 * Abstract for the method that shuts the connection down
	 */
	public abstract void shutdownConnection();

	// Runnable Method
	/**
	 * Abstract for the method that runs the connection thread
	 */
	@Override
	public abstract void run();
}
