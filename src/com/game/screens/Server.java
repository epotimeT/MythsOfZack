/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.game.screens;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author tfeyi
 */
public class Server extends Application {

	Stage window;
	String name;
	int ind = 0;
	Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
	double width = screenBounds.getWidth();
	double height = screenBounds.getHeight();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage)/* throws Exception */ {

		// window settings
		window = primaryStage;
		window.setTitle("Server");
		window();

	}

	public void window() {
		BorderPane layout2 = new BorderPane();
		// layout2.setTop(toppane);
		VBox midpane = new VBox(10);

		Label title = new Label("Server");
		TextField portNumberInput = new TextField();
		portNumberInput.setPromptText("Port Number");

		Button go = new Button("GO");

		VBox chatBox = new VBox(10);
		Button send = new Button("Add");
		List<Label> messages = new ArrayList<>();
		ScrollPane container = new ScrollPane();
		// container.setVbarPolicy(ScrollBarPolicy.NEVER);
		// HBox bottomPanel= new HBox(10);
		// TextField text = new TextField();
		// text.setMinSize(((width/5)-5),20);
		// bottomPanel.getChildren().addAll(text,send);

		// chatBox.setPrefSize(100, (height-300)-5);
		// chatroom.setPrefSize((width/4), (height-200));
		container.setPrefSize((width / 4), (height - 300));

		container.setContent(chatBox);

		midpane.getChildren().addAll(title, portNumberInput, go, container);

		layout2.setLeft(midpane);
		layout2.setRight(initChatBox());
		Scene scene2 = new Scene(layout2, width, height);
		scene2.getStylesheets().add(getClass().getResource("zelda.css").toExternalForm());
		window.setScene(scene2);
		window.setMaximized(true);
		window.show();
	}

	public VBox initChatBox() {
		VBox chatroom = new VBox(10);
		VBox chatBox = new VBox(10);
		Button send = new Button("Add");
		List<Label> messages = new ArrayList<>();
		ScrollPane container = new ScrollPane();
		// container.setVbarPolicy(ScrollBarPolicy.NEVER);
		HBox bottomPanel = new HBox(10);
		TextField text = new TextField();
		text.setMinSize(((width / 4) - 5), 20);
		bottomPanel.getChildren().addAll(text, send);

		// chatBox.setPrefSize(100, (height-300)-5);
		// chatroom.setPrefSize((width/4), (height-200));
		container.setPrefSize((width / 4), (height - 300));

		container.setContent(chatBox);

		chatBox.getStyleClass().add("chatbox");

		send.setOnAction(e -> {

			if (!(text.getText()).isEmpty()) {
				messages.add(new Label(("(" + name + ") " + text.getText())));

				Label label = messages.get(ind);

				label.setAlignment(Pos.CENTER_LEFT);
				label.setMaxWidth((width / 4) - 5);
				// messages.get(ind).setMinHeight(Region.USE_PREF_SIZE);
				label.setWrapText(true);
				// messages.get(ind).setMinHeight(70);
				// System.out.println("1");

				// messages.get(ind).setPrefSize((width/4),10);
				chatBox.getChildren().add(label);
				ind++;
				text.clear();

			}

		});

		text.setOnKeyPressed(e -> {

			if (e.getCode().equals(KeyCode.ENTER) && !(text.getText()).isEmpty()) {

				messages.add(new Label(("(" + name + ") " + text.getText())));

				Label label = messages.get(ind);

				label.setAlignment(Pos.CENTER_LEFT);
				label.setMaxWidth((width / 4) - 5);
				// messages.get(ind).setMinHeight(Region.USE_PREF_SIZE);
				label.setWrapText(true);
				// messages.get(ind).setMinHeight(70);
				// System.out.println("1");

				// messages.get(ind).setPrefSize((width/4),10);
				chatBox.getChildren().add(label);
				ind++;

				text.clear();
			}

		});

		chatroom.getChildren().addAll(container, bottomPanel);
		chatroom.getStylesheets().add(getClass().getResource("chatbox.css").toExternalForm());
		chatroom.setAlignment(Pos.CENTER);
		return chatroom;
	}
}
