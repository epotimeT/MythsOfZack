package com.game.server;

/**
 * <h1>Main</h1> This class runs a server through a command line JavaFX is still
 * required for this to run correctly
 */
public class Main {

	private static Server gameServer;
	private static int myPort = 6868;

	/**
	 * Launches the server upon being ran
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		// Create server
		System.out.println("Start Server Main Class");
		System.out.println("The server is running on port " + myPort);

		gameServer = new Server(myPort);
		gameServer.startServer();
	}
}
