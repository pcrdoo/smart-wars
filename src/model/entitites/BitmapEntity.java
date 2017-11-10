package model.entitites;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import util.ImageCache;
import util.Vector2D;

public abstract class BitmapEntity extends Entity {

	protected BufferedImage collisionMask; // non-transparent pixels can be hit

	public BitmapEntity(Vector2D position, Vector2D velocity) {
		super(position, velocity);
	}

	public void setImage(BufferedImage image) {
		this.collisionMask = image;
	}

	public boolean hitTest(Vector2D point) {
		if (collisionMask == null) {
			return false;
		}
		Rectangle boundingBox = getBoundingBox();
		Vector2D pointInImage = point.sub(new Vector2D(boundingBox.getMinX(), boundingBox.getMinY()));
		if (point.getdX() > boundingBox.getMinX() && point.getdX() < boundingBox.getMaxX()
				&& point.getdY() > boundingBox.getMinY() && point.getdY() < boundingBox.getMaxY()
				&& (collisionMask.getRGB((int) pointInImage.getdX(), (int) pointInImage.getdY()) >> 24) != 0x00) {
			return true;
		}
		return false;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) (position.getdX() - collisionMask.getWidth() / 2),
				(int) (position.getdY() - collisionMask.getHeight() / 2), collisionMask.getWidth(),
				collisionMask.getHeight());
	}

}
