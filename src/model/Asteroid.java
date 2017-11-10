package model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.Constants;
import util.ImageCache;
import util.Vector2D;

public class Asteroid extends BitmapEntity {

	public Asteroid(Vector2D position, Vector2D velocity, int type) {
		super(position, velocity);
		BufferedImage spriteSheet = ImageCache.getInstance().get("assets/asteroid-" + Integer.toString(type) + ".png");
		collisionMask = spriteSheet.getSubimage(0, 0, spriteSheet.getWidth() / Constants.ASTEROID_SPRITES_X,
				spriteSheet.getHeight() / Constants.ASTEROID_SPRITES_Y);
		// TODO: frame number in Asteroid class
	}

	@Override
	public boolean shouldCull() {
		Rectangle boundingBox = getBoundingBox();
		return boundingBox.getMinY() > Constants.WINDOW_HEIGHT;
	}

}
