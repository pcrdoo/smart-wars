package model;

import java.awt.Rectangle;

import util.Vector2D;

public class RoundEntity extends Entity {
	protected double radius;

	protected RoundEntity(Vector2D position, double radius) {
		super(position);
		this.radius = radius;
	}

	public boolean hitTest(Vector2D point) {
		return position.sub(point).length() < radius;
	}
	
	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) (position.getdX() - radius), (int) (position.getdY() - radius),
				(int) (position.getdX() + radius), (int) (position.getdY() + radius));
	}
}
