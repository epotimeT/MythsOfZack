package com.game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.game.serverScreen.ServerGUI;
import com.game.utilities.Semaphore;

/**
 * <h1>Server</h1> This class creates and initialises the server, as well as all
 * other objects related to the server that are needed for it to function. It
 * also manages searching for new clients and initialising their connections
 */
public class Server implements Runnable { // runnable needed to support threading

	/**
	 * Declares the number of clients allowed on the server (Will allow for easier
	 * upscaling in the future)
	 */
	public static final int MAX_CLIENTS = 4;

	/**
	 * Reference to the UDPConnection object
	 */
	public UDPConnection serverUDPConnection;

	/**
	 * Represents if the UDP thread is currently active
	 */
	public boolean serverUDPConnected = false;

	/**
	 * Reference to the ServerGUI object
	 */
	public ServerGUI gui;

	private EventDecipherer decipherer;
	private Lobby lobby;
	private Semaphore semReceivedName = new Semaphore(5000);
	private Semaphore semThisStarted = new Semaphore(5000);
	private ServerSocket serverSocket;
	private TCPConnection[] serverTCPClients = new TCPConnection[MAX_CLIENTS];

	private boolean withGui;
	private boolean runningSearch = true; // true - lobby running, false - game running
	private boolean initialized = false; // error-checking on socket
	private boolean shuttingDown = false; // error-checking on shutdown
	private boolean[] removingClientBools = new boolean[MAX_CLIENTS];
	private boolean[] pressedStartBools = new boolean[MAX_CLIENTS];

	private int idCount = 0; // number of clients connected
	private int[] idList = { 0, 0, 0, 0 }; // list of free client ids (the indexes)
	private int port; // port number of the server

	// Constructor w/ gui
	/**
	 * Creates a server that outputs to the server gui
	 * 
	 * @param portNum - port number that has been chosen
	 * @param guiRef  - reference to the ServerGui object
	 */
	public Server(int portNum, ServerGUI guiRef) {
		System.out.println("creating server w/ gui");
		withGui = true;
		port = portNum;
		gui = guiRef;

		decipherer = new EventDecipherer(this, semReceivedName, withGui);
		this.lobby = new Lobby(this);

		// Fill removing clients boolean array (ensures removeClient only ran once)
		// Also fill array of who has pressed start (so game begins only when everyone
		// is ready)
		for (int i = 0; i < MAX_CLIENTS; i++) {
			removingClientBools[i] = true;
			pressedStartBools[i] = false;
		}

		try {
			serverSocket = new ServerSocket(port);
			initialized = true; // constructor has correctly executed

		} catch (IOException e) {
			System.out.println("IOException - Server Constructor");
			// do nothing here - error already caught (initialized=false)
		} catch (IllegalArgumentException e) {
			System.out.println("The port number must be between 0 and 65535 inclusive");
		} catch (SecurityException e) {
			System.out.println("security");
		}
	}

	// Constructor w/out gui
	/**
	 * Creates a server that doesn not interact with a gui
	 * 
	 * @param portNum - port number that has been chosen
	 */
	public Server(int portNum) {
		System.out.println("creating server w/out gui");
		port = portNum;
		withGui = false;
		decipherer = new EventDecipherer(this, semReceivedName, withGui);
		this.lobby = new Lobby(this);

		// Fill removing clients boolean array
		for (int i = 0; i < MAX_CLIENTS; i++) {
			removingClientBools[i] = false;
		}

		try {
			serverSocket = new ServerSocket(port);
			initialized = true; // constructor has correctly executed

		} catch (IOException e) {
			System.out.println("IOException - Server Constructor");
			// do nothing here - error already caught (initialized=false)
		} catch (IllegalArgumentException e) {
			System.out.println("The port number must be between 0 and 65535 inclusive");
		} catch (SecurityException e) {
			System.out.println("security");
		}
	}

	// Start-up Method
	/**
	 * Initialises the thread that searches for clients If there has been an error
	 * in the creation of the socket, an error msg will be printed and the server
	 * will not start
	 */
	public void startServer() {
		System.out.println("starting server");
		if (initialized) {
			if (withGui) {
				gui.setServerInit(true);
				gui.addList("Server Starting on Port " + port + "...");
			} else {
				System.out.println("Server Starting on Port" + port);
			}

			// Start shutdown thread - incase server is shut unexpectedly
			shutdownHook();

			// Start listening for UDP
			Semaphore semUDPStarted = new Semaphore(5000);
			serverUDPConnection = new UDPConnection(port, decipherer, this, semUDPStarted);
			Thread udp = new Thread(serverUDPConnection);
			udp.setDaemon(true); // so that when the server closes, so does the thread
			udp.start();

			// Start listening for new clients
			Thread serverThread = new Thread(this);
			if (withGui) {
				serverThread.setDaemon(true);
			}
			serverThread.start();

			// Ensure this thread and UDP thread have started TODO
			boolean passed = semThisStarted.release();
			boolean passedTwo = semUDPStarted.release();

			System.out.println("passed is: " + passed);
			// Check whether a timeout has occurred
			if (passed == false) {
				if (withGui) {
					gui.addList("The server thread failed to start");
				}
				shutdownServer();
				return;
			} else if (passedTwo == false) {
				if (withGui) {
					gui.addList("The UDP thread failed to start");
				}
				shutdownServer();
				return;
			}

			if (withGui) {
				gui.addList("The server has started successfully");
			}

		} else {
			if (withGui) {
				gui.addList("ServerSocket Failed - Please try a different port");
				gui.setServerInit(false);
			}
		}
	}

	// Shut-down Method
	/**
	 * Shuts down all processes related to the server The specific TCP and UDP
	 * shut-down methods are called and then the server socket is closed. All
	 * threads are daemons to the server thread so only that must be stopped.
	 */
	public void shutdownServer() {
		if (shuttingDown == false) { // so it is not accidentally ran multiple times
			shuttingDown = true;
			runningSearch = false; // cause server thread to end

			for (int i = 0; i < MAX_CLIENTS; i++) {
				if (serverTCPClients[i] != null) {
					serverTCPClients[i].shutdownConnection(); // shutdown TCPs
				}
			}

			if (serverUDPConnection != null) {
				serverUDPConnection.shutdownConnection(); // shutdown UDP
			}

			try {
				serverSocket.close(); // close socket
				if (withGui) {
					gui.addList("Server has shutdown");
				} else {
					System.out.println("Server has shutdown");
				}
			} catch (IOException e) {
				System.out.println("IOException - Server Shutdown");
			}

		}
	}

	// Shut-down Hook (for when JVM is forced to close - ensures the sockets are
	// shut)
	/**
	 * Ensures shut-down is ran if server closed forcefully The run function is
	 * triggered when the server is shut, so that if the user uses the cmd or if
	 * they do not use the 'stop' button, the sockets are still properly closed
	 */
	private void shutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdownServer();
				System.out.println("Server has shutdown");
			}
		});
	}

	// Remove Client Method
	/**
	 * Removes a client from the server If a client has disconnected or if the
	 * server needs to remove them, this function ensures their TCP thread is
	 * stopped and all variables that include them are corrected
	 * 
	 * @param id - integer that represent's the client's id on the server
	 */
	public void removeClient(int id) {
		if (removingClientBools[id] == false) {
			removingClientBools[id] = true;

			if (serverTCPClients[id] != null) {
				serverTCPClients[id].shutdownConnection();
				serverTCPClients[id] = null;
			}
			if (pressedStartBools[id] == true) {
				decipherer.decrStartNum();
				pressedStartBools[id] = false;
			}

			idCount = id - 1;
			idList[id] = 0; // this id can now be used again
			lobby.removeClient(id);

			if (withGui) {
				gui.addList("Client has left with id " + id + lobby.getTotalClients());
			}
		}
	}

	// Create Communication Connection Method
	/**
	 * Sets up the TCP and UDP connection for the new client Does all the validation
	 * to ensure there is space for the client before creating the TCP listener
	 * thread and ensuring it is able to receive UDP packets
	 * 
	 * @param socket - socket that client can use to communicate through
	 */
	private void initSocket(Socket socket) {
		if (idCount < MAX_CLIENTS) {
			// Find First Empty ID in List
			int id = -1;
			for (int i = 0; i < MAX_CLIENTS; i++) {
				if (idList[i] == 0) {
					id = i;
					idList[i] = 1;
					idCount++;
					break;
				}
			}
			removingClientBools[id] = false;

			System.out.println("New client id: " + id);

			// Start TCP Connection
			Semaphore semTCPStarted = new Semaphore(5000);

			serverTCPClients[id] = new TCPConnection(socket, id, decipherer, this, semTCPStarted); // sem2
			Thread tcp = new Thread(serverTCPClients[id]);
			tcp.setDaemon(true); // so that when the server closes, so does the thread
			tcp.start();

			// Wait for TCP to start
			boolean passed = semTCPStarted.release();

			// Check for a timeout
			if (passed == false) {
				if (withGui) {
					gui.addList("The TCP thread has failed to start for a new client");
				}
				removeClient(id);
				return;
			}

			// Tell Client its id number via TCP + Ensure UDP is working
			// boolean UDPReady = true;
			boolean UDPReady = initUDPConnection(id);

			if (UDPReady == false) {
				// Disconnect the client - no UDP was received
				removeClient(id);
				if (withGui) {
					gui.addList("A client has been refused for slow connection");
				}
				System.out.println("Connection refused");
			} else {
				// Update all clients' lobbies w/ new client info
				serverTCPClients[id].sendData(1, " ");

				// Wait for name to be sent
				boolean passedTwo = semReceivedName.release();

				// Check for timeout
				if (passedTwo == false) {
					if (withGui) {
						gui.addList("The client's name was never received");
					}
					removeClient(id);
					return;
				}

				// Update values
				int totalClients = lobby.getTotalClients();
				if (withGui) {
					gui.addList("Client has joined with id " + id + totalClients);
				}
			}
		} else {
			System.out.println("Error - Max Clients Reached");
		}
	}

	// Connect UDP Method
	/**
	 * Sets up the UDP connection In order to accept a client, the server must
	 * receive a UDP packet from said client so it can access its IP address and
	 * port number so it can send back data during the game. This function requests
	 * a UDP packet
	 * 
	 * @param id - integer that represents the client's id on the server
	 * @return - boolean that represents whether the connection was successful
	 */
	private boolean initUDPConnection(int id) {
		serverUDPConnected = false;
		for (int i = 0; i < 3; i++) {

			String idStr = String.valueOf(id);
			serverTCPClients[id].sendData(0, idStr);

			try {
				Thread.sleep(1000); // Wait 500ms
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}

			if (serverUDPConnected == false) {
				// No package received, try again
			} else {
				serverUDPConnected = false;
				return true;
			}
		}
		// timeout connection - evict client
		return false;
	}

	// Main Thread Method
	/**
	 * Waits for new clients (as a thread) This function waits until a client wants
	 * to connect and then calls the function to set up the new connection
	 */
	@Override
	public void run() {
		runningSearch = true; // start search for new connections;
		semThisStarted.take(); // notify that this thread has started

		// TODO - STOP THIS ONCE GAME BEGINS
		while (runningSearch) { // server continually looks for new players
			try {
				System.out.println("Waiting for a new client");
				Socket socket = serverSocket.accept(); // wait for Client to connect
				initSocket(socket); // set up Connection for communication

			} catch (IOException e) {
				System.out.println("IOException - Server Running");
			}
		}
	}

	// TCP Commands
	/**
	 * Forwards data to be sent via TCP
	 * 
	 * @param actionId - int that tells the client what action to do
	 * @param data     - string that contains any other info needed to be sent
	 */
	public void fwdTCPData(int clientId, int actionId, String data) {
		if (serverTCPClients[clientId] != null) {
			serverTCPClients[clientId].sendData(actionId, data);
		}
	}

	/**
	 * Forwards data to be sent to all clients via TCP
	 * 
	 * @param actionId - int that tells the client what action to do
	 * @param data     - string that contains any other info needed to be sent
	 */
	public void fwdBroadcastTCPData(int actionId, String data) {
		TCPConnection.broadcastData(actionId, data, serverTCPClients);
	}

	/**
	 * Forwards data to be sent to all clients via TCP except one
	 * 
	 * @param exceptionId - int of client that doesn't need the data
	 * @param actionId    - int that tells the client what action to do
	 * @param data        - string that contains any other info needed to be sent
	 */
	public void fwdBroadcastExcClientTCPData(int exceptionId, int actionId, String data) {
		TCPConnection.broadcastDataExceptClient(actionId, data, exceptionId, serverTCPClients);
	}

	// Getters and Setters
	/**
	 * Sets a newly joined client's name in all variables This function tells the
	 * lobby a client has joined
	 * 
	 * @param id   - int that represents the assigned id of the client
	 * @param name - string that represents the client-chosen name to be set
	 */
	public void setClientName(int id, String name) {
		if (lobby != null) {
			lobby.addClient(name, id);
			System.out.println("Client " + id + " has name: " + name);
		}
	}

	/**
	 * Sets whether the UDP is connected or not
	 * 
	 * @param state - boolean (connected/not)
	 */
	public void setServerUDPConnected(boolean state) {
		serverUDPConnected = state;
	}

	/**
	 * Sets whether the server should be searching for new clients
	 * 
	 * @param state - boolean (search/stop)
	 */
	public void setRunningSearch(boolean state) {
		runningSearch = state;
	}

	/**
	 * Sets whether a client has chosen to start the game
	 * 
	 * @param id    - int that represents the id of the client
	 * @param state - boolean (start/wait)
	 */
	public void setPressedStartBools(int id, boolean state) {
		pressedStartBools[id] = state;
	}

	/**
	 * Gets whether the UDP connection is connected or not
	 * 
	 * @return serverUDPConnected - boolean of whether UDP is working
	 */
	public boolean getServerUDPConnected() {
		return serverUDPConnected;
	}

	/**
	 * Gets the list of client id slots
	 * 
	 * @return idList - int[] of which ids are free/used
	 */
	public int[] getIdList() {
		return idList;
	}

	/**
	 * Gets the number of clients currently connected
	 * 
	 * @return idCount - int that stores the number
	 */
	public int getIdCount() {
		return idCount;
	}

	/**
	 * Gives direct access to client TCP connections for sending data.
	 * 
	 * @return serverTcpClients - array of connected clients.
	 */
	public TCPConnection[] getTCPConnections() {
		return serverTCPClients;
	}

	/**
	 * Returns the list of names of clients who are connected to the server
	 * 
	 * @return clientNames from lobby if lobby exits, null otherwise
	 */
	public String[] getClientNames() {
		if (lobby != null) {
			return lobby.getClientNames();
		}
		return null;
	}

}
