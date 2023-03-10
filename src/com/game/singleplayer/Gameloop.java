package com.game.singleplayer;

import java.util.List;

import com.game.areas.AreasSupport;
import com.game.entities.ComplexEntity;
import com.game.entities.ComplexEntityCollision;
import com.game.entities.GenericEntity;
import com.game.entities.GenericEntity.Type;
import com.game.entities.GenericMonster;
import com.game.entities.GenericMonster.State;
import com.game.entities.NullEntity;
import com.game.entities.Player;
import com.game.entities.Retriever;
import com.game.entities.Rock;
import com.game.entities.SpikyRock;
import com.game.graphics.Renderer;
import com.game.screens.GUI;
import com.game.sound.EntitySFX;
import com.game.utilities.Vector;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * <h1>Gameloop</h1> This class contains the gameloop used to run the single
 * player game
 */
public class Gameloop extends AnimationTimer {
	private Player player;
	private InputHandler inputHandler;
	private Canvas gameCanvas;
	private Renderer gameRenderer;
	private Renderer uiRen;
	private Stage stage;
	private int screenNumber = 0;
	private boolean gameStarted = false;
	private int thisPlayerId = 0;
	private int prevRoom = 0;
	private int monstersLeft = 0;
	private AreasSupport as = new AreasSupport();
	private GenericEntity retriever = new Retriever();

	/**
	 * Creates a Gameloop object This sets up game-related values and then
	 * automatically starts it
	 * 
	 * @param gameCanvas   - reference to the Canvas the game uses for graphics
	 * @param inputObject  - reference to the InputHandler the game uses to
	 *                     recognise inputs
	 * @param gameRenderer - reference to the Renderer used to display the sprites
	 * @param uiRenderer   - reference to the Renderer used to display the user
	 *                     interface
	 * @param stage        - reference to the Stage that the main gui uses
	 */
	public Gameloop(Canvas gameCanvas, InputHandler inputObject, Renderer gameRenderer, Renderer uiRenderer,
			Stage stage) {
		setCanvas(gameCanvas);
		setRenderer(gameRenderer);
		setInputHandler(inputObject);
		uiRen = uiRenderer;
		this.stage = stage;

		Vector canvasSize = new Vector(gameCanvas.getWidth(), gameCanvas.getHeight());
		player = new Player(new Vector(120, 20), 0, "Zack", 1);
		as.createAreas(canvasSize, gameRenderer);

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

		ComplexEntityCollision.setIsOffline(true);

		moveToScreen();
		createUI(uiRen);

		gameStarted = true;
		inputChecker();

	}

	/**
	 * The gameloop ran in the singleplayer mode It performs collision checks,
	 * checks win conditions, updates entities, and checks to see if the player has
	 * changed rooms
	 */
	// if entity sprite not on renderer then add it
	@Override
	public void handle(long now) {

		try {
			screenNumber = player.getScreenId();
			if (prevRoom != screenNumber) {
				System.out.println("moved screen");
				retriever.getEntities().get(this.prevRoom)
						.forEach(entity -> gameRenderer.removeSprite(entity.getEntitySprite()));

				thisPlayerId = retriever.moveEntity(player, prevRoom);
				retriever.moveEntity(player.getSword(), prevRoom);
				retriever.moveEntity(player.getArrow(), prevRoom);
				retriever.moveEntity(player.getMagic(), prevRoom);

				// thisPlayerId =
				// retriever.moveEntity(retriever.getEntities().get(prevRoom).get(thisPlayerId),
				// prevRoom);

				moveToScreen();

				System.out.println(player.getSword().getScreenId());
				System.out.println(player.getArrow().getScreenId());
				System.out.println(screenNumber + " " + prevRoom);

			}
			prevRoom = screenNumber;
		} catch (Exception e) {
			// player null
		}

		if (player.isAlive()) {

			player.updateState();

			List<GenericEntity> screen = retriever.getEntities().get(screenNumber);
			List<GenericEntity> entities = screen;
			for (int i = 0; i < entities.size(); i++) {
				GenericEntity e = entities.get(i);

				if (e.entityType == Type.MONSTER) {
					GenericMonster mon = (GenericMonster) e;
					mon.updateState();
					mon.performAction();
					if (mon.getCurrState() == State.DEAD) {
						entities.set(i, new NullEntity());
						gameRenderer.removeSprite(mon.getEntitySprite());
					}
					e.getEntitySprite().updatePositionWithVelocity(10);
					e.getEntitySprite().updateRotationWithVelocity(10);

				}

				try {
					entities.forEach(otherEntity -> {
						if (otherEntity != e) {
							e.getEntitySprite().performCollisionCheck(otherEntity.getEntitySprite());
						}
					});
				} catch (Exception ex) {
					System.out.println("Failed to perform a collision check!");
					// ex.printStackTrace();
				}

			}

			// Rock Collision checks
			try {
				entities.forEach(entity -> {
					if (entity instanceof Rock || entity instanceof SpikyRock) {
						entity.getEntitySprite().performCollisionCheck(player.getEntitySprite());
					}
				});
			} catch (Exception ex) {
				System.out.println("Failed to perform a collision check!");
			}

			// Win Condition check
			monstersLeft = 0;
			for (int i = 0; i < 5; i++) {
				List<GenericEntity> screenX = retriever.getEntities().get(i);
				for (int j = 0; j < screenX.size(); j++) {
					if (screenX.get(j).getEntityType() == Type.MONSTER) {
						monstersLeft++;
						break;
					}
				}
				if (monstersLeft > 0) {
					break;
				}
			}

			if (monstersLeft == 0) {
				gameStarted = false;
				String winorlose = "You Won!";
				System.out.println(winorlose);
				endGame(winorlose, stage);

				for (int i = 0; i < 5; i++) {
					retriever.getEntities().get(i)
							.forEach(entity -> gameRenderer.removeSprite(entity.getEntitySprite()));
					retriever.getEntities().get(i).clear();
				}

				ComplexEntityCollision.setIsOffline(false);
				this.stop();
			}

		} else {
			gameStarted = false;
			// move to gui screen

			String winorlose = "You Lost!";
			System.out.println(winorlose);
			endGame(winorlose, stage);

			for (int i = 0; i < 5; i++) {
				retriever.getEntities().get(i).forEach(entity -> gameRenderer.removeSprite(entity.getEntitySprite()));
				retriever.getEntities().get(i).clear();
			}

			// retriever.getEntities().stream().flatMap(List::stream).forEach(entity ->
			// gameRenderer.removeSprite(entity.getEntitySprite()));
			// retriever.getEntities().forEach(screen -> screen.clear());

			// stop this handle thread
			ComplexEntityCollision.setIsOffline(false);
			this.stop();
		}
	}

	/**
	 * Allows you to change the canvas
	 * 
	 * @param setCanvas - canvas to change to
	 */
	public void setCanvas(Canvas setCanvas) {
		gameCanvas = setCanvas;
	}

	/**
	 * Allows you to change the renderer
	 * 
	 * @param setRenderer - renderer to change to
	 */
	public void setRenderer(Renderer setRenderer) {
		gameRenderer = setRenderer;
	}

	/**
	 * Allows you to change the inputhandler
	 * 
	 * @param setInputHandler - inputhandler to change to
	 */
	public void setInputHandler(InputHandler setInputHandler) {
		inputHandler = setInputHandler;
	}

	/**
	 * Adds all sprites from the current screen to the renderer
	 */
	private void moveToScreen() {
		retriever.getEntities().get(this.screenNumber)
				.forEach(entity -> gameRenderer.addSprite(entity.getEntitySprite()));
	}

	/**
	 * Allows you to change the player (object)
	 * 
	 * @param setPlayer - player (object) to change to
	 */
	public void setPlayer(Player setPlayer) {
		player = setPlayer;
	}

	/***
	 * Creates the stats screen at the top of the screen.
	 * 
	 * @param renderer - The renderer to add the stats sprites.
	 */
	public void createUI(Renderer renderer) {
		Player player = ((Player) retriever.getEntities().get(screenNumber).get(thisPlayerId));
		renderer.addSprite(player.getHealthBar());
		renderer.addSprite(player.getArrowcount());
		renderer.addSprite(player.getSwordLabel());
		renderer.addSprite(player.getBowLabel());
		renderer.addSprite(player.getMagicLabel());
		renderer.addSprite(player.getControls());
		renderer.addSprite(player.getMagicBar());
		renderer.addSprite(player.getSwordDmgLabel());
		renderer.addSprite(player.getBowDmgLabel());
		renderer.addSprite(player.getMagicDmgLabel());
		renderer.addSprite(player.getShieldLabel());
		renderer.addSprite(player.getSpeedLabel());
		renderer.addSprite(player.getAtkCooldownLabel());

	}

	/**
	 * Creates a thread that continually checks for input
	 */
	private void inputChecker() {
		Thread thread = new Thread() {
			@Override
			public void run() {

				while (gameStarted) {

					inputHandler.checkKeyboardInput(gameCanvas, player, gameRenderer);
					inputHandler.checkAttack(gameCanvas, player, gameRenderer);

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		thread.setDaemon(true); // so it doesn't prevent JVM shutdown
		thread.start();
	}

	/**
	 * Displays a win/lose screen and allows the player to the main menu after
	 * winning/losing a game
	 * 
	 * @param winorlose - string of whether you won or lost
	 * @param stage     - stage that the main gui is displayed on
	 */
	public void endGame(String winorlose, Stage stage) {

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

		endscreen.initOwner(stage);
		endscreen.setOnHidden(evt -> {
		});
		endscreen.show();
	}

}
