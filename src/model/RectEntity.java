package model;

import java.awt.Rectangle;

import util.Vector2D;

public abstract class RectEntity extends Entity {

	protected Vector2D size;

	public RectEntity(Vector2D position, Vector2D velocity, Vector2D size) {
		super(position, velocity);
		this.size = size;
	}

	public boolean hitTest(Vector2D point) {
		Rectangle boundingBox = getBoundingBox();
		return point.getdX() > boundingBox.getMinX() && point.getdX() < boundingBox.getMaxX()
				&& point.getdY() > boundingBox.getMinY() && point.getdY() < boundingBox.getMaxY();
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) (position.getdX() - size.getdX() / 2), (int) (position.getdY() - size.getdY() / 2),
				(int) (position.getdX() + size.getdX() / 2), (int) (position.getdY() + size.getdY() / 2));
	}

}
