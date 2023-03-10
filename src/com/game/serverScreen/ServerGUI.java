package com.game.serverScreen;

import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.game.server.Server;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * <h1>ServerGUI</h1> This class creates and loads the GUI used to start a
 * server with an interface, allowing for easier use and for keeping track of
 * key events on the server, such as clients joining and leaving
 */
public class ServerGUI {

	// Screen Details
	private static Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
	private static double width = screenBounds.getWidth();
	private static double height = screenBounds.getHeight();

	private static String name = "host";
	private static Server server;

	private static Label attClients = new Label(" Clients: 0");
	private static Label attPort = new Label("");
	private static LinkedList<String> consoleList = new LinkedList<String>();
	private static ReadWriteLock messagesLock = new ReentrantReadWriteLock();
	private static ScrollPane container = new ScrollPane();
	private static VBox attributesBox = new VBox(10);
	private static VBox consoleBox = new VBox(10);

	private static boolean autoStart;
	private static boolean consoleThread = true;
	private static boolean firstRun = true;
	private static boolean serverInit = false;
	private static boolean showWindow = true;

	private int port;

	/**
	 * Creates a ServerGUI object if the player loads the gui separately
	 */
	public ServerGUI() {
		autoStart = false;
		showWindow = true;
	}

	/**
	 * Creates a ServerGUI object if the player loads the gui through the host game
	 * screen (automatically starts a server)
	 * 
	 * @param port - the port number the player wants to start the server on
	 */
	public ServerGUI(int port) {
		this.port = port;
		autoStart = true;
		showWindow = true;
	}

	/**
	 * All the details to create the gui
	 * 
	 * @return whether the port chosen through host game is valid (ignored if called
	 *         through the server gui)
	 */
	public boolean window() {
		// Create Window
		Stage window = new Stage();

		// Set-up Main Layout
		BorderPane layout2 = new BorderPane();

		// Set-up Left Layout
		VBox midpane = new VBox(10);

		Label title = new Label("Server"); // server title

		TextField portNumberInput = new TextField(); // port number input
		portNumberInput.setPromptText("Port Number");

		Button go = new Button("GO"); // run server button
		Button stop = new Button("STOP"); // run server button
		stop.setDisable(true);

		consoleThread = true;
		consoleUpdatesThread(); // start console thread

		go.setOnAction(e -> {
			try {
				// Stop inputs from being accessed
				go.setDisable(true);
				portNumberInput.setDisable(true);

				// Create server object
				port = Integer.parseInt(portNumberInput.getText());
				server = new Server(port, this);

				// Attempt to start server
				server.startServer();

				if (!serverInit) {
					// Server hasn't started so allow user to try again
					go.setDisable(false);
					portNumberInput.setDisable(false);
				} else {
					// Server has started, set-up attributes
					attPort.setText(" Port Number: " + port);
					if (firstRun) {
						firstRun = false;
						addLblToAttributes(attPort);
						addLblToAttributes(attClients);
					}
					portNumberInput.setText("");

					// Allow user to stop server
					stop.setDisable(false);
				}

			} catch (NumberFormatException ex) {
				// Port number is not an int (too big or has non-number characters)
				addList("Please enter a valid integer for the port number. It should be a number between 0 and 65535 inclusive.");

				// Allow user to try again
				go.setDisable(false);
				portNumberInput.setDisable(false);

			}
		});

		stop.setOnAction(e -> {
			stop.setDisable(true);
			server.shutdownServer();
			attPort.setText(" Port Number: n/a");
			go.setDisable(false);
			portNumberInput.setDisable(false);
		});

		// Set-up Buttons HBox
		HBox btnsContainer = new HBox(25);
		btnsContainer.getChildren().addAll(go, stop);

		// Set-up Attributes Box (container set-up here)
		ScrollPane container = new ScrollPane(); // attributes box
		container.setPrefSize((width / 3), (height - 300)); // TODO
		container.setContent(attributesBox);

		// Get All Layouts/Items
		midpane.getChildren().addAll(title, portNumberInput, btnsContainer, container);
		layout2.setLeft(midpane);
		layout2.setRight(initConsoleBox());

		// Add All to Scene
		Scene sceneOne = new Scene(layout2, width, height);
		sceneOne.getStylesheets().add(getClass().getResource("zelda.css").toExternalForm());

		// Automatically Create Server If Ran Through 'Host Game'
		if (autoStart) {
			autoStart = false;

			// Turn off inputs
			go.setDisable(true);
			portNumberInput.setDisable(true);

			// Create server object
			server = new Server(port, this);

			// Attempt to start server
			server.startServer();
			if (!serverInit) {
				// Server hasn't started so don't show window
				showWindow = false;
			} else {
				// Server has started, set-up attributes
				attPort.setText(" Port Number: " + port);
				if (firstRun) {
					firstRun = false;
					addLblToAttributes(attPort);
					addLblToAttributes(attClients);
				}
				portNumberInput.setText("");

				// Allow user to stop server
				stop.setDisable(false);
			}
		}

		if (showWindow) {
			window.setScene(sceneOne);
			window.setMaximized(true);
			window.show();
			return true;
		}
		return false;
	}

	// Create Consolebox Method (console)
	/**
	 * Creates everything related to the console
	 * 
	 * @return VBox - the box with all the console features
	 */
	private VBox initConsoleBox() {
		// Init Components
		VBox consoleRoom = new VBox(10);
		Button send = new Button("Add");
		HBox bottomPanel = new HBox(10);
		TextField text = new TextField();

		// Set Components
		text.setMinSize(((width / 3) - 5), 20); // chatbox output area TODO

		bottomPanel.getChildren().addAll(text, send); // chatbox input area

		container.setPrefSize((width / 3), (height - 300)); // chatbox outer box TODO
		container.setContent(consoleBox);

		consoleBox.getStyleClass().add("chatbox");

		// Button Action
		send.setOnAction(e -> { // send entered message
			if (!(text.getText()).isEmpty()) {

				// consoleList.add( new Label( ( "(" + name + ") " + text.getText() ) ) );

				messagesLock.writeLock().lock();
				try {
					consoleList.add("(" + name + ") " + text.getText());
				} finally {
					messagesLock.writeLock().unlock();
				}

				/*
				 * Label label = messages.get(ind); //make message a label
				 * 
				 * label.setAlignment(Pos.CENTER_LEFT); //set attributes
				 * label.setMaxWidth((width/3)-5); //TODO label.setWrapText(true);
				 * 
				 * consoleBox.getChildren().add(label); //add to VBox
				 * 
				 * ind++;
				 */
				text.clear(); // clear inputfield

			}
		});

		// Final Code (add to layout + get CSS)
		consoleRoom.getChildren().addAll(container, bottomPanel);
		consoleRoom.getStylesheets().add(getClass().getResource("chatbox.css").toExternalForm());
		consoleRoom.setAlignment(Pos.CENTER);
		return consoleRoom;
	}

	// Console LinkedList Commands
	/**
	 * Adds a message to the list of messages to print to the console
	 * 
	 * @param msg - the message to add
	 */
	public void addList(String msg) {
		messagesLock.writeLock().lock();
		try {
			// System.out.println("adding to console list = "+msg);
			consoleList.add("(console) " + msg);
		} finally {
			messagesLock.writeLock().unlock();
		}
	}

	// GUI Additions Commands
	/**
	 * Prints a label containing a message to the console
	 * 
	 * @param lbl - the label w/ the message
	 */
	private void addLblToConsole(Label lbl) {
		// Add label to console
		lbl.setAlignment(Pos.CENTER_LEFT); // set attributes
		lbl.setMaxWidth((width / 3) - 5); // TODO
		lbl.setWrapText(true);
		consoleBox.getChildren().add(lbl);

		container.layout();

		container.setVvalue(1.0);

		// Configure console (scrollbox) to be at bottom TODO
		// container.setVvalue(1.0);
	}

	/**
	 * Prints a label to the attributes box
	 * 
	 * @param lbl - the label w/ the attribute
	 */
	private void addLblToAttributes(Label lbl) {
		lbl.setAlignment(Pos.CENTER_LEFT);
		lbl.setTextFill(Color.color(0, 0, 0));
		attributesBox.getChildren().add(lbl);
	}

	/**
	 * Updates the clients attribute label
	 * 
	 * @param clients - the new number of clients connected
	 */
	private void editClientsAttLbl(int clients) {
		attClients.setText(" Clients: " + clients);
	}

	// Getter/Setter Commands
	/**
	 * Sets boolean that shows whether the server has been initialised or not
	 * 
	 * @param status - true for initialised, false for not
	 */
	public void setServerInit(boolean status) {
		serverInit = status;
	}

	// Thread to Update Console
	/**
	 * Thread that periodically checks to see if any new messages need to be output
	 * to the console
	 */
	public void consoleUpdatesThread() {
		System.out.println("consoleUpdatesThread started");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Runnable updater = new Runnable() {
					@Override
					public void run() { // gui updates here
						// Get messages in list + then clear them
						int tempArrayLen = 0;
						String[] tempArray = new String[0];
						messagesLock.writeLock().lock();
						try {
							tempArrayLen = consoleList.size();
							tempArray = new String[tempArrayLen];
							for (int i = 0; i < tempArrayLen; i++) {
								tempArray[i] = consoleList.get(i);
							}
							consoleList.clear();
						} finally { // ensures the lock is always released
							messagesLock.writeLock().unlock();
						}

						for (int i = 0; i < tempArrayLen; i++) {
							String msg = tempArray[i];
							msg = checkUpdateClients(msg);
							Label lblMsg = new Label(msg);
							addLblToConsole(lblMsg);
						}

					}
				};

				while (consoleThread) { // actual thread starts here
					try {
						Thread.sleep(1000); // check every 1 second
					} catch (InterruptedException e) {
						System.out.println("InterruptedException - GUI ConsoleUpdates Thread");
					}

					if (!consoleList.isEmpty()) {
						Platform.runLater(updater);
					}
				}
				System.out.println("consoleUpdatesThread stopped");
			}
		});

		thread.setDaemon(true); // so it doesn't prevent JVM shutdown
		thread.start();
	}

	// Update Console Thread Sub-Methods
	/**
	 * Checks to see if any of the messages trigger an update to an attribute
	 * 
	 * @param msg - the message to be checked
	 * @return - the message, edited if needed
	 */
	private String checkUpdateClients(String msg) {
		if (msg.startsWith("(console) Client has joined")) {
			int msgId = Integer.parseInt(msg.substring(msg.length() - 1, msg.length()));
			editClientsAttLbl(msgId);
			msg = msg.substring(0, msg.length() - 1);
		} else if (msg.startsWith("(console) Client has left")) {
			int msgId = Integer.parseInt(msg.substring(msg.length() - 1, msg.length()));
			editClientsAttLbl(msgId);
			msg = msg.substring(0, msg.length() - 1);
		}
		return msg;
	}

}
