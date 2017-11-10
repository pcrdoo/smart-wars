package main;

import util.Vector2D;

public class Constants {
	public static final int WINDOW_WIDTH = 1280;
	public static final int WINDOW_HEIGHT = 720;

	public static final double BACKGROUND_ALPHA_PER_SECOND = 0.03f;
	public static final double STARS_ALPHA_PER_SECOND = 0.07f;

	public static final int PLAYER_ANIMATION_FPS = 24;

	public static final int BULLET_CULLING_X = 100;
	public static final int BULLET_CULLING_Y = 20;

	// Player
	public static final Vector2D LEFT_PLAYER_START_POS = new Vector2D(60, WINDOW_HEIGHT / 2);
	public static final Vector2D RIGHT_PLAYER_START_POS = new Vector2D(WINDOW_WIDTH - 60, WINDOW_HEIGHT / 2);
	public static final double PLAYER_SPEED = 300;
	public static final double PLAYER_RADIUS = 20;
	public static final int PLAYER_HEALTH = 100;

	// Bullet
	public static final double BULLET_SPEED = 500;
	public static final Vector2D BULLET_SIZE = new Vector2D(3, 3);
	public static final double BULLET_COOLDOWN = 0.2;
	public static final double BULLET_DAMAGE = 10;

	// Mirror
	public static final double MIRROR_SPEED = 0;
	public static final double MIRROR_RADIUS = 50;
}
