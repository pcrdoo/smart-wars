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
	private boolean disintegrated = false;
	
	@Override
	public boolean shouldCull() {
		Rectangle boundingBox = getBoundingBox();
		return disintegrated || boundingBox.getMinY() > Constants.WINDOW_HEIGHT;
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
	
	public Asteroid(Vector2D position, Vector2D velocity, int type) {
		super(position, velocity);

		this.type = type;
		
		frameTime = 1.0 / Constants.ASTEROID_FPS;
		disintegrated = false;
		
		if (collisionMasks == null) {
			BufferedImage spritesheet = ImageCache.getInstance().get("assets/asteroid-" + Integer.toString(type) + ".png");
			collisionMasks = createCollisionMasks(spritesheet);
		}
		
		collisionMask = collisionMasks[0];
		frame = 0;
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		time += dt;
		while (time > frameTime) {
			time -= frameTime;
			frame = (frame + 1) % (FRAMES_PER_X + FRAMES_PER_Y);
		}
		frame=0;
		collisionMask = collisionMasks[frame];
	}
	
	public void disintegrate() {
		disintegrated = true;
	}
	
	public boolean isDisintegrated() {
		return disintegrated;
	}
	
	public int getFrame() {
		return frame;
	}
	
	public int getType() {
		return type;
	}
}
