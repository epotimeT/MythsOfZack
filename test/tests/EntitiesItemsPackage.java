package com.game.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;

import com.game.entities.items.ArrowItem;
import com.game.entities.items.BowArrowItem;
import com.game.entities.items.BundleItem;
import com.game.entities.items.DefenceItem;
import com.game.entities.items.RegenItem;
import com.game.entities.items.RestoreHealthItem;
import com.game.entities.items.RestoreMPItem;
import com.game.entities.items.SpeedItem;
import com.game.entities.items.SwordItem;
import com.game.utilities.Vector;

/**
 * <h1>EntitiesItemsPackage</h1> Unit tests for the classes contained in the
 * package 'Entities.Items' Some of these tests require javafx to run
 */
class EntitiesItemsPackage {
	@Rule
	private Timeout entitiesItemsTimeout = new Timeout(200, TimeUnit.MILLISECONDS);

	@Rule
	private double precision = CollisionPackage.precision;

	// Testing ArrowItem
	@Test
	void testQuantity() {
		ArrowItem arrowItem = new ArrowItem(new Vector(), 0, null);

		arrowItem.setQuantity(100);

		assertEquals(arrowItem.getQuantity(), 100, "Quantity getter/setter incorrect");
	}

	// Testing BowArrowItem
	@Test
	void testBowExtraDmg() {
		BowArrowItem bowArrowItem = new BowArrowItem(new Vector(), 0, null);

		bowArrowItem.setExtraDmg(100);

		assertEquals(bowArrowItem.getExtraDmg(), 100, "ExtraDmg getter/setter incorrect");
	}

	// Testing BundleItem
	@Test
	void testBuffList() {
		BundleItem bundleItem = new BundleItem(new Vector(), new float[] { 3.1f }, 0);
		float[] expList = new float[] { 10.2f, 14.42f, 768675f };

		bundleItem.setBuffs(expList);

		assertEquals(bundleItem.getBuffs(), expList, "BuffList getter/setter incorrect");
	}

	// Testing DefenceItem
	@Test
	void testExtraDefence() {
		DefenceItem defenceItem = new DefenceItem(new Vector(), 0, null);

		defenceItem.setExtraDefence(100);

		assertEquals(defenceItem.getExtraDefence(), 100, "ExtraDefence getter/setter incorrect");

	}

	// Testing RegenItem
	@Test
	void testRegenAmount() {
		RegenItem regenItem = new RegenItem(new Vector(), 0, null);

		regenItem.setRegenAmount(100);

		assertEquals(regenItem.getRegenAmount(), 100, "RegenAmount getter/setter incorrect");
	}

	// Testing RestoreHealthItem
	@Test
	void testExtraHealth() {
		RestoreHealthItem restoreHealthItem = new RestoreHealthItem(new Vector(), 0, null);

		restoreHealthItem.setExtraHealth(100);

		assertEquals(restoreHealthItem.getExtraHealth(), 100, "ExtraHealth getter/setter incorrect");
	}

	// Testing RestoreMPItem
	@Test
	void testMagicPointsRestored() {
		RestoreMPItem restoreMpItem = new RestoreMPItem(new Vector(), 0, null);

		restoreMpItem.setMagicPointsRestored(100);

		assertEquals(restoreMpItem.getMagicPointsRestored(), 100, "MagicPointsRestored getter/setter incorrect");
	}

	// Testing SpeedItem
	@Test
	void testExtraSpeed() {
		SpeedItem speedItem = new SpeedItem(new Vector(), 0, null);

		speedItem.setExtraSpeed(100);

		assertEquals(speedItem.getExtraSpeed(), 100, "ExtraSpeed getter/setter incorrect");
	}

	// Testing SwordItem
	@Test
	void testSwordExtraDmg() {
		SwordItem swordItem = new SwordItem(new Vector(), 0, null);

		swordItem.setExtraDmg(100);

		assertEquals(swordItem.getExtraDmg(), 100, "ExtraDmg getter/setter incorrect");
	}
}