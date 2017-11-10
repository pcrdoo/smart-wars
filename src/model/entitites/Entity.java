package model.entitites;

import java.awt.Rectangle;

import util.Vector2D;

public abstract class Entity {
	protected Vector2D position;
	protected Vector2D velocity; // pixels per second

	public Entity(Vector2D position) {
		this.position = position;
		this.velocity = new Vector2D(0, 0);
	}

	public Entity(Vector2D position, Vector2D velocity) {
		this.position = position;
		this.velocity = velocity;
	}

	public void update(double dt) {
		position = position.add(velocity.scale(dt));
	}

	public abstract boolean hitTest(Vector2D point);
	
	public abstract Rectangle getBoundingBox();

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}
	
	public abstract boolean shouldCull();
}
