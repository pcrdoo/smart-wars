package model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.Constants;
import model.entitites.BitmapEntity;
import util.ImageCache;
import util.Vector2D;

public class Asteroid extends BitmapEntity {
	private final static int FRAMES_PER_X = 4;
	private final static int FRAMES_PER_Y = 4;
	
	private static BufferedImage[] collisionMasks;
	
	private int type, frame;
	private double time, frameTime;
	
	@Override
	public boolean shouldCull() {
		Rectangle boundingBox = getBoundingBox();
		return boundingBox.getMinY() > Constants.WINDOW_HEIGHT;
	}

	private static BufferedImage[] createCollisionMasks(BufferedImage spritesheet) {
		BufferedImage[] collisionMasks = new BufferedImage[FRAMES_PER_X * FRAMES_PER_Y];
		for (int y = 0; y < FRAMES_PER_Y; y++) {
			for (int x = 0; x < FRAMES_PER_X; x++) {
				collisionMasks[y * FRAMES_PER_X + x] = spritesheet.getSubimage(x, y, spritesheet.getWidth() / FRAMES_PER_X, spritesheet.getHeight() / FRAMES_PER_Y);
			}
		}
		
		return collisionMasks;
	}
	
	public Asteroid(Vector2D position, Vector2D velocity, int type, int frame) {
		super(position, velocity);

		this.type = type;
		
		frameTime = 1.0 / Constants.ASTEROID_FPS;
		if (collisionMasks == null) {
			BufferedImage spritesheet = ImageCache.getInstance().get("assets/asteroid-" + Integer.toString(type) + ".png");
			collisionMasks = createCollisionMasks(spritesheet);
		}
		
		this.frame = frame;
		collisionMask = collisionMasks[frame];
	}
	
	public int getFrame() {
		return frame;
	}
	
	public int getType() {
		return type;
	}

	public void stealMomentum(Vector2D velocity) {
		this.velocity = this.velocity.add(velocity.normalize().scale(Constants.ASTEROID_STOLEN_VELOCITY_INTENSITY));
	}
}
