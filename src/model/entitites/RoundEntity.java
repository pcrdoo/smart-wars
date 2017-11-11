package model.entitites;

import java.awt.Rectangle;

import util.Vector2D;

public abstract class RoundEntity extends Entity {
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
		return new Rectangle((int) (position.getX() - radius), (int) (position.getY() - radius), (int) (2 * radius),
				(int) (2 * radius));
	}
}
