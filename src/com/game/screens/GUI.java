
package com.game.screens;

import javafx.application.Application;
import javafx.application.Platform;
import com.game.main.Client;
import com.game.logic.GameInitialiser;
import com.game.sound.BackgroundMusic;
import com.game.sound.MenuSFX;
import com.game.serverScreen.ServerGUI;
import com.game.singleplayer.GameStarter;
import com.game.sound.SfxController;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.scene.control.Slider;
import javafx.stage.*;
import javafx.scene.input.KeyCode;
import java.util.*; 
import static javafx.application.Application.launch;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *  Graphical User Interface
 *  Contains all the sceness in the GUI
 *  @author tof831
 */
public class GUI extends Application{
        /**
         *  The Javafx stage onto which the entire game is drawn
         */
	Stage window = new Stage();
        /**
         * All the scene in the game
         */
	public Scene scene1,scene2,scene3,scene4,scene5,scene6,gameScene,previousScene;
        /**
         *  main menu buttons
         */
	Button findGame,settings;
        /**
         * screen parameters
         */
	final double width = 1200;
	final double height = 800;
        
        /**
         * host game and port-number input variables
         */
	int portNumber;
	String ipAddress,name;
        
        /**
         * volumes
         */
        int musicVolume=10;
        int soundfxVolume=100;
        boolean sound=true;
	boolean musicactive= true;
        
	public static MenuSFX sfx = new MenuSFX();
	private Client myself;
        
        /**
         *  lobby players display variables
         */
	private Label[] playersNames = new Label[4];
	private VBox[] playersBoxes = new VBox[4];
	GridPane playersGridP;
	private boolean gameStarted = false;
	
	/**
         * thread booleans
         */
	boolean returnToMenuThreadRunning = true;
	boolean checkForClientsThreadRunning = true;
	boolean checkForStartThreadRunning = true;
	boolean checkForMessagesThreadRunning = true;
        /**
         * sliders for the sound settings
         */
	Slider musicSlider, soundfxSlider;
        /**
         * lobby chat box variable
         */
	String[] messages;
	VBox chatroom =new VBox(10);
	VBox chatBox = new VBox(10);
	Button send = new Button("Add");
	ScrollPane container = new ScrollPane();
	TextField text = new TextField();
        /**
         * error message labels
         */
	Label nameerrormessage = new Label();
	Label porterrormessage = new Label();
	Label iperrormessage = new Label();

        /**
         * launches game
         **/
	public static void main(String[] args){
		launch(args);
	}

        /**
         * Initialises window
         * Start music
         * Displays the main menu
         * @param primaryStage is the stage every other element is placed on
         **/
	@Override
	public void start (Stage primaryStage){
		window = primaryStage;
		window.setTitle("Myths of Zack");
                window.setWidth(width);
                window.setHeight(height);
                window.setResizable(false);
                window.sizeToScene();
		window.setOnCloseRequest(e -> {
			e.consume();
			closeProgram();
		});
		mainMenu(window);
                BackgroundMusic.changeTrack(0);
	}

        /**
         * Loads sound effects from MenuSFX
         * @param button is the button the sound-effect is placed on
         **/    
	public void soundFX(Button button){
		button.setOnMouseEntered(e ->{ sfx.loadTrack(0);}  );
		EventHandler<ActionEvent> current = button.getOnAction();
		button.setOnAction(e -> {
			sfx.loadTrack(1);
			current.handle(e);  
		});
	}

        /**
          * Loads main menu
          * @param window is the stage where the the main menu is placed on
         **/
	public void mainMenu(Stage window){
            this.window =window;
            ImageView logo = new ImageView(loadImage("logo",650,300));
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(1,1,1,1) );
            grid.setVgap(1);
            grid.setHgap(10);
            GridPane.setConstraints(logo, 0, 5);

    		Button singlePlayer = new Button("SINGLEPLAYER");
    		singlePlayer.setOnAction(e -> {
    			singlePlayer();
    		});
                soundFX(singlePlayer);


            Button findGame = new Button("FIND GAME");           
            findGame.setOnAction(e -> {
                    findGame();
            });
            soundFX(findGame);

            Button settings = new Button("SETTINGS");
            settings .setOnAction(e -> {
                    settings();
            } );
            soundFX(settings);

            Button exit = new Button("EXIT");
            soundFX(exit);
            exit.setOnAction(e -> {
                    closeProgram();
            });

            VBox layout1 = new VBox (20);
            layout1.getChildren().addAll(singlePlayer,findGame,settings,exit);
            layout1.setAlignment(Pos.CENTER);
            GridPane.setConstraints(layout1, 0, 30);
            grid.getChildren().addAll(logo, layout1);
            grid.setAlignment(Pos.CENTER);
            scene1 = new Scene(grid,width,height);
            scene1.getStylesheets().add(getClass().getResource("zelda.css").toExternalForm());
            window.setScene(scene1);
            window.show(); 
	}
        
        /**
         * Begins single player mode
         **/
	private void singlePlayer() {	
		GameStarter gameInit = new GameStarter(window);
	}

        /**
         * Displays the find game screen 
         **/
	public void findGame(){

		Button back = new Button("Back");             
		back.setOnAction(e -> window.setScene(scene1));
		soundFX(back);
		HBox toppane = new HBox (20);
		toppane.getChildren().addAll(back);
		toppane.setAlignment(Pos.TOP_RIGHT);

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);
		grid.setHgap(10);

		TextField portNumberInput = new TextField();
		portNumberInput.setPromptText("Port Number e.g. 6868");
		GridPane.setConstraints(portNumberInput, 1, 0);

		TextField ipAddressInput = new TextField();
		ipAddressInput.setPromptText("Host's IP Address");
		GridPane.setConstraints(ipAddressInput, 1, 2);

		TextField nameInput = new TextField();
		nameInput.setPromptText("Name");
		GridPane.setConstraints(nameInput, 1, 4);

		Button join = new Button("Join");
                join.setDisable(true);
                
		GridPane.setConstraints(join, 1, 6);

		ipAddressInput.setOnKeyReleased(event -> {

                    validate(ipAddressInput,2,grid,iperrormessage); 
                    if(isInt(portNumberInput.getText())==true && !portNumberInput.getText().isEmpty() && !nameInput.getText().isEmpty() && !ipAddressInput.getText().isEmpty() && portNumberInput.getText().length()<16 && nameInput.getText().length()<16 && ipAddressInput.getText().length()<16 && nameInput.getText().trim().length() !=0 && ipAddressInput.getText().trim().length() !=0){
                        join.setDisable(false);
                    }
                    else{
                        join.setDisable(true);
                    }
			if(event.getCode().equals(KeyCode.ENTER)){
				nameInput.requestFocus();
			}        
		});

		portNumberInput.setOnKeyReleased(event -> {
			validateport(portNumberInput,0,grid,porterrormessage);
			if(event.getCode().equals(KeyCode.ENTER)){
				ipAddressInput.requestFocus();

			}
                    if(isInt(portNumberInput.getText())==true && !portNumberInput.getText().isEmpty() && !nameInput.getText().isEmpty() && !ipAddressInput.getText().isEmpty() && portNumberInput.getText().length()<16 && nameInput.getText().length()<16 && ipAddressInput.getText().length()<16 && nameInput.getText().trim().length() !=0 && ipAddressInput.getText().trim().length() !=0){
                        join.setDisable(false);
                    }
                    else{
                        join.setDisable(true);
                    }
	
		});

                nameInput.setOnKeyReleased(event -> {
                    validate(nameInput,4,grid,nameerrormessage);
                    if(isInt(portNumberInput.getText())==true && !portNumberInput.getText().isEmpty() && !nameInput.getText().isEmpty() && !ipAddressInput.getText().isEmpty() && portNumberInput.getText().length()<16 && nameInput.getText().length()<16 && ipAddressInput.getText().length()<16 && nameInput.getText().trim().length() !=0 && ipAddressInput.getText().trim().length() !=0){
                        join.setDisable(false);
                    }
                    else{
                        join.setDisable(true);
                    }
                    if(event.getCode().equals(KeyCode.BACK_SPACE)){
                        if(nameInput.getText().length()==1){
                                nameInput.clear();
                            }          
                    }
                });

                join.setOnAction(e ->{
                	portNumber= Integer.parseInt(portNumberInput.getText());
                	ipAddress = ipAddressInput.getText();
                	name= nameInput.getText();
                	if(!((portNumberInput.getText()).isEmpty() || (ipAddressInput.getText()).isEmpty() || (nameInput.getText()).isEmpty()) ){                                
                		myself = new Client(ipAddress,portNumber,name,this);
                		Boolean clientIsConnected = false;
                		clientIsConnected = myself.connectClient();

                		if (clientIsConnected) {
                			checkForClientsThreadRunning = true;
                			returnToMenuThreadRunning = true;
                			checkForMessagesThreadRunning = true;
                			returnToMenuThread(); //start thread that checks if client is disconnected from server
                			checkForClientsThread();
                			checkForMessagesThread();
                			checkForStartThread();
                			lobby();
                		}
                		else {
                		}
                	}
                	else {
                	}
                });

		soundFX(join);
		grid.getChildren().addAll(portNumberInput, ipAddressInput, nameInput, join);
		BorderPane border = new BorderPane();
		border.setTop(toppane);
		border.setCenter(grid);
		scene3 =new Scene(border,width,height);
		scene3.getStylesheets().add(getClass().getResource("zelda.css").toExternalForm());
		window.setScene(scene3);
		previousScene=scene3;
		window.show(); 

	}

        /**
         * Validates the user input from the a given text field and displays error message if input is invalid
         * @param a is the text-field to be validated 
         * @param pos is the position of the message in the grid
         * @param grid the grid the label is to be displayed on
         * @param errormessage is the label for the message to be displayed
         **/
        public void validate(TextField a, int pos, GridPane grid, Label errormessage){
            
            if((a.getText()).length()==0){              
                a.getStyleClass().remove("valid");
                a.setStyle(null);
                a.getStyleClass().add("error");  
                grid.getChildren().remove(errormessage);
                errormessage.setText("    Please enter a value ");     
                GridPane.setConstraints(errormessage, 2, pos);
                grid.getChildren().add(errormessage);
                errormessage.getStyleClass().add("error");                
            }
            else if (a.getText().trim().length() ==0){
                a.getStyleClass().remove("valid");
                a.setStyle(null);
                a.getStyleClass().add("error");   
                grid.getChildren().remove(errormessage);
                errormessage.setText("    Please enter a character value ");     
                GridPane.setConstraints(errormessage, 2, pos);
                grid.getChildren().add(errormessage);
                errormessage.getStyleClass().add("error");            
            }
            else if(a.getText().length()>=16){
                a.getStyleClass().remove("valid");
                a.setStyle(null);
                a.getStyleClass().add("error");   
                grid.getChildren().remove(errormessage);
                errormessage.setText("    Max. 16 Characters ");     
                GridPane.setConstraints(errormessage, 2, pos);
                grid.getChildren().add(errormessage);
                errormessage.getStyleClass().add("error");           
            }
            else{
                a.getStyleClass().remove("error");
                a.setStyle(null);
                a.getStyleClass().add("valid"); 
                grid.getChildren().remove(errormessage);  
            }
        }
        
        /**
         * Validates the user input from the a given text field and displays error message if input is invalid
         * @param a is the text-field to be validated 
         * @param pos is the position of the message in the grid
         * @param grid the grid the label is to be displayed on
         * @param errormessage is the label for the message to be displayed
         **/
        public void validateport(TextField a, int pos, GridPane grid, Label errormessage){
            
            if((a.getText()).length()==0){
                a.getStyleClass().remove("valid");
                a.setStyle(null);
                a.getStyleClass().add("error");
                grid.getChildren().remove(errormessage);
                errormessage.setText("    Please enter a value ");
                errormessage.getStyleClass().add("error");
                GridPane.setConstraints(errormessage, 2, pos);
                grid.getChildren().add(errormessage);
 
            }
            else if(isInt(a.getText())==false){
                a.getStyleClass().remove("valid");
                a.setStyle(null);
                a.getStyleClass().add("error");
                grid.getChildren().remove(errormessage);
                errormessage.setText("    Value must be an integer ");
                errormessage.getStyleClass().add("error");
                GridPane.setConstraints(errormessage, 2, pos);
                grid.getChildren().add(errormessage);
  
            }
            else if(a.getText().length()>=16){
                a.getStyleClass().remove("valid");
                a.setStyle(null);
                a.getStyleClass().add("error");   
                grid.getChildren().remove(errormessage);
                errormessage.setText("    Max. 16 Characters ");     
                GridPane.setConstraints(errormessage, 2, pos);
                grid.getChildren().add(errormessage);
                errormessage.getStyleClass().add("error");        
            }
            else{
                a.getStyleClass().remove("error");
                a.setStyle(null);
                a.getStyleClass().add("valid"); 
                grid.getChildren().remove(errormessage);
            }      
        }

        /**
         * Displays the settings pop-up 
         */
	public void settings(){

		ToggleButton soundfx = new ToggleButton("Sound FX");
		ToggleButton music = new ToggleButton("Music");
		musicSlider = new Slider(0, 100, musicVolume);
		soundfxSlider = new Slider(0, 100, soundfxVolume);
		musicSlider.setPrefWidth(300);
		soundfxSlider.setPrefWidth(300);
		musicSlider.setBlockIncrement(1);
		soundfxSlider.setBlockIncrement(1);

		musicSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue <?extends Number>observable, Number oldValue, Number newValue){
                                musicVolume=newValue.intValue();
				BackgroundMusic.setMusicVolume(newValue.intValue());
			}
		});
		musicSlider.styleProperty().bind(Bindings.createStringBinding(() -> {
			double percentage = (musicSlider.getValue() - musicSlider.getMin()) / (musicSlider.getMax() - musicSlider.getMin()) * 100.0 ;
			return String.format("-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
					+ "-slider-filled-track-color %f%%, -fx-base %f%%, -fx-base 100%%);", 
					percentage, percentage);
		}, musicSlider.valueProperty(), musicSlider.minProperty(), musicSlider.maxProperty()));

		soundfxSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue <?extends Number>observable, Number oldValue, Number newValue){

                                soundfxVolume=newValue.intValue();
				SfxController.setVolume(newValue.intValue());
			}
		});
		soundfxSlider.styleProperty().bind(Bindings.createStringBinding(() -> {
			double percentage = (soundfxSlider.getValue() - soundfxSlider.getMin()) / (soundfxSlider.getMax() - soundfxSlider.getMin()) * 100.0 ;
			return String.format("-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
					+ "-slider-filled-track-color %f%%, -fx-base %f%%, -fx-base 100%%);", 
					percentage, percentage);
		}, soundfxSlider.valueProperty(), soundfxSlider.minProperty(), soundfxSlider.maxProperty()));

		soundfx.setOnAction(e ->{
			SfxController.toggleSound();
			if(sound){
				soundfxSlider.setDisable(true);
				sound=false;
			}
			else {
				soundfxSlider.setDisable(false);
				sound=true;
			}
		});

		music.setOnAction(e ->{
			BackgroundMusic.toggleSound();
			if(musicactive){
				musicSlider.setDisable(true);
				musicactive=false;
			}
			else{ musicSlider.setDisable(false);
			musicactive=true;
			}
		});

		VBox buttons = new VBox (15);
		VBox sliders = new VBox (30);
		HBox layout = new HBox (30);
		layout.getChildren().addAll(buttons,sliders);
		buttons.getChildren().addAll(soundfx,music);
		sliders.getChildren().addAll(soundfxSlider,musicSlider);
		layout.setAlignment(Pos.CENTER);
		sliders.setAlignment(Pos.CENTER);
		buttons.setAlignment(Pos.CENTER);

		Stage popupStage = new Stage(StageStyle.TRANSPARENT);
		
		Button close = new Button("Close");
		close.setOnAction(e -> popupStage.hide());
		HBox toppane = new HBox (20);
		toppane.getChildren().addAll(close);
		toppane.setAlignment(Pos.TOP_RIGHT);

		BorderPane layout1 = new BorderPane();
		layout1.setTop(toppane);
		layout1.setCenter(layout);
		scene4 =new Scene(layout1,500,200);
		scene4.getStylesheets().add(getClass().getResource("settings.css").toExternalForm());

		popupStage.initOwner(window);
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setScene(scene4);
		popupStage.showAndWait();

	}
        
        /**
         * Creates and displays the game lobby
         **/
	public void lobby(){

		Button back = new Button("Back");
		back.setOnAction(e -> {
			checkForClientsThreadRunning = false;
			myself.disconnectClient();
			returnToMenuThreadRunning = false; 
			if(previousScene==scene2){
				window.setScene(scene2);
				window.show(); 
			}
			else if(previousScene==scene3){
				window.setScene(scene3);
				window.show(); 
			}
		});
		soundFX(back);
		Button start = new Button("Start");
		start.setOnAction(e -> {
			if (start.getText().equals("Start")) {
				myself.sendByteArray(12, "");
				start.setText("Cancel");
			} else {
				myself.sendByteArray(13, "");
				start.setText("Start");
			}
		});

		soundFX(start);
		start.setAlignment(Pos.CENTER);

		HBox toppane = new HBox (20);
		toppane.getChildren().addAll(back);
		toppane.setAlignment(Pos.TOP_RIGHT);

		VBox midpane = new VBox (20);
		midpane.getChildren().addAll(players(),start);
		midpane.setAlignment(Pos.TOP_CENTER);

		BorderPane layout2 = new BorderPane();
		layout2.setTop(toppane);
		layout2.setCenter(midpane);
		layout2.setRight(initChatBox());
		scene4 =new Scene(layout2,width,height);
		scene4.getStylesheets().add(getClass().getResource("zelda.css").toExternalForm());
		window.setScene(scene4);
		window.show(); 
	}

        /**
         * Displays grid pane of all players in the lobby
         **/
	public GridPane players(){

		playersGridP = new GridPane(); 
		playersGridP.setPadding(new Insets(10, 10, 10, 10));
		playersGridP.setVgap(100);
		playersGridP.setHgap(200);

		for (int i=0; i<4; i++) {
			playersBoxes[i] = new VBox(10);
			playersNames[i] = new Label("player");
			playersNames[i].setAlignment(Pos.CENTER);
			playersNames[i].setWrapText(true);
		}
		playersGridP.setAlignment(Pos.CENTER);
		return playersGridP;

	}

        /**
         * creates and displays group chat in the lobby
         **/
	public VBox initChatBox(){
		VBox chatroom =new VBox(10);
		Button send = new Button("Send");
		HBox bottomPanel= new HBox(10);
		text.setMinSize(((width/5)-5),20);
		bottomPanel.getChildren().addAll(text,send);
		container.setPrefSize((width/5), (height-300));
		chatBox.getStyleClass().add("chatbox");

		send.setOnAction(e->{
			if(!(text.getText()).isEmpty()){                          
				String message="(" + name + ") " + text.getText() ;  
				text.clear();
				myself.setMessages(message);
				myself.sendByteArray(5, message);
			}
		});
		text.setOnKeyPressed(e->{
			if(e.getCode().equals(KeyCode.ENTER) && !(text.getText()).isEmpty()){
				String message="(" + name + ") " + text.getText() ;
				text.clear();
				myself.setMessages(message);
				myself.sendByteArray(5, message);
			}
		});
                
		chatroom.getChildren().addAll(container,bottomPanel);
		chatroom.getStylesheets().add(getClass().getResource("chatbox.css").toExternalForm());
		chatroom.setAlignment(Pos.CENTER);
		return chatroom ;
	}



	/**
         * Displays the close program pop-up
         **/
	public void closeProgram(){
		BackgroundMusic.kill();
		boolean result = ConfirmBox.display("Quit","Are you sure you what to quit?");
		if(result){
			window.close();
		}
	}

	/**
         * Checks if a string is an integer
         **/
	public boolean isInt(String str){
		if(str.matches("-?\\d+")){return true;}
                return false;
                // only got here if we didn't return false
                
	}

	/**
         * When a client is disconnected from the server, they are forced to the main menu
         */
	public void returnToMenuThread() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Runnable updater = new Runnable() {

					@Override
					public void run() {
						window.setScene(scene1);
						window.show();
					}
				};

				while (returnToMenuThreadRunning) {
					boolean stillConnected = true;
					try {
						stillConnected = myself.getRunning();
					} catch (NullPointerException e) {
					}

					try {
						Thread.sleep(5000); 
					} catch (InterruptedException e) {	
					}
					if (stillConnected == false) {
                                                myself.disconnectClient();
						Platform.runLater(updater);
						returnToMenuThreadRunning = false;
						checkForClientsThreadRunning = false;
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}


	/**
         *Check for any new clients (or removed clients)
         */
	public void checkForClientsThread() { 
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				//GUI Updates Here
				Runnable updater = new Runnable() {
					@Override
					public void run() {
						String[] clientNames = myself.getClientNames();
						for (int i=0; i<4; i++) {
							if (clientNames[i] != null) {
								if (playersNames[i].getText().equals("player")) {
									playersNames[i].setText(clientNames[i]);
									ImageView view =new ImageView(loadImage("character"+Integer.toString(i+1)+"/1",200,200));
									playersBoxes[i].getChildren().addAll(playersNames[i],view);

									int x = 0;
									int y = 0;
                                                                        switch (i) {
                                                                            case 1:
                                                                                x = 1;
                                                                                y = 0;
                                                                                break;
                                                                            case 2:
                                                                                x = 0;
                                                                                y = 1;
                                                                                break;
                                                                            case 3:
                                                                                x = 1;
                                                                                y = 1;
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }

								GridPane.setConstraints(playersBoxes[i], x, y);
								playersGridP.getChildren().add(playersBoxes[i]);
							}
							else {
							}
						}
						else {
							if (!playersNames[i].getText().equals("player")) {
								playersNames[i].setText("player");
								playersGridP.getChildren().remove(playersBoxes[i]);
								playersBoxes[i] = new VBox();
							}
						}
					}
				}
			};
			
			while (checkForClientsThreadRunning) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {	
				}
				Platform.runLater(updater);
			}
			playersGridP.getChildren().removeAll();
			}});

		thread.setDaemon(true);
		thread.start();
	}


        /**
         * Constantly checks for new messages
         **/
	public void checkForMessagesThread() { 
		Thread thread2 = new Thread(new Runnable() {
			@Override
			public void run() {
				//GUI Updates Here
				Runnable updater = new Runnable() {
					@Override
					public void run() {
						Stack<String> stack = myself.getMessages();
						for (String message : stack) {
							Label label = new Label(message);
							label.setAlignment(Pos.CENTER_LEFT);
							label.setMaxWidth((width/5)-5);
							label.setWrapText(true);
							chatBox.getChildren().add(label);
							container.setContent(chatBox);
						}
					}
				};
				while (checkForMessagesThreadRunning) {

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {	
					}

					Platform.runLater(updater);
				}					
			}
		});

		thread2.setDaemon(true);
		thread2.start();
	}

	/**
         * Continually ensure the game has not began
         **/
	public void checkForStartThread() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				//GUI Updates Here
				Runnable updater = new Runnable() {
					@Override
					public void run() {
						gameStarted = false;
						checkForStartThreadRunning = false;
						checkForClientsThreadRunning = false;

						GameInitialiser gameInit = new GameInitialiser(window, gameScene,myself);
					}
				};
				while (checkForStartThreadRunning) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {	
					}
					if (gameStarted) {
						Platform.runLater(updater);
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}


        /**
         * set checkForClientsThreadRunning variable to true or false 
         * @param state is either true or false
         **/
	public void setCheckForClients(boolean state) {
		checkForClientsThreadRunning = state;
	}

        /**
         * set gameStarted variable to true or false 
         * @param state is either true or false
         **/
	public void setGameStarted(boolean state) {
		gameStarted = state;
	}

        /**
         * Displays the host game screen 
         * @param name is the name of the image
         * @param x is the preferred breadth of the image 
         * @param y is the preferred length of the image 
         **/
	public Image loadImage(String name,int x, int y){

		FileInputStream inputstream = null;
		try
		{
			inputstream = new FileInputStream("res/graphics/"+ name+".png");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return new Image(inputstream,x,y,true,true);
	}
}

