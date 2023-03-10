package com.game.logic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.game.commands.MeleeCommands;
import com.game.commands.RangedCommands;
import com.game.entities.ArrowProjectile;
import com.game.entities.GenericEntity;
import com.game.entities.GenericEntity.Type;
import com.game.entities.Magic;
import com.game.entities.Player;
import com.game.entities.Player.directionPressed;
import com.game.entities.Player.weapon;
import com.game.entities.Retriever;
import com.game.graphics.Renderer;
import com.game.main.Client;
import com.game.utilities.Vector;

import javafx.animation.Animation;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

class PlayerAnimation {
	public PlayerAnimation() {

	}

	/**
	 * Stops player animation.
	 * 
	 * @param player
	 */
	public void stopAnimation(Player player) {

		if (player.timeline != null) {
			if (player.timeline.getStatus().equals(Animation.Status.RUNNING)) {
				player.timeline.stop();
				player.timeline.getKeyFrames().clear();
				player.timeline = null;
			}
		}
	}

	/**
	 * Continues player animation.
	 * 
	 * @param player
	 * @param i
	 */
	public void continueAnimation(Player player, int i) {

		if (player.getDirPressed() != null) {
			if (player.getDirPressed().size() >= 1) {
				if (player.getDirPressed().get(0) == directionPressed.UP) {
					player.animate(3, 3);
					player.timeline.play();
				}
				if (player.getDirPressed().get(0) == directionPressed.DOWN) {
					player.animate(3, 0);
					player.timeline.play();
				}
				if (player.getDirPressed().get(0) == directionPressed.LEFT) {
					player.animate(3, 6);
					player.timeline.play();
				}
				if (player.getDirPressed().get(0) == directionPressed.RIGHT) {
					player.animate(3, 9);
					player.timeline.play();
				}
			} else if (player.getDirPressed().size() == 0) {
				player.currentsprite.setCurrentSprite(i);
			}
		}

	}

}

class MoveUp {
	/**
	 * Moves the player up the screen and animates it.
	 * 
	 * @param player
	 */
	public void moveUp(Player player) {
		if (player.getEntitySprite().velocity().y() > -player.getMaxSpeed()) {
			PlayerAnimation pA = new PlayerAnimation();
			if (!player.getDirPressed().contains(directionPressed.UP)) {
				pA.stopAnimation(player);
				player.animate(3, 3);
				player.timeline.play();
				player.addDirPressed(directionPressed.UP);
				player.getEntitySprite().updateVelY(-player.getSpeed());
				player.getSword().getEntitySprite().updateVelY(-player.getSpeed());
			}
		}
	}

	/**
	 * Stop the player moving up the screen and stops the animation.
	 * 
	 * @param player
	 */
	public void stopMoveUp(Player player) {
		if (player.getDirPressed().contains(directionPressed.UP)) {
			PlayerAnimation pA = new PlayerAnimation();
			pA.stopAnimation(player);

			player.removeDirPressed(directionPressed.UP);
			pA.continueAnimation(player, 4);
			player.getEntitySprite().updateVelY(player.getSpeed());
			player.getSword().getEntitySprite().updateVelY(player.getSpeed());

		}
	}
}

class MoveDown {
	/**
	 * Moves the player down the screen and animates it.
	 * 
	 * @param player
	 */
	public void moveDown(Player player) {
		if (player.getEntitySprite().velocity().y() < player.getMaxSpeed()) {
			PlayerAnimation pA = new PlayerAnimation();
			if (!player.getDirPressed().contains(directionPressed.DOWN)) {
				pA.stopAnimation(player);
				player.animate(3, 0);
				player.timeline.play();
				player.addDirPressed(directionPressed.DOWN);
				player.getEntitySprite().updateVelY(player.getSpeed());
				player.getSword().getEntitySprite().updateVelY(player.getSpeed());
			}
		}
	}

	/**
	 * Stops the player moving down the screen and stops the animation.
	 * 
	 * @param player
	 */
	public void stopMoveDown(Player player) {
		PlayerAnimation pA = new PlayerAnimation();
		pA.stopAnimation(player);

		player.removeDirPressed(directionPressed.DOWN);
		pA.continueAnimation(player, 1);
		player.getEntitySprite().updateVelY(-player.getSpeed());
		player.getSword().getEntitySprite().updateVelY(-player.getSpeed());

	}
}

class MoveLeft {
	/**
	 * Moves the player to the left of the screen and animates it.
	 * 
	 * @param player
	 */
	public void moveLeft(Player player) {
		if (player.getEntitySprite().velocity().x() > -player.getMaxSpeed()) {
			PlayerAnimation pA = new PlayerAnimation();
			if (!player.getDirPressed().contains(directionPressed.LEFT)) {
				pA.stopAnimation(player);
				player.animate(3, 6);
				player.timeline.play();
				player.addDirPressed(directionPressed.LEFT);
				player.getEntitySprite().updateVelX(-player.getSpeed());
				player.getSword().getEntitySprite().updateVelX(-player.getSpeed());
			}
		}
	}

	/**
	 * Stops the player moving to the left of the screen and stops the animation.
	 * 
	 * @param player
	 */
	public void stopMoveLeft(Player player) {
		PlayerAnimation pA = new PlayerAnimation();
		pA.stopAnimation(player);

		player.removeDirPressed(directionPressed.LEFT);
		pA.continueAnimation(player, 7);
		player.getEntitySprite().updateVelX(player.getSpeed());
		player.getSword().getEntitySprite().updateVelX(player.getSpeed());

	}
}

class MoveRight {
	/**
	 * Moves the player to the right of the screen and animates it.
	 * 
	 * @param player
	 */
	public void moveRight(Player player) {
		if (player.getEntitySprite().velocity().x() < player.getMaxSpeed()) {
			PlayerAnimation pA = new PlayerAnimation();
			if (!player.getDirPressed().contains(directionPressed.RIGHT)) {
				pA.stopAnimation(player);
				player.animate(3, 9);
				player.timeline.play();
				player.addDirPressed(directionPressed.RIGHT);
				player.getEntitySprite().updateVelX(player.getSpeed());
				player.getSword().getEntitySprite().updateVelX(player.getSpeed());
			}
		}
	}

	/**
	 * Stops the player moving to the right of the screen and stops the animation.
	 * 
	 * @param player
	 */
	public void stopMoveRight(Player player) {
		PlayerAnimation pA = new PlayerAnimation();
		pA.stopAnimation(player);

		player.removeDirPressed(directionPressed.RIGHT);
		pA.continueAnimation(player, 10);
		player.getEntitySprite().updateVelX(-player.getSpeed());
		player.getSword().getEntitySprite().updateVelX(-player.getSpeed());

	}
}

class EquipSword {
	/**
	 * Equips the sword to the player.
	 * 
	 * @param player
	 */
	public void equipSword(Player player) {
		player.swordlabel.setCountColor(Color.CHARTREUSE);
		player.bowlabel.setCountColor(Color.WHITE);
		player.magiclabel.setCountColor(Color.WHITE);
		player.setEquippedWeapon(weapon.SWORD);
	}
}

class EquipBow {
	/**
	 * Equips the bow and arrow to the player.
	 * 
	 * @param player
	 */
	public void equipBow(Player player) {
		player.swordlabel.setCountColor(Color.WHITE);
		player.bowlabel.setCountColor(Color.CHARTREUSE);
		player.magiclabel.setCountColor(Color.WHITE);
		player.setEquippedWeapon(weapon.BOW);
		player.setProjectile(new ArrowProjectile());
	}
}

class EquipMagic {
	/**
	 * Equips magic to the player.
	 * 
	 * @param player
	 */
	public void equipMagic(Player player) {
		player.swordlabel.setCountColor(Color.WHITE);
		player.bowlabel.setCountColor(Color.WHITE);
		player.magiclabel.setCountColor(Color.CHARTREUSE);
		player.setEquippedWeapon(weapon.MAGIC);
		player.setProjectile(new Magic());
	}
}

class KeyBoardInput {
	private InputCommand pressedCommand, releasedCommand;
	private Player player;

	public KeyBoardInput() {

	}

	public KeyBoardInput(InputCommand Pressed, Player p) {
		pressedCommand = Pressed;
		player = p;
	}

	public KeyBoardInput(InputCommand Pressed, InputCommand Released, Player p) {
		pressedCommand = Pressed;
		releasedCommand = Released;
		player = p;
	}

	/**
	 * Executes a pressed command dependent on the commands fed to the object.
	 */
	void pressedCommand() {
		pressedCommand.execute(player);
	}

	/**
	 * Executes a released command dependent on the commands fed to the object.
	 */
	void releasedCommand() {
		releasedCommand.execute(player);
	}
}

class MoveUpCommand implements InputCommand {
	private MoveUp moveUp;

	public MoveUpCommand(MoveUp move) {
		moveUp = move;
	}

	/**
	 * Executes a command for moving up.
	 */
	@Override
	public void execute(Player player) {
		moveUp.moveUp(player);
	}
}

class StopMoveUpCommand implements InputCommand {
	private MoveUp moveUp;

	public StopMoveUpCommand(MoveUp move) {
		moveUp = move;
	}

	/**
	 * Executes a command for stopping moving up.
	 */
	@Override
	public void execute(Player player) {
		moveUp.stopMoveUp(player);
	}
}

class MoveDownCommand implements InputCommand {
	private MoveDown moveDown;

	public MoveDownCommand(MoveDown move) {
		moveDown = move;
	}

	/**
	 * Executes a command for moving down.
	 */
	@Override
	public void execute(Player player) {
		moveDown.moveDown(player);
	}
}

class StopMoveDownCommand implements InputCommand {
	private MoveDown moveDown;

	public StopMoveDownCommand(MoveDown move) {
		moveDown = move;
	}

	/**
	 * Executes a command for stopping moving down.
	 */
	@Override
	public void execute(Player player) {
		moveDown.stopMoveDown(player);
	}
}

class MoveLeftCommand implements InputCommand {
	private MoveLeft moveLeft;

	public MoveLeftCommand(MoveLeft move) {
		moveLeft = move;
	}

	/**
	 * Executes a command for moving left.
	 */
	@Override
	public void execute(Player player) {
		moveLeft.moveLeft(player);
	}
}

class StopMoveLeftCommand implements InputCommand {
	private MoveLeft moveLeft;

	public StopMoveLeftCommand(MoveLeft move) {
		moveLeft = move;
	}

	/**
	 * Executes a command for stopping moving left.
	 */
	@Override
	public void execute(Player player) {
		moveLeft.stopMoveLeft(player);
	}
}

class MoveRightCommand implements InputCommand {
	private MoveRight moveRight;

	public MoveRightCommand(MoveRight move) {
		moveRight = move;
	}

	/**
	 * Executes a command for moving right.
	 */
	@Override
	public void execute(Player player) {
		moveRight.moveRight(player);
	}
}

class StopMoveRightCommand implements InputCommand {
	private MoveRight moveRight;

	public StopMoveRightCommand(MoveRight move) {
		moveRight = move;
	}

	/**
	 * Executes a command for stopping moving right.
	 */
	@Override
	public void execute(Player player) {
		moveRight.stopMoveRight(player);
	}
}

class EquipSwordCommand implements InputCommand {
	private EquipSword equipSword;

	public EquipSwordCommand(EquipSword sword) {
		equipSword = sword;
	}

	/**
	 * Executes a command for equipping a sword.
	 */
	@Override
	public void execute(Player player) {
		equipSword.equipSword(player);
	}
}

class EquipBowCommand implements InputCommand {
	private EquipBow equipBow;

	public EquipBowCommand(EquipBow Bow) {
		equipBow = Bow;
	}

	/**
	 * Executes a command for equipping a bow and arrow.
	 */
	@Override
	public void execute(Player player) {
		equipBow.equipBow(player);
	}
}

class EquipMagicCommand implements InputCommand {
	private EquipMagic equipMagic;

	public EquipMagicCommand(EquipMagic Magic) {
		equipMagic = Magic;
	}

	/**
	 * Executes a command for equipping magic.
	 */
	@Override
	public void execute(Player player) {
		equipMagic.equipMagic(player);
	}
}

public class InputHandler {
	static HashMap<KeyCode, Integer> keyMap = new HashMap<KeyCode, Integer>();
	Client networkClient;
	private static GenericEntity retriever = new Retriever();

	public InputHandler(Client networkClient) {
		this.networkClient = networkClient;
		keyMap.put(KeyCode.W, 0);
		keyMap.put(KeyCode.A, 1);
		keyMap.put(KeyCode.S, 2);
		keyMap.put(KeyCode.D, 3);
		keyMap.put(KeyCode.DIGIT1, 4);
		// keyMap.put(KeyCode.DIGIT2, 5);
		// keyMap.put(KeyCode.DIGIT3, 6);
	}

	/**
	 * Handles any keyboard input from the user. Keyboard inputs may move the player
	 * or change the players equipped weapon.
	 * 
	 * @param gameCanvas
	 * @param player
	 * @param gameRenderer
	 */
	public void checkKeyboardInput(Canvas gameCanvas, Player player, Renderer gameRenderer) {
		gameCanvas.requestFocus();
		gameCanvas.setFocusTraversable(true);

		gameCanvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (player.isAlive()) {
					Integer command = keyMap.get(event.getCode());
					KeyBoardInput keyBoardInput = new KeyBoardInput();
					if (command != null) {
						switch (command) {
						case 0:
							MoveUp moveUp = new MoveUp();
							MoveUpCommand moveUpCommand = new MoveUpCommand(moveUp);
							StopMoveUpCommand stopMoveUpCommand = new StopMoveUpCommand(moveUp);
							keyBoardInput = new KeyBoardInput(moveUpCommand, stopMoveUpCommand, player);
							keyBoardInput.pressedCommand();
							break;
						case 1:
							MoveLeft moveLeft = new MoveLeft();
							MoveLeftCommand moveLeftCommand = new MoveLeftCommand(moveLeft);
							StopMoveLeftCommand stopMoveLeftCommand = new StopMoveLeftCommand(moveLeft);
							keyBoardInput = new KeyBoardInput(moveLeftCommand, stopMoveLeftCommand, player);
							keyBoardInput.pressedCommand();
							break;
						case 2:
							MoveDown moveDown = new MoveDown();
							MoveDownCommand moveDownCommand = new MoveDownCommand(moveDown);
							StopMoveDownCommand stopMoveDownCommand = new StopMoveDownCommand(moveDown);
							keyBoardInput = new KeyBoardInput(moveDownCommand, stopMoveDownCommand, player);
							keyBoardInput.pressedCommand();
							break;
						case 3:
							MoveRight moveRight = new MoveRight();
							MoveRightCommand moveRightCommand = new MoveRightCommand(moveRight);
							StopMoveRightCommand stopMoveRightCommand = new StopMoveRightCommand(moveRight);
							keyBoardInput = new KeyBoardInput(moveRightCommand, stopMoveRightCommand, player);
							keyBoardInput.pressedCommand();
							break;
						case 4:
							EquipSword equipSword = new EquipSword();
							EquipSwordCommand EquipSwordCommand = new EquipSwordCommand(equipSword);
							keyBoardInput = new KeyBoardInput(EquipSwordCommand, player);
							keyBoardInput.pressedCommand();
							break;
						case 5:
							EquipBow equipBow = new EquipBow();
							EquipBowCommand EquipBowCommand = new EquipBowCommand(equipBow);
							keyBoardInput = new KeyBoardInput(EquipBowCommand, player);
							keyBoardInput.pressedCommand();
							break;
						case 6:
							EquipMagic equipMagic = new EquipMagic();
							EquipMagicCommand EquipMagicCommand = new EquipMagicCommand(equipMagic);
							keyBoardInput = new KeyBoardInput(EquipMagicCommand, player);
							keyBoardInput.pressedCommand();
							break;
						default:
							break;
						}
					}
				}
			}
		});

		gameCanvas.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (player.isAlive()) {
					Integer command = keyMap.get(event.getCode());
					KeyBoardInput keyBoardInput = new KeyBoardInput();
					if (command != null) {
						switch (command) {
						case 0:
							MoveUp moveUp = new MoveUp();
							MoveUpCommand moveUpCommand = new MoveUpCommand(moveUp);
							StopMoveUpCommand stopMoveUpCommand = new StopMoveUpCommand(moveUp);
							keyBoardInput = new KeyBoardInput(moveUpCommand, stopMoveUpCommand, player);
							keyBoardInput.releasedCommand();
							break;
						case 1:
							MoveLeft moveLeft = new MoveLeft();
							MoveLeftCommand moveLeftCommand = new MoveLeftCommand(moveLeft);
							StopMoveLeftCommand stopMoveLeftCommand = new StopMoveLeftCommand(moveLeft);
							keyBoardInput = new KeyBoardInput(moveLeftCommand, stopMoveLeftCommand, player);
							keyBoardInput.releasedCommand();
							break;
						case 2:
							MoveDown moveDown = new MoveDown();
							MoveDownCommand moveDownCommand = new MoveDownCommand(moveDown);
							StopMoveDownCommand stopMoveDownCommand = new StopMoveDownCommand(moveDown);
							keyBoardInput = new KeyBoardInput(moveDownCommand, stopMoveDownCommand, player);
							keyBoardInput.releasedCommand();
							break;
						case 3:
							MoveRight moveRight = new MoveRight();
							MoveRightCommand moveRightCommand = new MoveRightCommand(moveRight);
							StopMoveRightCommand stopMoveRightCommand = new StopMoveRightCommand(moveRight);
							keyBoardInput = new KeyBoardInput(moveRightCommand, stopMoveRightCommand, player);
							keyBoardInput.releasedCommand();
							break;
						default:
							break;
						}
					}
				}
			}
		});

		if (player.getDirPressed().isEmpty() && player.getInvulnerableTime() == 0.0) {
			player.getEntitySprite().setVelocity(new Vector());
			player.getSword().getEntitySprite().setVelocity(new Vector());
		}
	}

	/**
	 * Handles any mouse input from the user. Mouse inputs may cause a weapon to
	 * activate.
	 * 
	 * @param gameCanvas
	 * @param player
	 * @param gameRenderer
	 */
	public void checkAttack(Canvas gameCanvas, Player player, Renderer gameRenderer) {
		gameCanvas.requestFocus();
		gameCanvas.setFocusTraversable(true);
		gameCanvas.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (player.isAlive()) {
					if (player.getEquippedWeapon() == weapon.SWORD) {
						swordAttack(player, event);
					} else if (player.getEquippedWeapon() == weapon.BOW) {
						bowAttack(player, event);
					} else if (player.getEquippedWeapon() == weapon.MAGIC) {
						magicAttack(player, event);
					}
				}
			}
		});
	}

	/**
	 * Displays sword swing on the screen. Its position and rotation are determined
	 * by angle between the player and the mouse click.
	 * 
	 * @param player
	 * @param event
	 */
	public void swordAttack(Player player, MouseEvent event) {
		List<GenericEntity> entities = retriever.getEntities().stream().flatMap(List::stream)
				.collect(Collectors.toList());

		for (int i = 0; i < entities.size(); i++) { // Find entity of this sprite
			if (entities.get(i).getEntityType() == Type.PLAYER) {
				if ((Player) entities.get(i) == player) {
					MeleeCommands.deployMeleeWeapon(player, new Vector(event.getX(), event.getY()));
					if (player.getAtkCooldown() > 0.0)
						return;
					networkClient.sendByteArray(7, prepareToSendData(i, 1));
					break;
				}
			}
		}
		// MeleeCommands.deployMeleeWeapon((ComplexEntity) player, new
		// Vector(event.getX(), event.getY()));
	}

	/**
	 * Displays a moving arrow on the screen. Its position, direction and rotation
	 * are determined by angle between the player and the mouse click.
	 * 
	 * @param player
	 * @param event
	 */
	public void bowAttack(Player player, MouseEvent event) {
		if (player.getAtkCooldown() == 0.0 && player.getArrows() != 0) {
			RangedCommands.projAttack(player, new Vector(event.getX(), event.getY()));
		}
	}

	/**
	 * Displays a moving magic attack on the screen. Its position and direction are
	 * determined by angle between the player and the mouse click. On collision, the
	 * magic attack stops moving and size of the attack increases for a short period
	 * of time before disappearing.
	 * 
	 * @param player
	 * @param event
	 */
	public void magicAttack(Player player, MouseEvent event) {
		if (player.getMagicPoints() >= 50 && player.getAtkCooldown() == 0.0) {
			Vector target = new Vector(event.getX(), event.getY());
			RangedCommands.projAttack(player, target);
			player.setMagicPoints(player.getMagicPoints() - 50);
		}
	}

	private String prepareToSendData(Object... values) {
		return Arrays.stream(values).map(x -> x.toString()).collect(Collectors.joining(","));
	}
}