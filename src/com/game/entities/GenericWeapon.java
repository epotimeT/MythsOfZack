package com.game.entities;

/**
 * A Generic weapon class. A sword is a generic weapon.
 */
public abstract class GenericWeapon extends GenericEntity {
	private float attackRate;
	private float damage;
	private float knockback;

	public float getKnockback() {
		return knockback;
	}

	public void setKnockback(float knockback) {
		this.knockback = knockback;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getAttackRate() {
		return attackRate;
	}

	public void setAttackRate(float attackRate) {
		this.attackRate = attackRate;
	}
}