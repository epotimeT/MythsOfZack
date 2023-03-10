package com.game.server;

/**
 * <h1>Lobby</h1> This class contains the functions related to hosting the
 * lobby. It means the server can easily keep track of who has connected
 */
public class Lobby {

	private Server server;

	private String[] names;

	private int clientsConnected = 0;

	/**
	 * Creates a Lobby object
	 * 
	 * @param server - reference to the Server object
	 */
	public Lobby(Server server) {
		this.server = server;
		names = new String[Server.MAX_CLIENTS];
	}

	/**
	 * Adds a client locally and updates other clients Their name is stored locally
	 * in the String[] names and the variable that stores the number of clients is
	 * incremented, so the server stays up-to-date. Plus, all other clients are
	 * informed of the new name so the guis can update locally
	 * 
	 * @param name - string storing the name the client has chosen
	 * @param id   - int storing the id of the client, assigned by the server
	 */
	public void addClient(String name, int id) {

		// record new client locally
		names[id] = name;
		System.out.println(name);

		// update new client's gui w/ other clients' names
		String namesStr = "";
		boolean none = true;
		for (int i = 0; i < names.length - 1; i++) {
			if (names[i] != null) {
				namesStr = namesStr + "#" + i + names[i];
				none = false;
			}
		}
		if (!none) {
			namesStr = namesStr.substring(1);
			server.fwdTCPData(id, 2, namesStr);
		}

		// tell all clients that a new client has joined
		server.fwdBroadcastTCPData(3, id + name);

		// incremenet local clients counter
		incrementTotalClients();

		System.out.println(name);
	}

	/**
	 * Removes a client locally and updates other clients The client's data is
	 * removed from the attributes of this class and all clients are informed that
	 * the client has been removed (incl the removed client incase it was forcefully
	 * removed by the server)
	 * 
	 * @param id - int that stores the id of the client to be removed
	 */
	public void removeClient(int id) {
		// record removal locally
		names[id] = null;

		// tell all other clients that a client has left
		server.fwdBroadcastTCPData(4, String.valueOf(id));

		// decremenet local clients counter
		decrementTotalClients();
	}

	// ClientsConnected Methods
	/**
	 * Increments the clientsConnected attribute
	 */
	private void incrementTotalClients() {
		clientsConnected++;
	}

	/**
	 * Decrements the clientsConnected attribute
	 */
	private void decrementTotalClients() {
		clientsConnected--;
	}

	// Getters and Setters
	/**
	 * Gets the number of clients connected to the server
	 * 
	 * @return clientsConnected - int - total num of clients
	 */
	public int getTotalClients() {
		return clientsConnected;
	}

	/**
	 * Gets the list of names of all the clients connected to the server
	 * 
	 * @return names - String[] - array of client names
	 */
	public String[] getClientNames() {
		return names;
	}

}
