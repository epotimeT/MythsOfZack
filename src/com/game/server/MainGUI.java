package com.game.server;

import com.game.serverScreen.ServerGUI;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * <h1>MainGUI</h1> This class runs a server through a gui (recommended)
 */
public class MainGUI extends Application {

	private static ServerGUI serverGui;
	private Stage window;

	/**
	 * Launches the gui upon being ran
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		serverGui = new ServerGUI();
		launch(args);
	}

	/**
	 * The method that's called through the launch (sets up the window)
	 * 
	 * @param primaryStage -
	 */
	@Override
	public void start(Stage primaryStage) {
		window = primaryStage;
		window.setTitle("Server");
		serverGui.window();
	}

}
