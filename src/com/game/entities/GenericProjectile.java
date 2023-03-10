package com.game.entities;

import com.game.utilities.Vector;

/**
 * A Generic projectile class.
 * 
 * @author Harry B, John Z
 *
 */
public abstract class GenericProjectile extends GenericEntity {
	private float fireRate;
	private float damage;
	private double maxSpeed;
	private double speed;
	private float rotVel;
	private double angleDeg;
	private Vector size;
	private ComplexEntity owner;
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

	public float getFireRate() {
		return fireRate;
	}

	public void setFireRate(float fireRate) {
		this.fireRate = fireRate;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public float getRotVel() {
		return rotVel;
	}

	public void setRotVel(float rotVel) {
		this.rotVel = rotVel;
	}

	public Vector getSize() {
		return size;
	}

	public void setSize(Vector size) {
		this.size = size;
	}

	public double getAngleDeg() {
		return angleDeg;
	}

	public void setAngleDeg(double angleDeg) {
		this.angleDeg = angleDeg;
	}

	public ComplexEntity getOwner() {
		return owner;
	}

	public void setOwner(ComplexEntity owner) {
		this.owner = owner;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Removes this projectile from the owner's projectile list.
	 */
	public void removeFromOwner() {
		this.getOwner().removeProjectile(this);
	}

	/**
	 * A method to shoot this projectile. Abstract method.
	 * 
	 * @param pos
	 * @param vel
	 * @param angleDeg
	 * @param rotVel
	 * @param e
	 */
	public abstract GenericProjectile shoot(Vector pos, Vector vel, Double angleDeg, Double rotVel, ComplexEntity e);
}
