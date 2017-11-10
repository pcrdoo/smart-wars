package model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import util.ImageCache;
import util.Vector2D;

public class BitmapEntity extends Entity {

	private BufferedImage image; // non-transparent pixels can be hit

	public BitmapEntity(Vector2D position, BufferedImage image) {
		super(position);
		this.image = image;
	}

	public boolean hitTest(Vector2D point) {
		Rectangle boundingBox = getBoundingBox();
		Vector2D pointInImage = point.sub(new Vector2D(boundingBox.getMinX(), boundingBox.getMinY()));
		if (point.getdX() > boundingBox.getMinX() && point.getdX() < boundingBox.getMaxX()
				&& point.getdY() > boundingBox.getMinY() && point.getdY() < boundingBox.getMaxY()
				&& (image.getRGB((int) pointInImage.getdX(), (int) pointInImage.getdY()) >> 24) != 0x00) {
			return true;
		}
		return false;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) (position.getdX() - image.getWidth() / 2),
				(int) (position.getdY() - image.getHeight() / 2), (int) (position.getdX() + image.getWidth() / 2),
				(int) (position.getdY() + image.getHeight() / 2));
	}

}
