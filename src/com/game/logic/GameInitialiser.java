package com.game.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.game.graphics.Renderer;
import com.game.main.Client;
import com.game.screens.GUI;
import com.game.sound.BackgroundMusic;
import com.game.sound.SfxController;
import com.game.utilities.Vector;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GameInitialiser {
        /**
         * Volume controls
         */
        boolean sound = true;
	boolean musicactive = true;
        int musicVolume=10;
        int soundfxVolume=100;
        /**
         * Stage the game canvas is displays on
         */
        Stage window;
    
	/***
	 * A constructor for the GameInitialiser class. Creates the bare bones of a
	 * screen and starts the game renderer and game loop.
	 * 
	 * @param window        - The stage to display the game
	 * @param gameScene     - The scene to display the game
	 * @param networkClient - The client that calls the constructor
	 */

	public GameInitialiser(Stage window, Scene gameScene, Client networkClient) {
                this.window=window;
		Group root = new Group();
		Scene scene = new Scene(root);

		ImageView view1 = new ImageView(loadImage("settings/0"));
		ImageView view2 = new ImageView(loadImage("settings/1"));
		Button settings = new Button();
		settings.setPrefSize(5, 5);
		settings.setGraphic(view1);

		settings.setStyle(
				"-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");

		settings.setPadding(Insets.EMPTY);

		settings.setOnMouseEntered(e -> {
			settings.setGraphic(view2);
		});
		settings.setOnMouseExited(e -> {
			settings.setGraphic(view1);
		});
		settings.setOnAction(e -> {
			gamesettings();
		});
		HBox toppane = new HBox();

		toppane.setAlignment(Pos.TOP_RIGHT);
		toppane.setStyle("-fx-background-color: black;");
		Canvas canvas = new Canvas(1200, 700);
		Canvas ui = new Canvas(1100, 100);
		Vector canvasSize = new Vector(1200, 700);

		BorderPane border = new BorderPane();
		border.setTop(toppane);
		toppane.getChildren().addAll(ui, settings);
		// border.setBottom(ui);
		border.setCenter(canvas);
		border.setStyle("-fx-background-color: #ffd8a0");

		root.getChildren().add(border);

		Renderer gameRenderer = new Renderer(canvas);
		Renderer uiRenderer = new Renderer(ui);
		uiRenderer.setShouldClampSprites(true);
		uiRenderer.start();
		gameRenderer.start();

		// gameRenderer.setShouldRenderHitboxes(true);

		InputHandler inputObject = new InputHandler(networkClient);
		AnimationTimer gameLoop = new Gameloop(canvas, networkClient, inputObject, gameRenderer, uiRenderer, window);

		gameLoop.start();
		window.setScene(scene);
	}

	public Image loadImage(String name) {
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/" + name + ".png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Image(inputstream, 70, 70, true, true);
	}

	public void gamesettings() {
		ToggleButton soundfx = new ToggleButton("Sound FX");
		ToggleButton music = new ToggleButton("Music");
		Slider musicSlider = new Slider(0, 100, 10);
		Slider soundfxSlider = new Slider(0, 100, 10);
		musicSlider.setPrefWidth(300);
		soundfxSlider.setPrefWidth(300);
		musicSlider.setBlockIncrement(1);
		soundfxSlider.setBlockIncrement(1);

		musicSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

				musicVolume=newValue.intValue();
				BackgroundMusic.setMusicVolume(newValue.intValue());
			}
		});
		musicSlider.styleProperty().bind(Bindings.createStringBinding(() -> {
			double percentage = (musicSlider.getValue() - musicSlider.getMin())
					/ (musicSlider.getMax() - musicSlider.getMin()) * 100.0;
			return String.format(
					"-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
							+ "-slider-filled-track-color %f%%, -fx-base %f%%, -fx-base 100%%);",
					percentage, percentage);
		}, musicSlider.valueProperty(), musicSlider.minProperty(), musicSlider.maxProperty()));

		soundfxSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

				soundfxVolume=newValue.intValue();
				SfxController.setVolume(newValue.intValue());
			}
		});
		soundfxSlider.styleProperty().bind(Bindings.createStringBinding(() -> {
			double percentage = (soundfxSlider.getValue() - soundfxSlider.getMin())
					/ (soundfxSlider.getMax() - soundfxSlider.getMin()) * 100.0;
			return String.format(
					"-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
							+ "-slider-filled-track-color %f%%, -fx-base %f%%, -fx-base 100%%);",
					percentage, percentage);
		}, soundfxSlider.valueProperty(), soundfxSlider.minProperty(), soundfxSlider.maxProperty()));

		soundfx.setOnAction(e -> {

			SfxController.toggleSound();
			if (sound) {
				soundfxSlider.setDisable(true);
				sound = false;
			} else {
				soundfxSlider.setDisable(false);
				sound = true;
			}

		});

		music.setOnAction(e -> {

			BackgroundMusic.toggleSound();
			if (musicactive) {
				musicSlider.setDisable(true);
				musicactive = false;
			} else {
				musicSlider.setDisable(false);
				musicactive = true;
			}

		});
		VBox bottom = new VBox(30);
		VBox buttons = new VBox(15);
		VBox sliders = new VBox(30);
		HBox layout = new HBox(30);
		Button quit = new Button("QUIT");
		quit.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(buttons, sliders);
		bottom.getChildren().addAll(layout, quit);
		buttons.getChildren().addAll(soundfx, music);
		sliders.getChildren().addAll(soundfxSlider, musicSlider);
		bottom.setAlignment(Pos.CENTER);
		layout.setAlignment(Pos.CENTER);
		sliders.setAlignment(Pos.CENTER);
		buttons.setAlignment(Pos.CENTER);

		Stage popupStage = new Stage(StageStyle.TRANSPARENT);

                quit.setOnAction(e -> {
			popupStage.hide();
			GUI gui = new GUI();
			gui.mainMenu(window);
		});

		Button close = new Button("Close");
		close.setOnAction(e -> popupStage.hide());
		HBox toppane = new HBox(20);
		toppane.getChildren().addAll(close);
		toppane.setAlignment(Pos.TOP_RIGHT);

		BorderPane layout1 = new BorderPane();
		layout1.setTop(toppane);
		layout1.setCenter(bottom);

		Scene scene4 = new Scene(layout1, 500, 300);

		scene4.getStylesheets().add(getClass().getResource("settings.css").toExternalForm());

		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setScene(scene4);
		popupStage.showAndWait();

	}

}
