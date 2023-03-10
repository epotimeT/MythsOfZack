package com.game.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import com.game.ai.Slime;
import com.game.collision.Hitbox;
import com.game.commands.MeleeCommands;
import com.game.commands.RangedCommands;
import com.game.entities.ArrowProjectile;
import com.game.entities.GenericEntity;
import com.game.entities.GenericEntity.Type;
import com.game.entities.GenericMonster.State;
import com.game.entities.GenericProjectile;
import com.game.entities.Magic;
import com.game.entities.Player;
import com.game.entities.Rock;
import com.game.entities.RockProjectile;
import com.game.entities.SpikyRock;
import com.game.entities.Sword;
import com.game.entities.items.BowArrowItem;
import com.game.entities.items.DefenceItem;
import com.game.entities.items.SpeedItem;
import com.game.entities.items.SwordItem;
import com.game.graphics.Renderer;
import com.game.graphics.Sprite;
import com.game.sound.EntitySFX;
import com.game.utilities.Vector;

import javafx.scene.canvas.Canvas;

/**
 * <h1>EntitiesPackage</h1> Unit tests for the classes contained in the package
 * 'Entities' Some of these tests require javafx to run
 */
class EntitiesPackage {
	@Rule
	private Timeout entitiesTimeout = new Timeout(200, TimeUnit.MILLISECONDS);

	@Rule
	private double precision = CollisionPackage.precision;

	// Testing ArrowProjecticle
	@Test
	void testGetThisArrowProjectile() {
		ArrowProjectile arrow = new ArrowProjectile();

		assertSame(arrow.getThisArrow(), arrow, "Getting the arrow returns a different object");
	}

	@Test
	void testArrowShoot() {
		ArrowProjectile arrow = new ArrowProjectile();
		Slime slime = new Slime(new Vector(), 0); // Slime cannot use arrows in the game, but is good for testing
		GenericProjectile proj = arrow.shoot(new Vector(0, 0), new Vector(1, 1), 45.0, 10.0, slime);

		assertSame(proj.getOwner(), slime, "Slime is not the owner when it should be");
		assertEquals(proj.getPosition(), new Vector(0, 0), "Position of new arrow set wrong");
		assertEquals(proj.getVelocity(), new Vector(1, 1), "Velocity of new arrow set wrong");
		assertEquals(proj.getAngleDeg(), 45.0, "Angle Degree of new arrow set wrong");

	}

	// Testing ComplexEntity
	// because this is an abstract class, Player (from logic) will be used to test
	// it
	// it is assumed that if the methods work for one subclass, they will work for
	// all others
	@Test
	void testSetStats() {
		Player player = new Player(new Vector(), 0, "name", 1);

		List<Float> stats = List.of(10F, 20F, 30F, 40F, 50F, 60F, 70F);
		player.setStats(stats);

		assertEquals(player.getHealth(), 10F, "Health doesn't match stats list");
		assertEquals(player.getPhysDmg(), 20F, "PhysDmg doesn't match stats list");
		assertEquals(player.getMagDmg(), 30F, "MagDmg doesn't match stats list");
		assertEquals(player.getDefence(), 40F, "PhysDef doesn't match stats list");
		assertEquals(player.getSpeed(), 60F, "Speed doesn't match stats list");
		assertEquals(player.getMaxSpeed(), 70F, "MaxSpeed doesn't match stats list");
		player.removeEntity(player);
	}

	@Test
	void testSFX() {
		Player player = new Player(new Vector(), 0, "name", 1);
		EntitySFX sfx = new EntitySFX(player, 1.0f, 1);

		player.setSfx(sfx);

		assertSame(player.getSfx(), sfx, "SFX getter/setter incorrect");
	}

	@Test
	void testSwordSwung() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setSwordSwung(true);

		assertTrue(player.isSwordSwung(), "SwordSwung getter/setter incorrect");
	}

	void testInvulnerableTime() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setInvulnerableTime(100);

		assertEquals(player.getInvulnerableTime(), 100, "InvulnerableTime getter/setter incorrect");
	}

	@Test
	void testSword() {
		Player player = new Player(new Vector(), 0, "name", 1);
		Sword sword = new Sword();

		player.setSword(sword);

		assertSame(player.getSword(), sword, "Sword getter/setter incorrect");
	}

	@Test
	void testExtraSwordDmg() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setExtraSwordDmg(100);

		assertEquals(player.getExtraSwordDmg(), 100, "ExtraSwordDmg getter/setter incorrect");
	}

	@Test
	void testExtraArrowDmg() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setExtraArrowDmg(100);

		assertEquals(player.getExtraArrowDmg(), 100, "ExtraArrowDmg getter/setter incorrect");
	}

	@Test
	void testExtraMagicDmg() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setExtraMagicDmg(100);

		assertEquals(player.getExtraMagicDmg(), 100, "ExtraMagicDmg getter/setter incorrect");
	}

	@Test
	void testPhysDmg() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setPhysDmg(100);
		assertEquals(player.getPhysDmg(), 100, "PhysDmg getter/setter incorrect");
		player.removeEntity(player);
	}

	@Test
	void testMagDmg() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setMagDmg(100);
		assertEquals(player.getMagDmg(), 100, "MagDmg getter/setter incorrect");
		player.removeEntity(player);
	}

	@Test
	void testPhysDef() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setPhysDef(100);
		assertEquals(player.getDefence(), 100, "Defence getter/setter incorrect");

		player.removeEntity(player);
	}

	@Test
	void testCompEntSpeed() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setSpeed(100);
		assertEquals(player.getSpeed(), 100, "Speed getter/setter incorrect");
		player.removeEntity(player);
	}

	@Test
	void testCompEntMaxSpeed() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setMaxSpeed(100);
		assertEquals(player.getMaxSpeed(), 100, "Health getter/setter incorrect");
		player.removeEntity(player);
	}

	@Test
	void testHealth() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setHealth(100);
		assertEquals(player.getHealth(), 100, "Health getter/setter incorrect");
		player.removeEntity(player);
	}

	@Test
	void testCompEntVelocity() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setVelocity(new Vector(10, 10));
		assertEquals(player.getVelocity(), new Vector(10, 10), "Velocity getter/setter incorrect");
		player.removeEntity(player);
	}

	@Test
	void testAtkCooldown() {
		Player player = new Player(new Vector(), 0, "name", 1);

		player.setAtkCooldown(100);
		assertEquals(player.getAtkCooldown(), 100, "AtkCooldown getter/setter incorrect");
		player.removeEntity(player);
	}

	@Test
	void testCooldownTime() {
		Player player = new Player(new Vector(), 0, "name", 1);
		player.setCooldownTime(100);
		assertEquals(player.getCooldownTime(), 100, "GetCooldownTime getter/setter incorrect");
		player.removeEntity(player);
	}

	@Test
	void testMeleeAttack() {
		Player player = new Player(new Vector(), 0, "name", 1);

		assertFalse(player.isMeleeAttack(), "Melee Attack should start as false");
		player.removeEntity(player);
	}

	@Test
	void testArrowAttack() {
		Player player = new Player(new Vector(), 0, "name", 1);

		assertFalse(player.isArrowAttack(), "Arrow Attack should start as false");
		player.removeEntity(player);
	}

	@Test
	void testMagAttack() {
		Player player = new Player(new Vector(), 0, "name", 1);

		assertFalse(player.isMagAttack(), "Mag Attack should start as false");
		player.removeEntity(player);
	}

	@Test
	void testProjectile() {
		Player player = new Player(new Vector(), 0, "name", 1);
		ArrowProjectile arrow = new ArrowProjectile();

		player.setProjectile(arrow);
		assertSame(player.getProjectile(), arrow, "Projectile getter/setter incorrect");
		player.removeEntity(player);
	}

	// ProjAttack() is abstract so will be unit tested in each separate subclass if
	// appropriate

	@Test
	void testProjectileList() {
		Player player = new Player(new Vector(), 0, "name", 1);
		ArrowProjectile arrow = new ArrowProjectile();

		List<GenericProjectile> expectedList = List.of(arrow);

		player.addProjectile(arrow);
		assertEquals(player.getProjectileList(), expectedList, "Projectile List adds arrow incorrectly");

		player.addProjectile(arrow);
		assertEquals(player.getProjectileList(), expectedList, "Projectile List adds second arrow incorrectly");

		List<GenericProjectile> expectedList2 = List.of();

		player.removeProjectile(arrow);
		assertEquals(player.getProjectileList(), expectedList2, "Projectile List removes arrow incorrectly");

		player.removeProjectile(arrow);
		assertEquals(player.getProjectileList(), expectedList2, "Projectile removed from Projectile List twice");
		player.removeEntity(player);
	}

	// ComplexEntityCollision has no methods that would benefit from unit testing

	// EntityCommonStats is an interface with no implemented methods

	// Testing GenericEntity
	// because this is an abstract class, Rock will be used to test the methods
	// it is assumed that if the methods work for one subclass, they will work for
	// all others
	@Test
	void testPosition() {
		Rock rock = new Rock(new Vector(0, 0), new Vector(1, 3), 0);

		rock.setPosition(new Vector(5, 5));
		assertEquals(rock.getPosition(), new Vector(5, 5), "Position getter/setter incorrect");
	}

	@Test
	void testScreenId() {
		Rock rock = new Rock(new Vector(0, 0), new Vector(1, 3), 0);

		rock.setScreenId(4);
		assertEquals(rock.getScreenId(), 4, "ScreenId getter/setter incorrect");
	}

	@Test
	void testEntitySprite() {
		Rock rock = new Rock(new Vector(0, 0), new Vector(1, 3), 0);
		Sprite sprite = new Sprite();

		rock.setEntitySprite(sprite);
		assertSame(rock.getEntitySprite(), sprite, "EntitySprite getter/setter incorrect");
	}

	@Test
	void testHitbox() {
		Rock rock = new Rock(new Vector(0, 0), new Vector(1, 3), 0);
		Hitbox box = new Hitbox(null);

		rock.setHitbox(box);
		assertSame(rock.getHitbox(), box, "Hitbox getter/setter incorrect");
	}

	// getRenderer(), setRenderer(), getEntities() and removeEntity() cannot be
	// tested effectively
	// via unit testing

	@Test
	void testEntityType() {
		Rock rock = new Rock(new Vector(0, 0), new Vector(1, 3), 0);

		rock.setEntityType(Type.PROJECTILE);
		assertEquals(rock.getEntityType(), Type.PROJECTILE, "EntityType getter/setter incorrect");
	}

	@Test
	void testGenEntVelocity() {
		Rock rock = new Rock(new Vector(0, 0), new Vector(1, 3), 0);

		rock.setVelocity(new Vector(5, 5));
		assertEquals(rock.getVelocity(), new Vector(5, 5), "Velocity getter/setter incorrect");
	}

	// Testing GenericMonster
	// because this is an abstract class, Slime will be used to test the methods
	// it is assumed that if the methods work for one subclass, they will work for
	// all others
	@Test
	void testDetectionRadius() {
		Slime slime = new Slime(new Vector(), 0);

		slime.setDetectionRadius(100);
		assertEquals(slime.getDetectionRadius(), 100, "DetectionRadius getter/setter incorrect");
	}

	@Test
	void testGetPlayerLocations() {
		Slime slime = new Slime(new Vector(), 0);
		Vector expPos = new Vector(25, 25);

		Player player = new Player(new Vector(), 0, "name", 1);
		Player player2 = new Player(new Vector(10, 15), 0, "name", 1);

		List<GenericEntity> inputList = List.of(slime, player, player2);
		List<Vector> expectedList = List.of(player.getPosition().add(expPos), player2.getPosition().add(expPos));

		assertEquals(slime.getPlayerLocations(inputList), expectedList,
				"PlayerLocations list differs from expected Vector positions");

	}

	@Test
	void testClosestPlayer() {
		Slime slime = new Slime(new Vector(), 0);
		slime.setPosition(new Vector(3, 3));

		List<Vector> vectors = List.of(new Vector(0, 0), new Vector(-23, 12), new Vector(9, 2));

		assertEquals(slime.ClosestPlayer(vectors), new Vector(0, 0), "Closest vector is incorrect");
	}

	@Test
	void testGetDistance() {
		Slime slime = new Slime(new Vector(), 0);
		slime.setPosition(new Vector(3, 3));

		Vector target = new Vector(-4, 6);
		Vector target2 = new Vector(0, 6);

		assertEquals(slime.getDistance(target), 7.615773, precision, "Calculated distance from vector incorrect");

		assertEquals(slime.getDistance(target, target2), 4, precision, "Calculated distance from vector incorrect");
	}

	// getClosestPlayer() cannot be tested effectively via unit testing

	@Test
	void testCurrState() {
		Slime slime = new Slime(new Vector(), 0);

		assertEquals(slime.getCurrState(), State.IDLE, "Standard State should be idle");
	}

	// facing() is an abstract method so will be unit tested by its subclasses as
	// needed

	// death() cannot be tested effectively via unit testing

	// Testing GenericProjectile
	// because this is an abstract class, Arrow will be used to test the methods
	// it is assumed that if the methods work for one subclass, they will work for
	// all others
	@Test
	void testGenProjKnockback() {
		ArrowProjectile arrow = new ArrowProjectile();

		arrow.setKnockback(100.0f);
		assertEquals(arrow.getKnockback(), 100.0f, "Knockback getter/setter incorrect");
	}

	@Test
	void testGenProjDamage() {
		ArrowProjectile arrow = new ArrowProjectile();

		arrow.setDamage(100);
		assertEquals(arrow.getDamage(), 100, "Damage getter/setter incorrect");
	}

	@Test
	void testFireRate() {
		ArrowProjectile arrow = new ArrowProjectile();

		arrow.setFireRate(100);
		assertEquals(arrow.getFireRate(), 100, "FireRate getter/setter incorrect");
	}

	@Test
	void testProjMaxSpeed() {
		ArrowProjectile arrow = new ArrowProjectile();

		arrow.setMaxSpeed(100);
		assertEquals(arrow.getMaxSpeed(), 100, "MaxSpeed getter/setter incorrect");
	}

	@Test
	void testRotVel() {
		ArrowProjectile arrow = new ArrowProjectile();

		arrow.setRotVel(100);
		assertEquals(arrow.getRotVel(), 100, "RotVel getter/setter incorrect");
	}

	@Test
	void testSize() {
		ArrowProjectile arrow = new ArrowProjectile();

		arrow.setSize(new Vector(1, 1));
		assertEquals(arrow.getSize(), new Vector(1, 1), "Size getter/setter incorrect");
	}

	@Test
	void testAngleDeg() {
		ArrowProjectile arrow = new ArrowProjectile();

		arrow.setAngleDeg(100);
		assertEquals(arrow.getAngleDeg(), 100, "AngleDeg getter/setter incorrect");
	}

	// Testing getOwner(), setOwner() and removeFromOwner() cannot be tested
	// effectively via unit testing

	@Test
	void testProjSpeed() {
		ArrowProjectile arrow = new ArrowProjectile();

		arrow.setSpeed(100);
		assertEquals(arrow.getSpeed(), 100, "Speed getter/setter incorrect");
	}

	// shoot() is an abstract method so will be unit tested by its subclasses as
	// needed

	// Testing GenericWeapon
	// because this is an abstract class, Sword will be used to test the methods
	// it is assumed that if the methods work for one subclass, they will work for
	// all other others
	@Test
	void testGenWepKnockback() {
		Sword sword = new Sword();

		sword.setKnockback(100.0f);
		assertEquals(sword.getKnockback(), 100.0f, "Knockback getter/setter incorrect");
	}

	@Test
	void testGenWepDamage() {
		Sword sword = new Sword();

		sword.setDamage(100F);
		assertEquals(sword.getDamage(), 100F, "Damage getter/setter incorrect");
	}

	@Test
	void testAttackRate() {
		Sword sword = new Sword();

		sword.setAttackRate(100F);
		assertEquals(sword.getAttackRate(), 100F, "AttackRate getter/setter incorrect");
	}

	// Testing Magic
	@Test
	void testGetThisMagic() {
		Magic magic = new Magic();

		assertSame(magic.getThisMagic(), magic, "Magic retrieves the wrong magic object");
	}

	// detonateMagic() cannot be tested effectively via unit testing

	@Test
	void testMagicShoot() {
		Magic magic = new Magic();
		Slime slime = new Slime(new Vector(), 0); /// Slime cannot use magic in the game, but is good for testing
		GenericProjectile proj = magic.shoot(new Vector(0, 0), new Vector(1, 1), 45.0, 10.0, slime);

		assertSame(proj.getOwner(), slime, "Slime is not the owner when it should be");
		assertEquals(proj.getPosition(), new Vector(0, 0), "Position of new magic set wrong");
		assertEquals(proj.getVelocity(), new Vector(1, 1), "Velocity of new magic set wrong");
		assertEquals(proj.getAngleDeg(), 45.0, "Angle Degree of new magic set wrong");
	}

	@Test
	void testSfx() {
		Magic magic = new Magic();
		EntitySFX sfx = new EntitySFX(magic, 1.0f, 1);

		magic.setSfx(sfx);

		assertSame(magic.getSfx(), sfx, "Sfx getter/setter incorrect");
	}

	@Test
	void testDetonated() {
		Magic magic = new Magic();

		magic.setDetonated(true);

		assertTrue(magic.isDetonated(), "Detonated getter/setter incorrect");
	}

	// loadImage() cannot be tested effectively via unit testing

	// Testing Player
	@Rule
	private Player player = new Player(new Vector(), 0, "name", 1);
	private Canvas canvas = new Canvas(1200, 700);
	private Renderer gameRenderer = new Renderer(canvas);

	// updateState() and updatePlayerSwordVel() cannot be tested effectively via
	// unit testing

	@Test
	void testAtkCooldownLabel() {
		player.setSfx(new EntitySFX(player, 1200, 0));
		MeleeCommands.deployMeleeWeapon(player, new Vector());
		player.updateState();

		assertEquals(player.getAtkCooldownLabel().getDescription(), "Atk: Cooldown",
				"AtkCooldownLabel incorrect for sword swing");
		assertEquals(player.getAtkCooldown(), 29, "AtkCooldown value incorrect for sword swing");
	}

	@Test
	void testSwordDmgLabel() {
		player.setSfx(new EntitySFX(player, 1200, 0));

		assertEquals(player.getSwordDmgLabel().getDescription(), "40 Damage",
				"SwordDmgLabel incorrect at the start of the game");
		assertEquals(player.getSword().getDamage(), 40, "SwordDmg incorrect at the start of the game");

		SwordItem item = new SwordItem(new Vector(), 0, gameRenderer);
		player.setExtraSwordDmg(player.getExtraSwordDmg() + item.getExtraDmg());

		assertEquals(player.getSword().getDamage() + player.getExtraSwordDmg(), 50,
				"SwordDmg incorrect after one sword item pickup");

		player.setExtraSwordDmg(player.getExtraSwordDmg() + item.getExtraDmg());

		assertEquals(player.getSword().getDamage() + player.getExtraSwordDmg(), 60,
				"SwordDmg incorrect after two sword item pickups");

		player.updateState();
		assertEquals(player.getSwordDmgLabel().getDescription(), "60 Damage",
				"SwordDmgLabel incorrect after two sword item pickups");
	}

	@Test
	void testSpeedLabel() {
		player.setSfx(new EntitySFX(player, 1200, 0));

		assertEquals(player.getSpeedLabel().getDescription(), "2.0 Speed",
				"SpeedLabel incorrect at the start of the game");

		SpeedItem item = new SpeedItem(new Vector(), 0, gameRenderer);
		player.setSpeed(player.getSpeed() + item.getExtraSpeed());

		assertEquals(player.getSpeed(), 2.5, "Speed incorrect after one speed item pickup");

		player.updateState();

		assertEquals(player.getSpeedLabel().getDescription(), "2.5 Speed",
				"SpeedLabel incorrect after one speed item pickup");
	}

	@Test
	void testShieldLabel() {
		player.setSfx(new EntitySFX(player, 1200, 0));

		assertEquals(player.getShieldLabel().getDescription(), "2.0 Defence",
				"DefenceLabel incorrect at the start of the game");

		DefenceItem item = new DefenceItem(new Vector(), 0, gameRenderer);
		player.setPhysDef(player.getDefence() + item.getExtraDefence());

		assertEquals(player.getDefence(), 3.0, "Defence incorrect after one defence item pickup");

		player.updateState();

		assertEquals(player.getShieldLabel().getDescription(), "3.0 Defence",
				"DefenceLabel incorrect after one defence item pickup");
	}

	@Test
	void testBowDmgLabel() {
		player.setSfx(new EntitySFX(player, 1200, 0));

		assertEquals(player.getBowDmgLabel().getDescription(), "40 Damage",
				"BowDmgLabel incorrect at the start of the game");

		BowArrowItem item = new BowArrowItem(new Vector(), 0, gameRenderer);
		player.setExtraArrowDmg(player.getExtraArrowDmg() + item.getExtraDmg());

		assertEquals(40 + player.getExtraArrowDmg(), 50, "BowDmg incorrect after one defence item pickup");

		player.updateState();

		assertEquals(player.getBowDmgLabel().getDescription(), "50 Damage",
				"BowDmgLabel incorrect after one defence item pickup");
	}

	@Test
	void testMagicDmgLabel() {
		player.setSfx(new EntitySFX(player, 1200, 0));

		assertEquals(player.getMagicDmgLabel().getDescription(), "100 Damage",
				"MagicDmgLabel incorrect at the start of the game");
	}

	// animate(), loadImage() and createList() cannot be tested effectively via unit
	// tests

	@Test
	void testMagicPoints() {
		player.setSfx(new EntitySFX(player, 1200, 0));
		player.setProjectile(new Magic());
		RangedCommands.projAttack(player, new Vector());
		player.setMagicPoints(player.getMagicPoints() - 50);
		player.updateState();

		assertEquals(player.getMagicPoints(), 51.0, "Magic points  not deducted correctly");
	}

	@Test
	void testHealthBar() {
		Player player = new Player(new Vector(3, 4), 0, "name", 1);
		player.setHealth(50);
		player.setHealthBar();

		assertEquals(player.getHealthBar().preferSize(), new Vector(50, 20),
				"HealthBar doesn't update preferSize correctly");
	}

	@Test
	void testMagicBar() {
		Player player = new Player(new Vector(3, 4), 0, "name", 1);
		player.setMagicPoints(49);
		player.updateState();

		assertEquals(player.getMagicBar().preferSize(), new Vector(50, 20),
				"HealthBar doesn't update preferSize correctly");
	}

	// getArrowCount(), setArrowCount(), getSwordLabel(), getBowLabel(),
	// getMagicLabel() and getControls()
	// cannot be tested effectively via unit tests

	@Test
	void testMagic() {
		Canvas canvas = new Canvas(1200, 700);
		Renderer gameRenderer = new Renderer(canvas);

		Player player = new Player(new Vector(3, 4), 0, "name", 1);
		Magic magic = new Magic(new Vector(1, 2), new Vector(2, 3), 2.0, 6.0);

		player.setMagic(magic);

		assertSame(player.getMagic(), magic, "The wrong magic object is retrieved");

		player.removeMagic();

		assertNull(player.getMagic(), "Removing magic doesn't set magic to null");
	}

	@Test
	void testArrow() {
		Player player = new Player(new Vector(3, 4), 0, "name", 1);
		ArrowProjectile arrow = new ArrowProjectile();

		player.setArrow(arrow);

		assertSame(player.getArrow(), arrow, "The wrong arrow object is retrieved");

		player.removeArrow();

		assertNull(player.getArrow(), "Removing arrow doesn't set arrow to null");
	}

	// getDirPressed(), addDirPressed() and removeDirPressed() cannot be tested
	// effectively via

	@Test
	void testAlive() {
		Player player = new Player(new Vector(3, 4), 0, "name", 1);

		player.setAlive(true);
		assertTrue(player.isAlive(), "Alive getter/setter incorrect");

		player.setAlive(false);
		assertFalse(player.isAlive(), "Alive getter/setter incorrect");
	}

	@Test
	void testArrows() {
		Player player = new Player(new Vector(3, 4), 0, "name", 1);

		player.setArrows(100);

		assertEquals(player.getArrows(), 100, "Arrows getter/setter incorrect");

		player.addArrows(15);

		assertEquals(player.getArrows(), 115, "Adding arrows incorrect");
	}

	// setEquippedWeapon() and getEquippedWeapon() cannot be tested effectively via
	// unit testing

	// Retriever has no methods to unit test

	// Rock has no methods that would benefit from unit testing

	// Testing RockProjectile
	@Test
	void testRockProjShoot() {
		RockProjectile rock = new RockProjectile();
		Slime slime = new Slime(new Vector(), 0); // Slime cannot use rocks in the game, but is good for testing
		GenericProjectile proj = rock.shoot(new Vector(0, 0), new Vector(1, 1), 45.0, 10.0, slime);

		assertSame(proj.getOwner(), slime, "Slime is not the owner when it should be");
		assertEquals(proj.getPosition(), new Vector(0, 0), "Position of new rock projectile set wrong");
		assertEquals(proj.getVelocity(), new Vector(1, 1), "Velocity of new rock projectile set wrong");
		assertEquals(proj.getAngleDeg(), 45.0, "Angle Degree of new rock projectile set wrong");
	}

	// loadImage() cannot be tested effectively via unit tests

	// Testing SpikyRock
	@Test
	void testSpikeDamage() {
		SpikyRock rock = new SpikyRock(new Vector(), new Vector(), 0);

		rock.setSpikeDamage(100f);

		assertEquals(rock.getSpikeDamage(), 100f, "SpikeDamage getter/setter incorrect");
	}

	// loadImage() cannot be tested effectively via unit tests

	// Testing Sword
	@Test
	void testGetThisSword() {
		Sword sword = new Sword();

		assertSame(sword.getThisSword(), sword, "Sword retrieves wrong sword object");
	}
}
