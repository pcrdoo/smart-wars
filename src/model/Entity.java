package model;

import util.Vector2D;

public class Entity {
	private Vector2D position;
	private Vector2D velocity; // pixels per second
	private 
	
	public void update(double dt) {
		position = position.add(velocity).scale(dt);
	}
	
	public boolean hitTest(Vector2D point) {
		return false;
	}
	

}
