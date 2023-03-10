package com.game.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.game.collision.Hitbox;
import com.game.graphics.Renderer;
import com.game.graphics.Sprite;
import com.game.utilities.Vector;

public abstract class GenericEntity {

	// protected static List<List<GenericEntity>> entities = new
	// ArrayList<>(Collections.nCopies(9, new ArrayList<>()));
	// protected static List<List<GenericEntity>> entities = new
	// ArrayList<List<GenericEntity>>();
	protected static List<List<GenericEntity>> entities = Arrays.asList(new ArrayList<GenericEntity>(),
			new ArrayList<GenericEntity>(), new ArrayList<GenericEntity>(), new ArrayList<GenericEntity>(),
			new ArrayList<GenericEntity>(), new ArrayList<GenericEntity>(), new ArrayList<GenericEntity>(),
			new ArrayList<GenericEntity>(), new ArrayList<GenericEntity>());

	/*
	 * A simple version of an entity. Contains attributes that all entities have.
	 * 
	 */
	Sprite entitySprite;
	Hitbox hitbox;
	public Type entityType;
	protected int screenId;

	public enum Type {
		PLAYER, MONSTER, ITEM, WEAPON, PROJECTILE, OTHER
	}

	public Vector getPosition() {
		return entitySprite.position();
	}

	public void setPosition(Vector position) {
		entitySprite.setPosition(position);
	}

	public int getScreenId() {
		return screenId;
	}

	public void setScreenId(int newId) {
		this.screenId = newId;
	}

	public Sprite getEntitySprite() {
		return entitySprite;
	}

	public void setEntitySprite(Sprite entitySprite) {
		this.entitySprite = entitySprite;
	}

	public Hitbox getHitbox() {
		return hitbox;
	}

	public void setHitbox(Hitbox hitbox) {
		this.getEntitySprite().setHitbox(hitbox);
		this.hitbox = hitbox;
	}

	public List<List<GenericEntity>> getEntities() {
		return entities;
	}

	public void removeEntity(GenericEntity e) {
		if (entities.get(e.getScreenId()).contains(e)) {
			entities.get(e.getScreenId()).remove(e);
		} else {
			// throw new java.lang.RuntimeException("Tried to remove entity that was not in
			// list");
		}
	}

	protected void addEntity(int screenId, GenericEntity e) {
		if (!entities.get(screenId).contains(e)) {
			entities.get(screenId).add(e);
		} else {
			// throw new java.lang.RuntimeException("Tried to add entity that was already in
			// list.");
		}
	}

	public Type getEntityType() {
		return entityType;
	}

	public void setEntityType(Type entityType) {
		this.entityType = entityType;
	}

	public Vector getVelocity() {
		return this.getEntitySprite().velocity();
	}

	public void setVelocity(Vector velocity) {
		this.getEntitySprite().setVelocity(velocity);
	}

	// Kirsty's functions
	public int moveEntity(GenericEntity e, int prevRoom) {
		if (entities.get(prevRoom).contains(e)) {
			entities.get(prevRoom).remove(e);

			addEntity(e.getScreenId(), e);
			return entities.get(e.getScreenId()).indexOf(e);
		}
		return -1;
	}

	public void addToRenderer(Renderer renderer) {
		renderer.addSprite(this.getEntitySprite());
	}

	public void removeFromRenderer(Renderer renderer) {
		renderer.removeSprite(this.getEntitySprite());
	}

	public void pubAddEntity() {
		addEntity(this.screenId, this);
	}

}
