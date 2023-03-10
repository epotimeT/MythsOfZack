package com.game.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import com.game.collision.Hitbox;
import com.game.collision.Polygon;
import com.game.entities.items.BundleItem;
import com.game.graphics.CollisionHandler;
import com.game.graphics.EdgeCollisionHandler;
import com.game.graphics.SideOfScreen;
import com.game.graphics.Sprite;
import com.game.utilities.Vector;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Player extends ComplexEntity {
	public enum weapon {
		SWORD, BOW, MAGIC
	};

	private weapon equippedWeapon = weapon.SWORD;

	public enum directionPressed {
		UP, LEFT, DOWN, RIGHT
	};

	private ArrayList<directionPressed> dirPressed = new ArrayList<directionPressed>();
	private int arrows;
	private ArrowProjectile arrow = new ArrowProjectile();
	private Magic magic = new Magic();
	private boolean alive = true;

	private Sprite healthBar;
	private Sprite magicBar;
	private float magicPoints;
	private int wait;

	public Sprite atkCooldownLabel;
	public Sprite speedLabel;
	public Sprite shieldLabel;
	public Sprite swordDmgLabel;
	public Sprite bowDmgLabel;
	public Sprite magicDmgLabel;
	public Sprite swordlabel;
	public Sprite bowlabel;
	public Sprite magiclabel;
	public Sprite controls;
	public Sprite arrowcount;
	public Sprite currentsprite;

	// public ArrayList<Image> spritelist = createList();
	public Timeline timeline;

	/***
	 * A constructor for the Player. Creates a player object.
	 * 
	 * @param position - Position to spawn player.
	 * @param screenId - Screen to spawn player.
	 */
	public Player(Vector position, int screenId, String name, int color) {
		ArrayList<Image> spritelist = createList(color);
		Vector size = new Vector(50.0, 50.0);
		// Sprite(position, velocity, size, Images)
		currentsprite = new Sprite(position, new Vector(0.0, 0.0), size, spritelist);
		currentsprite.setName(name);

		// currentsprite.setCurrentSprite(1);
		setEntitySprite(currentsprite);
		setHitbox(new Hitbox(Arrays.asList(new Polygon(
				Arrays.asList(new Vector(size.x() / 2, size.y() / 2), new Vector(-size.x() / 2, size.y() / 2),
						new Vector(-size.x() / 2, -size.y() / 2), new Vector(size.x() / 2, -size.y() / 2))))));
		setHealth(200f);
		setPhysDmg(40f);
		setMagDmg(100f);
		setPhysDef(2f);
		setSpeed(2f);
		setMaxSpeed(2f);
		setArrows(10);
		setMagicPoints(100);
		setEntityType(Type.PLAYER);
		setVelocity(new Vector());
		setProjectile(new ArrowProjectile());
		this.screenId = screenId;
		setExtraArrowDmg(0);
		setExtraMagicDmg(0);
		setExtraSwordDmg(0);

		addEntity(screenId, this);

		setSword(new Sword());

		this.getEntitySprite().registerCallbackOnCollision(new CollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, Sprite otherSprite) {
				ComplexEntityCollision.collision(thisSprite, otherSprite);
			}
		});

		this.getEntitySprite().registerCallbackOnEdgeCollision(new EdgeCollisionHandler() {
			@Override
			public void onCollision(Sprite thisSprite, SideOfScreen edge) {
				ComplexEntityCollision.screenCollision(thisSprite, edge);
			}
		});

		swordlabel = new Sprite(new Vector(950, 35), new Vector(), new Vector(20.0, 50.0), loadImage("sword"));
		swordlabel.setCount("KEY 1");
		swordlabel.setCountColor(Color.CHARTREUSE);
		bowlabel = new Sprite(new Vector(1000, 35), new Vector(), new Vector(15.0, 50.0), loadImage("bow"));
		bowlabel.setCount("KEY 2");
		magiclabel = new Sprite(new Vector(1050, 35), new Vector(), new Vector(50.0, 50.0), loadImage("magic"));
		magiclabel.setCount("KEY 3");
		arrowcount = new Sprite(new Vector(250, 35), new Vector(), new Vector(15.0, 50.0), loadImage("arrow"));
		arrowcount.setCount(Integer.toString(arrows));
		healthBar = new Sprite(new Vector(30, 30), new Vector(), new Vector(200.0, 20.0), loadImage("healthbar"));
		healthBar.setDescription("HP ");
		magicBar = new Sprite(new Vector(30, 55), new Vector(), new Vector(100.0, 20.0), loadImage("mpbar"));
		magicBar.setDescription("MP ");
		controls = new Sprite(new Vector(830, 50), new Vector(), new Vector(100.0, 100.0), loadImage("controls"));
		controls.setDescription("Movement");

		swordDmgLabel = new Sprite(new Vector(370, 10), new Vector(), new Vector(10.0, 25.0), loadImage("sword"));
		swordDmgLabel.setDescription((40 + getExtraSwordDmg()) + " Damage");
		bowDmgLabel = new Sprite(new Vector(370, 40), new Vector(), new Vector(10.0, 25.0), loadImage("bow"));
		bowDmgLabel.setDescription((40 + getExtraArrowDmg()) + " Damage");
		magicDmgLabel = new Sprite(new Vector(370, 70), new Vector(), new Vector(25.0, 25.0), loadImage("magic"));
		magicDmgLabel.setDescription((100 + getExtraMagicDmg()) + " Damage");

		shieldLabel = new Sprite(new Vector(520, 10), new Vector(), new Vector(25.0, 25.0), loadImage("shield"));
		shieldLabel.setDescription(getDefence() + " Defence");
		speedLabel = new Sprite(new Vector(520, 40), new Vector(), new Vector(25.0, 25.0), loadImage("speedItem"));
		speedLabel.setDescription(getSpeed() + " Speed");
		atkCooldownLabel = new Sprite(new Vector(520, 70), new Vector(), new Vector(10.0, 25.0), loadImage("sword"));
		atkCooldownLabel.setDescription("Atk: Ready");
	}

	/***
	 * Update player values and check player health.
	 */
	public void updateState() {
		if (getInvulnerableTime() > 0) {
			setInvulnerableTime(getInvulnerableTime() - 1);
		}

		if (getAtkCooldown() > 0.0) {
			atkCooldownLabel.setDescription("Atk: Cooldown");
			setAtkCooldown((float) (getAtkCooldown() - 1.0));
		}

		if (getAtkCooldown() <= 0.0) {
			if (isSwordSwung()) {
				getSword().setPosition(new Vector(10000, 10000));
				setSwordSwung(false);
			}
			atkCooldownLabel.setDescription("Atk: Ready");
		}

		if (getHealth() <= 0) {
			// TODO removeSprite sword, player, health
			setAlive(false);
			// this.removeEntity(this);

			// getSfx().PlayTrack(3);

			if (getArrows() > 5)
				setArrows(5);
			if ((getExtraArrowDmg() + getArrows() + (getDefence() - 2) + (getSpeed() - 2) + getExtraSwordDmg()) != 0) {
				float[] buffList = { getExtraArrowDmg(), (float) getArrows(), getDefence() - 2f,
						(float) (getSpeed() - 2f), getExtraSwordDmg() };

				BundleItem bundleItem = new BundleItem(new Vector(getPosition().x() + 5.0, getPosition().y() + 5.0),
						buffList, screenId);
				// TODO addSprite
			}
		}
		// 16.33 milliseconds a frame. 30*16.33 ~ 0.5 secs
		if (wait == 0) { // Increase MP by 1 every half a second ish
			if (getMagicPoints() > 100) {
				setMagicPoints(100);
			}
			if (getMagicPoints() < 100) {
				setMagicPoints(getMagicPoints() + 1);
				wait = 30;
			}
		} else {
			wait -= 1;
		}

		healthBar.setPreferSize(new Vector(getHealth(), 20.0));
		magicBar.setPreferSize(new Vector(getMagicPoints(), 20.0));
		arrowcount.setCount(Integer.toString(arrows));
		swordDmgLabel.setDescription((40 + getExtraSwordDmg()) + " Damage");
		bowDmgLabel.setDescription((40 + getExtraArrowDmg()) + " Damage");
		magicDmgLabel.setDescription((100 + getExtraMagicDmg()) + " Damage");
		shieldLabel.setDescription(getDefence() + " Defence");
		speedLabel.setDescription(getSpeed() + " Speed");
	}

	/***
	 * Update both the player and sword velocity.
	 * 
	 * @param vel - Velocity to be added to the player.
	 */
	public void updatePlayerSwordVel(Vector vel) {
		getEntitySprite().updateVelX(vel.x());
		getEntitySprite().updateVelY(vel.y());
		getSword().getEntitySprite().updateVelX(vel.x());
		getSword().getEntitySprite().updateVelY(vel.y());
	}

	public Sprite getAtkCooldownLabel() {
		return atkCooldownLabel;
	}

	public void setAtkCooldownLabel(Sprite atkCooldownLabel) {
		this.atkCooldownLabel = atkCooldownLabel;
	}

	public Sprite getSwordDmgLabel() {
		return swordDmgLabel;
	}

	public Sprite getSpeedLabel() {
		return speedLabel;
	}

	public void setSpeedLabel(Sprite speedLabel) {
		this.speedLabel = speedLabel;
	}

	public Sprite getShieldLabel() {
		return shieldLabel;
	}

	public void setShieldLabel(Sprite shieldLabel) {
		this.shieldLabel = shieldLabel;
	}

	public void setSwordDmgLabel(Sprite swordDmgLabel) {
		this.swordDmgLabel = swordDmgLabel;
	}

	public Sprite getBowDmgLabel() {
		return bowDmgLabel;
	}

	public void setBowDmgLabel(Sprite bowDmgLabel) {
		this.bowDmgLabel = bowDmgLabel;
	}

	public Sprite getMagicDmgLabel() {
		return magicDmgLabel;
	}

	public void setMagicDmgLabel(Sprite magicDmgLabel) {
		this.magicDmgLabel = magicDmgLabel;
	}

	/***
	 * Changes the player image to make the player look like it walks.
	 */
	public void animate(int count, int imageIndex) {

		timeline = new Timeline(new KeyFrame(Duration.millis(200), new EventHandler<ActionEvent>() {
			int index = imageIndex;

			@Override
			public void handle(ActionEvent event) {
				currentsprite.setCurrentSprite(index);
				index++;
				if (index == (imageIndex + count)) {
					index = imageIndex;
				}
				// imageView.setImage(images.get(imageIndex++));
			}

		}));

		timeline.setCycleCount(Animation.INDEFINITE);

	}

	public ArrayList<Image> loadImage(String name) {

		ArrayList<Image> sprites = new ArrayList<Image>();

		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File("res/graphics/" + name + ".png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sprites.add(new Image(inputstream));

		return sprites;
	}

	public ArrayList<Image> createList(int n) {
		ArrayList<Image> sprites = new ArrayList<Image>();

		for (int i = 0; i <= 12; i++) {
			FileInputStream inputstream = null;
			try {
				inputstream = new FileInputStream(
						new File("res/graphics/character" + Integer.toString(n) + "/" + Integer.toString(i) + ".png"));

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sprites.add(new Image(inputstream));

		}
		return sprites;
	}

	public float getMagicPoints() {
		return magicPoints;
	}

	public void setMagicPoints(float magicPoints) {
		this.magicPoints = magicPoints;
	}

	public Sprite getHealthBar() {
		return healthBar;
	}

	public Sprite getMagicBar() {
		return magicBar;
	}

	public Sprite getArrowcount() {
		return arrowcount;
	}

	public Sprite getSwordLabel() {
		return swordlabel;
	}

	public Sprite getBowLabel() {
		return bowlabel;
	}

	public Sprite getMagicLabel() {
		return magiclabel;
	}

	public Sprite getControls() {
		return controls;
	}

	public void setHealthBar() {
		healthBar.setPreferSize(new Vector(getHealth(), 20.0));
	}

	public void setArrowCount() {
		arrowcount.setCount(Integer.toString(arrows));
	}

	public Magic getMagic() {
		return magic;
	}

	public void setMagic(Magic magic) {
		this.magic = magic;
	}

	public void removeMagic() {
		this.magic = null;
	}

	public ArrowProjectile getArrow() {
		return arrow;
	}

	public void setArrow(ArrowProjectile arrow) {
		this.arrow = arrow;
	}

	public void removeArrow() {
		this.arrow = null;
	}

	public ArrayList<directionPressed> getDirPressed() {
		return dirPressed;
	}

	public void addDirPressed(directionPressed dirPressed) {
		this.dirPressed.add(dirPressed);
	}

	public void removeDirPressed(directionPressed dirPressed) {
		this.dirPressed.remove(dirPressed);
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void setArrows(int arrows) {
		this.arrows = arrows;
	}

	public weapon getEquippedWeapon() {
		return equippedWeapon;
	}

	public void setEquippedWeapon(weapon newWeapon) {
		equippedWeapon = newWeapon;
	}

	public double getArrows() {
		return arrows;
	}

	public void addArrows(int extraArrows) {
		arrows += extraArrows;
	}

	@Override
	public void projAttack() {
		// Achieved in input handler
	}
}
