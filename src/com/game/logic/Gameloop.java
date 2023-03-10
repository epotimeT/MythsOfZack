package com.game.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import com.game.ai.RockSpitter;
import com.game.ai.Slime;
import com.game.areas.Area0;
import com.game.entities.ComplexEntity;
import com.game.entities.ComplexEntityCollision;
import com.game.entities.GenericEntity;
import com.game.entities.GenericEntity.Type;
import com.game.entities.GenericMonster;
import com.game.entities.NullEntity;
import com.game.entities.Player;
import com.game.entities.Retriever;
import com.game.entities.Rock;
import com.game.entities.SpikyRock;
import com.game.graphics.Renderer;
import com.game.graphics.Sprite;
import com.game.logic.events.DeathEvent;
import com.game.logic.events.HitEvent;
import com.game.logic.events.MoveRoomEvent;
import com.game.logic.events.NetworkEvent;
import com.game.logic.events.NewEntityEvent;
import com.game.logic.events.PlayerAssignmentEvent;
import com.game.logic.events.PositionUpdateEvent;
import com.game.logic.events.RotationUpdateEvent;
import com.game.logic.events.RotationalVelocityUpdateEvent;
import com.game.logic.events.SoundEvent;
import com.game.logic.events.StartEvent;
import com.game.logic.events.VelocityUpdateEvent;
import com.game.logic.events.WinLoseEvent;
import com.game.main.Client;
import com.game.main.NetworkEventHandler;
import com.game.screens.GUI;
import com.game.sound.EntitySFX;
import com.game.utilities.Vector;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Gameloop extends AnimationTimer {
	private Player player; // testing code without network
	private boolean skipGame = false;
	private int count = 400;
	private Sprite startCountdown;

	private Integer thisPlayerId;
	private InputHandler inputHandler;
	private Canvas gameCanvas;
	private Renderer gameRenderer;
	private Renderer uiRen;
	private Client networkClient;
	private Queue<NetworkEvent> eventsToProcess;
	private int shouldSend;
	private int currentScreen;
	private boolean gameStarted = false;
	private boolean hasInformedServerOfDeath = false;
	public Stage stage;

	private GenericEntity retriever = new Retriever();

	/***
	 * A constructor for the Gameloop class. Allows you to skip to the game without
	 * requiring any network components
	 */

	public Gameloop(Canvas gameCanvas, Player player, Renderer gameRenderer, Stage window) {
		setCanvas(gameCanvas);
		setRenderer(gameRenderer);
		setPlayer(player);
		stage = window;
		// setInputHandler(new InputHandler());
		skipGame = true;
		moveToScreen();
	}

	/***
	 * A constructor for the Gameloop class. Sets up the game area and performs a
	 * handshake with the server to start the game.
	 * 
	 * @param gameCanvas    - The canvas to display the game
	 * @param networkClient - The client that calls the constructor
	 * @param inputObject   - The object that handles player inputs
	 * @param gameRenderer  - The game renderer
	 * @param uiRenderer    - The stats renderer
	 */

	public Gameloop(Canvas gameCanvas, Client networkClient, InputHandler inputObject, Renderer gameRenderer,
			Renderer uiRenderer, Stage window) {
		setCanvas(gameCanvas);
		setRenderer(gameRenderer);
		setInputHandler(inputObject);
		uiRen = uiRenderer;
		stage = window;

		this.networkClient = networkClient;
		this.eventsToProcess = new ArrayDeque<>();
		Gameloop loop = this;
		this.networkClient.setEventCallback(new NetworkEventHandler() {
			@Override
			public void onEvent(NetworkEvent event) {
				loop.enqeueNetworkedEvent(event);
			}
		});
		long playerCount = Arrays.stream(networkClient.getClientNames()).filter(name -> name != null).count();
		for (int i = 0; i < playerCount; i++) {

			new Player(new Vector(), 0, networkClient.getClientNames()[i], i + 1)
					.setPosition(new Vector[] { new Vector(10, 10), new Vector(gameCanvas.getWidth() - 10, 10),
							new Vector(10, gameCanvas.getHeight() - 10),
							new Vector(gameCanvas.getWidth() - 10, gameCanvas.getHeight() - 10), }[i]);
		}

		Area0 a0 = new Area0();
		Vector canvasSize = new Vector(gameCanvas.getWidth(), gameCanvas.getHeight());
		a0.obstacles(canvasSize);
		a0.monsters(canvasSize);
		// a0.items(canvasSize);

		retriever.getEntities().forEach(screen -> {
			List<GenericEntity> entities = screen;
			for (int i = 0; i < entities.size(); i++) {
				if (entities.get(i) instanceof ComplexEntity) {
					ComplexEntity e = (ComplexEntity) entities.get(i);
					if (e.getEntityType() == Type.PLAYER) {
						e.setSfx(new EntitySFX(e, 1200, 0));
					} else if (e.getEntityType() == Type.MONSTER) {
						e.setSfx(new EntitySFX(e, 1200, 1));
					}
				}
			}
		});

		this.networkClient.sendByteArray(6, "");

		while (!gameStarted || this.thisPlayerId == null) {
			System.out.println(gameStarted);
			while (!eventsToProcess.isEmpty()) {
				handleEvent(eventsToProcess.remove());
			}
		}

		while (!eventsToProcess.isEmpty()) {
			handleEvent(eventsToProcess.remove());
		}
		moveToScreen();
		createUI(uiRen);
	}

	/***
	 * This is the gameloop run by the clients. It calls methods to check player
	 * inputs, handle events and communicate between the server. It performs player
	 * rock and spiky rock collisions. if (skipGame) allows the game to run without
	 * a server, useful for testing.
	 */
	@Override
	public void handle(long now) {
		if (skipGame) {
			if (count != -1) {
				countdown();
			} else {
				if (player.isAlive()) {
					player.updateState();
					inputHandler.checkKeyboardInput(gameCanvas, player, gameRenderer);
					inputHandler.checkAttack(gameCanvas, player, gameRenderer);
				}

				for (GenericEntity e : retriever.getEntities().stream().flatMap(List::stream)
						.collect(Collectors.toList())) {
					if (e.entityType == Type.MONSTER) {
						GenericMonster mon = (GenericMonster) e;
						if (mon.getCurrState() != GenericMonster.State.DEAD) {
							mon.updateState();
							mon.performAction();
						}
					}
				}
			}

		} else {
			if (((Player) retriever.getEntities().get(currentScreen).get(thisPlayerId)).isAlive()) {

				((Player) retriever.getEntities().get(currentScreen).get(thisPlayerId)).updateState();
				inputHandler.checkKeyboardInput(gameCanvas,
						(Player) retriever.getEntities().get(currentScreen).get(thisPlayerId), gameRenderer);
				inputHandler.checkAttack(gameCanvas,
						(Player) retriever.getEntities().get(currentScreen).get(thisPlayerId), gameRenderer);

				retriever.getEntities().forEach(screen -> {
					List<GenericEntity> entities = screen;
					try {
						entities.forEach(entity -> {
							if (entity instanceof Rock || entity instanceof SpikyRock) {
								entity.getEntitySprite().performCollisionCheck(
										((Player) retriever.getEntities().get(currentScreen).get(thisPlayerId))
												.getEntitySprite());
							}
						});
					} catch (Exception ex) {
						System.out.println("Failed to perform a collision check!");
					}
				});
				if (shouldSend == 0) {
					Player player = (Player) retriever.getEntities().get(currentScreen).get(thisPlayerId);
					this.networkClient.myUDP.sendPacket(1, player.getScreenId(), thisPlayerId, player.getPosition().x(),
							player.getPosition().y(), player.getVelocity().x(), player.getVelocity().y(),
							player.getEntitySprite().rotation(), player.getEntitySprite().rotationalVelocity(),
							player.getInvulnerableTime());

					int swordId = 0;
					List<GenericEntity> entities = retriever.getEntities().stream().flatMap(List::stream)
							.collect(Collectors.toList());
					for (int i = 0; i < entities.size(); i++) { // Find entity of this sprite
						if (entities.get(i) == player.getSword()) {
							swordId = i;
							break;
						}
					}

					this.networkClient.myUDP.sendPacket(1, player.getSword().getScreenId(), swordId,
							player.getSword().getPosition().x(), player.getSword().getPosition().y(),
							player.getSword().getVelocity().x(), player.getSword().getVelocity().y(),
							player.getSword().getEntitySprite().rotation(),
							player.getSword().getEntitySprite().rotationalVelocity(), 0);

				}
				shouldSend = (shouldSend + 1) % 3;
			} else {
				if (!hasInformedServerOfDeath) {
					((Player) retriever.getEntities().get(currentScreen).get(thisPlayerId)).getSfx().PlayTrack(3);
					networkClient.sendByteArray(7, prepareToSendData(thisPlayerId, 3));
					gameRenderer.removeSprite(
							((Player) retriever.getEntities().get(currentScreen).get(thisPlayerId)).getEntitySprite());
					this.networkClient.sendByteArray(10, thisPlayerId.toString());
					hasInformedServerOfDeath = true;
				}
			}

			while (!eventsToProcess.isEmpty()) {
				handleEvent(eventsToProcess.remove());
			}
		}
	}

	public void setCanvas(Canvas setCanvas) {
		gameCanvas = setCanvas;
	}

	public void setRenderer(Renderer setRenderer) {
		gameRenderer = setRenderer;
	}

	public void setInputHandler(InputHandler setInputHandler) {
		inputHandler = setInputHandler;
	}

	public void enqeueNetworkedEvent(NetworkEvent event) {
		if (event instanceof StartEvent) {
			System.out.println("enqueued start event");
			this.gameStarted = true;
		}
		eventsToProcess.add(event);
	}

	/***
	 * Handles an event received from the server.
	 * 
	 * @param event - The event to be handled
	 */
	private void handleEvent(NetworkEvent event) {
		if (event instanceof HitEvent) {
			HitEvent hit = (HitEvent) event;

			((Player) retriever.getEntities().get(currentScreen).get(thisPlayerId)).getSfx().PlayTrack(2);
			networkClient.sendByteArray(7, prepareToSendData(thisPlayerId, 2));

			switch (hit.collision) {
			case 0:
				ComplexEntityCollision.PlayerHitMonPlayerCollision(
						(ComplexEntity) retriever.getEntities().get(currentScreen).get(hit.entityId),
						retriever.getEntities().get(currentScreen).get(hit.entityId).getEntitySprite(),
						(Player) retriever.getEntities().get(currentScreen).get(thisPlayerId),
						retriever.getEntities().get(currentScreen).get(thisPlayerId).getEntitySprite());
				break;
			case 1:
				ComplexEntityCollision.PlayerHitEntitySwordCollision(
						(Player) retriever.getEntities().get(currentScreen).get(thisPlayerId),
						retriever.getEntities().get(currentScreen).get(thisPlayerId).getEntitySprite(),
						(ComplexEntity) retriever.getEntities().get(currentScreen).get(hit.entityId));
				break;
			}

			System.out.println("Entity ID " + hit.entityId + " is attacking!"
					+ (hit.entityId == thisPlayerId ? " (that's me!)" : ""));
		} else if (event instanceof MoveRoomEvent) {
			// TODO
			MoveRoomEvent moveRoom = (MoveRoomEvent) event;
			System.out.println("Entity ID " + moveRoom.entityId + " is moving room!"
					+ (moveRoom.entityId == thisPlayerId ? " (that's me!)" : ""));
		}
		// movement events can fail gracefully.
		try {
			if (event instanceof PositionUpdateEvent) {
				PositionUpdateEvent position = (PositionUpdateEvent) event;
				retriever.getEntities().get(position.screenId).get(position.entityId)
						.setPosition(position.newVelocity);
				System.out.println("Entity ID " + position.entityId + " position updated!"
						+ (position.entityId == thisPlayerId ? " (that's me!)" : ""));
			} else if (event instanceof VelocityUpdateEvent) {
				VelocityUpdateEvent velocity = (VelocityUpdateEvent) event;
				if (!(retriever.getEntities().get(velocity.screenId).get(velocity.entityId) instanceof Player)) {
					retriever.getEntities().get(velocity.screenId).get(velocity.entityId)
							.setVelocity(velocity.newVelocity);
					System.out.println("Entity ID " + velocity.entityId + " velocity updated!"
							+ (velocity.entityId == thisPlayerId ? " (that's me!)" : ""));
				}
			} else if (event instanceof RotationUpdateEvent) {
				RotationUpdateEvent rotation = (RotationUpdateEvent) event;
				retriever.getEntities().get(rotation.screenId).get(rotation.entityId).getEntitySprite()
						.setRotation(rotation.newRotation);
				System.out.println("Entity ID " + rotation.entityId + " rotation updated!"
						+ (rotation.entityId == thisPlayerId ? " (that's me!)" : ""));
			} else if (event instanceof RotationalVelocityUpdateEvent) {
				RotationalVelocityUpdateEvent rotationalVelocity = (RotationalVelocityUpdateEvent) event;
				retriever.getEntities().get(rotationalVelocity.screenId).get(rotationalVelocity.entityId)
						.getEntitySprite().setRotationalVelocity(rotationalVelocity.newRotationalVelocity);
				System.out.println("Entity ID " + rotationalVelocity.entityId + " rotational velocity updated!"
						+ (rotationalVelocity.entityId == thisPlayerId ? " (that's me!)" : ""));
			}
		} catch (Exception e) {
			System.out.println("Invalid movement event!");
		}
		if (event instanceof StartEvent) {
			this.gameStarted = true;
		} else if (event instanceof NewEntityEvent) {
			NewEntityEvent newEntity = (NewEntityEvent) event;
			switch (newEntity.which) {
			case PLAYER:
				new Player(new Vector(), newEntity.screenId, newEntity.name, newEntity.color);
				break;
			case SLIME:
				new Slime(new Vector(), newEntity.screenId);
				break;
			case ROCKSPITTER:
				new RockSpitter(new Vector(), newEntity.screenId);
				break;
			case ROCK:
				new Rock(new Vector(), (Vector) newEntity.extraData, newEntity.screenId);
				break;
			case SPIKYROCK:
				new SpikyRock(new Vector(), (Vector) newEntity.extraData, newEntity.screenId);
				break;
			}
		} else if (event instanceof PlayerAssignmentEvent) {
			PlayerAssignmentEvent playerAssignment = (PlayerAssignmentEvent) event;
			this.currentScreen = playerAssignment.screenId;
			this.thisPlayerId = playerAssignment.entityId;
		} else if (event instanceof DeathEvent) {
			DeathEvent death = (DeathEvent) event;
			if (retriever.getEntities().get(0).get(death.entityId).getEntityType() == Type.PLAYER) {
				((Player) retriever.getEntities().get(currentScreen).get(thisPlayerId)).getSfx().PlayTrack(3);
			} else if (retriever.getEntities().get(0).get(death.entityId).getEntityType() == Type.MONSTER) {
				((Player) retriever.getEntities().get(currentScreen).get(thisPlayerId)).getSfx().PlayTrack(3);
				networkClient.sendByteArray(7, prepareToSendData(thisPlayerId, 2));
			}
			gameRenderer.removeSprite(retriever.getEntities().get(0).get(death.entityId).getEntitySprite());
			retriever.getEntities().get(0).set(death.entityId, new NullEntity());
			System.out.println(death.entityId + " died!");
		} else if (event instanceof SoundEvent) {
			SoundEvent sound = (SoundEvent) event;
			ComplexEntity e = (ComplexEntity) retriever.getEntities().get(0).get(sound.entityId);
			e.getSfx().PlayTrack(sound.soundId);
		} else if (event instanceof WinLoseEvent) {
			WinLoseEvent winLose = (WinLoseEvent) event;
			// TODO
			String winorlose = "You " + (winLose.didWin ? "Won!" : "Lost!");
			System.out.println(winorlose);
			endGame(winorlose);
			retriever.getEntities().stream().flatMap(List::stream)
					.forEach(entity -> gameRenderer.removeSprite(entity.getEntitySprite()));
			retriever.getEntities().forEach(screen -> screen.clear());
			this.stop();
		}
	}

	private void moveToScreen() {
		// TODO: renderer management
		retriever.getEntities().get(this.currentScreen)
				.forEach(entity -> gameRenderer.addSprite(entity.getEntitySprite()));
	}

	public void setPlayer(Player setPlayer) {
		player = setPlayer;
	}

	/***
	 * A countdown for skipping to the game to allow some time for the cpu usage to
	 * decrease as it finishes loading things to memory.
	 */
	public void countdown() {
		FileInputStream inputstream = null;
		if (count == 400) {
			// player.getSfx().PlayTrack(6);
			try {
				inputstream = new FileInputStream(new File("res/graphics/count3.png"));
			} catch (FileNotFoundException e) {
				System.out.println("FileNotFoundException: count3.png not found");
			}
		} else if (count == 280) {
			gameRenderer.removeSprite(startCountdown);
			try {
				inputstream = new FileInputStream(new File("res/graphics/count2.png"));
			} catch (FileNotFoundException e) {
				System.out.println("FileNotFoundException: count3.png not found");
			}
		} else if (count == 160) {
			gameRenderer.removeSprite(startCountdown);
			try {
				inputstream = new FileInputStream(new File("res/graphics/count1.png"));
			} catch (FileNotFoundException e) {
				System.out.println("FileNotFoundException: count3.png not found");
			}
		} else if (count == 40) {
			gameRenderer.removeSprite(startCountdown);
			try {
				inputstream = new FileInputStream(new File("res/graphics/count0.png"));
			} catch (FileNotFoundException e) {
				System.out.println("FileNotFoundException: count3.png not found");
			}
		} else if (count == 0) {
			gameRenderer.removeSprite(startCountdown);
			count -= 1;
			return;
		} else {
			count -= 1;
			return;
		}
		ArrayList<Image> imgs = new ArrayList<Image>();
		imgs.add(new Image(inputstream));
		startCountdown = new Sprite(
				new Vector((gameCanvas.getWidth() / 2) - 250.0, (gameCanvas.getHeight() / 2) - 150.0), new Vector(),
				new Vector(500.0, 300.0), imgs);
		gameRenderer.addSprite(startCountdown);
		count -= 1;
	}

	/***
	 * Creates the stats screen at the top of the screen.
	 * 
	 * @param renderer - The renderer to add the stats sprites.
	 */
	public void createUI(Renderer renderer) {
		Player player = ((Player) retriever.getEntities().get(currentScreen).get(thisPlayerId));
		renderer.addSprite(player.getHealthBar());
		renderer.addSprite(player.getSwordLabel());
		renderer.addSprite(player.getControls());
		renderer.addSprite(player.getSwordDmgLabel());
		renderer.addSprite(player.getShieldLabel());
		renderer.addSprite(player.getSpeedLabel());
		renderer.addSprite(player.getAtkCooldownLabel());

	}

	private static String prepareToSendData(Object... values) {
		return Arrays.stream(values).map(x -> x.toString()).collect(Collectors.joining(","));
	}

	public void endGame(String winorlose) {

		Stage endscreen = new Stage(StageStyle.TRANSPARENT);
		endscreen.initStyle(StageStyle.UNDECORATED);
		endscreen.centerOnScreen();

		Label gameover = new Label("Game Over");
		Label result = new Label(winorlose);
		Button mainmenu = new Button("MAIN MENU");
		mainmenu.setOnAction(e -> {
			endscreen.hide();
			GUI gui = new GUI();
			gui.mainMenu(stage);
		});
		VBox layout = new VBox(20);
		layout.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(gameover, result, mainmenu);

		Scene scene5 = new Scene(layout, 300, 300);
		scene5.getStylesheets().add(getClass().getResource("settings.css").toExternalForm());
		// popupStage.initOwner(window);
		endscreen.setScene(scene5);
		// popupStage.setOnHidden(evt -> Platform.exit());

		endscreen.show();
	}
}
