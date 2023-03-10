package com.game.entities;

import java.util.ArrayList;
import java.util.List;

import com.game.sound.EntitySFX;
import com.game.utilities.Vector;

/***
 *
 * An abstract class that contains the attributes, getters and setters that all
 * complex entities have.
 * 
 * List of Complex entities for reference:
 * <ul>
 * <li>Players</li>
 * <li>Monsters</li>
 * </ul>
 * <br>
 */

public abstract class ComplexEntity extends GenericEntity {
	private float health;
	private float physDmg;
	private float magDmg;
	private float defence;
	private double speed;
	private float maxSpeed;

	private float atkCooldown = 0.0f;
	private double cooldownTime;
	private boolean meleeAttack = false;
	private boolean arrowAttack = false;
	private boolean magAttack = false;
	private GenericProjectile projectile;
	private List<GenericProjectile> projectileList = new ArrayList<>();

	private int extraSwordDmg = 0;
	private int extraArrowDmg = 0;
	private int extraMagicDmg = 0;
	private Sword sword;
	private boolean swordSwung = false;
	private int invulnerableTime = 0;
	private EntitySFX sfx;

	/***
	 * Sets the stats of the entity to the ones defined in the list, in order; <br>
	 * [health, physDmg, magDmg,physDef,speed,maxSpeed]
	 * 
	 * @param stats
	 */
	public void setStats(List<Float> stats) {
		if (stats.size() >= 7) {
			health = stats.get(0);
			physDmg = stats.get(1);
			magDmg = stats.get(2);
			defence = stats.get(3);
			// magDef = stats.get(4);
			speed = stats.get(5);
			maxSpeed = stats.get(6);
		}
	}

	public EntitySFX getSfx() {
		return sfx;
	}

	public void setSfx(EntitySFX sfx) {
		this.sfx = sfx;
	}

	public boolean isSwordSwung() {
		return swordSwung;
	}

	public void setSwordSwung(boolean swordSwung) {
		this.swordSwung = swordSwung;
	}

	public int getInvulnerableTime() {
		return invulnerableTime;
	}

	public void setInvulnerableTime(int invulnerableTime) {
		this.invulnerableTime = invulnerableTime;
	}

	public Sword getSword() {
		return sword;
	}

	public void setSword(Sword sword) {
		this.sword = sword;
	}

	public int getExtraSwordDmg() {
		return extraSwordDmg;
	}

	public void setExtraSwordDmg(int extraSwordDmg) {
		this.extraSwordDmg = extraSwordDmg;
	}

	public int getExtraArrowDmg() {
		return extraArrowDmg;
	}

	public void setExtraArrowDmg(int extraArrowDmg) {
		this.extraArrowDmg = extraArrowDmg;
	}

	public int getExtraMagicDmg() {
		return extraMagicDmg;
	}

	public void setExtraMagicDmg(int extraMagicDmg) {
		this.extraMagicDmg = extraMagicDmg;
	}

	public float getPhysDmg() {
		return physDmg;
	}

	public void setPhysDmg(float physicalDmg) {
		this.physDmg = physicalDmg;
	}

	public float getMagDmg() {
		return magDmg;
	}

	public void setMagDmg(float magDmg) {
		this.magDmg = magDmg;
	}

	public float getDefence() {
		return defence;
	}

	public void setPhysDef(float defence) {
		this.defence = defence;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public float getHealth() {
		return health;
	}

	@Override
	public Vector getVelocity() {
		return this.getEntitySprite().velocity();
	}

	@Override
	public void setVelocity(Vector velocity) {
		this.getEntitySprite().setVelocity(velocity);
	}

	public double getAtkCooldown() {
		return atkCooldown;
	}

	public void setAtkCooldown(float atkCooldown) {
		this.atkCooldown = atkCooldown;
	}

	public double getCooldownTime() {
		return cooldownTime;
	}

	public void setCooldownTime(double getCooldownTime) {
		this.cooldownTime = getCooldownTime;
	}

	protected void setMeleeAttack(boolean meleeAttack) {
		this.meleeAttack = meleeAttack;
	}

	protected void setArrowAttack(boolean arrowAttack) {
		this.arrowAttack = arrowAttack;
	}

	protected void setMagAttack(boolean magAttack) {
		this.magAttack = magAttack;
	}

	public boolean isMeleeAttack() {
		return meleeAttack;
	}

	public boolean isArrowAttack() {
		return arrowAttack;
	}

	public boolean isMagAttack() {
		return magAttack;
	}

	public GenericProjectile getProjectile() {
		return projectile;
	}

	public void setProjectile(GenericProjectile projectile) {
		this.projectile = projectile;
	}

	public abstract void projAttack();

	public List<GenericProjectile> getProjectileList() {
		return projectileList;
	}

	public void addProjectile(GenericProjectile projectile) {
		if (!projectileList.contains(projectile)) {
			projectileList.add(projectile);
		}
	}

	public void removeProjectile(GenericProjectile projectile) {
		if (projectileList.contains(projectile)) {
			projectileList.remove(projectile);
		}
	}

}
