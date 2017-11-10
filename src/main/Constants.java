package main;

import util.Vector2D;

public class Constants {
	public static final int WINDOW_WIDTH = 1280;
	public static final int WINDOW_HEIGHT = 720;

	public static final double BACKGROUND_ALPHA_PER_SECOND = 0.05f;
	public static final double STARS_ALPHA_PER_SECOND = 0.12f;

	public static final int PLAYER_ANIMATION_FPS = 24;

	// Player
	public static final Vector2D LEFT_PLAYER_START_POS = new Vector2D(60, WINDOW_HEIGHT / 2);
	public static final Vector2D RIGHT_PLAYER_START_POS = new Vector2D(WINDOW_WIDTH - 60, WINDOW_HEIGHT / 2);
	public static final double PLAYER_SPEED = 300;
	public static final double PLAYER_RADIUS = 20;
	public static final int PLAYER_HEALTH = 100;

	// Bullet
	public static final double BULLET_SPEED = 500;
	public static final Vector2D BULLET_SIZE = new Vector2D(3, 3);
	public static final double BULLET_COOLDOWN = 0.1;
	public static final double BULLET_DAMAGE = 10;
	
	// Asteroid
	public static final double ASTEROID_SPAWN_PROBABILITY = 1; // per second
	public static final int ASTEROID_TYPE_COUNT = 6;
	public static final int ASTEROID_SPRITES_X = 4;
	public static final int ASTEROID_SPRITES_Y = 4;
	public static final int ASTEROID_SPAWN_X_MIN = (int) (LEFT_PLAYER_START_POS.getdX() + 80);
	public static final int ASTEROID_SPAWN_X_MAX = (int) (RIGHT_PLAYER_START_POS.getdX() - 80);
	
	public static final int ASTEROID_SPAWN_Y = -50;
	
	public static final int ASTEROID_Y_VELOCITY = 100;
	public static final int ASTEROID_Y_VELOCITY_JITTER = 20;
	public static final int ASTEROID_X_VELOCITY = 20;
	public static final int ASTEROID_X_VELOCITY_JITTER = 7;
	
	public static final int ASTEROID_FPS = 5;
}

