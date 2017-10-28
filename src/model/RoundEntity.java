package model;

import util.Vector2D;

public class RoundEntity extends Entity {
	protected double radius;
	
	public boolean hitTest(Vector2D point) {
		return this.position.sub(point).length() < this.radius;
	}
}
