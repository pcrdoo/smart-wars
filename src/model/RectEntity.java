package model;

import util.Vector2D;

public class RectEntity extends Entity {

	protected Vector2D size;

	public RectEntity(Vector2D position, Vector2D velocity) {
		super(position, velocity);
	}

	public boolean hitTest(Vector2D point) {
		return point.getdX() > position.getdX() && point.getdX() < position.getdX() + size.getdX()
				&& point.getdY() > position.getdY() && point.getdY() < position.getdY() + size.getdY();
	}

}
