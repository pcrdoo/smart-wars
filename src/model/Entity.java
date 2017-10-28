package model;

import util.Vector2D;

public class Entity {
	protected Vector2D position;
	protected Vector2D velocity; // pixels per second

	public Entity(Vector2D position) {
		this.position = position;
	}

	public Entity(Vector2D position, Vector2D velocity) {
		this.position = position;
		this.velocity = velocity;
	}
	
	public void update(double dt) {
		position = position.add(velocity).scale(dt);
	}
	
	public boolean hitTest(Vector2D point) {
		return false;
	}
	
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
}
