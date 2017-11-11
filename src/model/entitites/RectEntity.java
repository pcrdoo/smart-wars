package model.entitites;

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
		return point.getX() > boundingBox.getMinX() && point.getX() < boundingBox.getMaxX()
				&& point.getY() > boundingBox.getMinY() && point.getY() < boundingBox.getMaxY();
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) (position.getX() - size.getX() / 2), (int) (position.getY() - size.getY() / 2),
				(int) size.getX(), (int) size.getY());
	}

}
