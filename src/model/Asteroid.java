package model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.Constants;
import memory.Poolable;
import model.entitites.BitmapEntity;
import util.ImageCache;
import util.Vector2D;

public class Asteroid extends BitmapEntity implements Poolable {
	private final static int FRAMES_PER_X = 4;
	private final static int FRAMES_PER_Y = 4;
	
	private static BufferedImage[][] collisionMasks;
	
	private int type, frame;
	
	@Override
	public boolean shouldCull() {
		Rectangle boundingBox = getBoundingBox();
		return boundingBox.getMinY() > Constants.WINDOW_HEIGHT;
	}

	private static void createCollisionMasks(BufferedImage spritesheet, int type) {
		for (int y = 0; y < FRAMES_PER_Y; y++) {
			for (int x = 0; x < FRAMES_PER_X; x++) {
				collisionMasks[type][y * FRAMES_PER_X + x] = spritesheet.getSubimage(x, y, spritesheet.getWidth() / FRAMES_PER_X, spritesheet.getHeight() / FRAMES_PER_Y);
			}
		}
	}
	
	public Asteroid() {
		super(null, null);

		if (collisionMasks == null) {
			collisionMasks = new BufferedImage[Constants.ASTEROID_TYPE_COUNT][FRAMES_PER_X * FRAMES_PER_Y];
			for (int i = 0; i < Constants.ASTEROID_TYPE_COUNT; i++) {
				BufferedImage spritesheet = ImageCache.getInstance().get("asteroid-" + Integer.toString(i + 1) + ".png");
				createCollisionMasks(spritesheet, i);
			}
		}
	}
	
	public void init(Vector2D position, Vector2D velocity, int type, int frame) {
		this.position = position;
		this.velocity = velocity;
		this.type = type;
		this.frame = frame;
		collisionMask = collisionMasks[type][frame];
	}
	
	public int getFrame() {
		return frame;
	}
	
	public int getType() {
		return type;
	}

	public void getPushed(Vector2D velocity) {
		this.velocity = this.velocity.add(velocity.normalize().scale(Constants.ASTEROID_PUSH_VELOCITY_INTENSITY));
		this.position = this.position.add(velocity.normalize().scale(Constants.ASTEROID_PUSH_POSITION_INTENSITY));
	}

	@Override
	public void reset() {
		this.velocity = null;
		this.position = null;
	}
}
