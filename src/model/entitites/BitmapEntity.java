package model.entitites;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import util.ImageCache;
import util.Vector2D;

public abstract class BitmapEntity extends Entity {

	protected BufferedImage collisionMask; // non-transparent pixels can be hit

	public BitmapEntity(Vector2D position, Vector2D velocity) {
		super(position, velocity);
	}

	public boolean hitTest(Vector2D point) {
		if (collisionMask == null) {
			return false;
		}
		Rectangle boundingBox = getBoundingBox();

		Vector2D pointInImage = point.sub(new Vector2D(boundingBox.getMinX(), boundingBox.getMinY()));
		Point p = new Point((int)point.getX(), (int)point.getY());
		
		if (boundingBox.contains(p) && (collisionMask.getRGB((int)pointInImage.getX(), (int)pointInImage.getY()) >> 24) != 0x00) {
			return true;
		}
		return false;
	}
	
	public boolean hitTest(RoundEntity round) {
		if (!round.getBoundingBox().intersects(getBoundingBox())) {
			return false;
		}
		
		Rectangle intersection = new Rectangle();
		Rectangle.intersect(getBoundingBox(), round.getBoundingBox(), intersection);
		
		for (int y = (int)intersection.getMinY(); y <= intersection.getMaxY(); y++) {
			for (int x = (int)intersection.getMinX(); x <= intersection.getMaxX(); x++) {
				if (round.hitTest(new Vector2D(x, y)) && hitTest(new Vector2D(x, y))) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) (position.getX() - collisionMask.getWidth() / 2),
				(int) (position.getY() - collisionMask.getHeight() / 2), collisionMask.getWidth(),
				collisionMask.getHeight());
	}

}
