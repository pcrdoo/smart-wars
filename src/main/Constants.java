package main;
import util.Vector2D;

public class Constants {
	// DAVID
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;
	
	public static final double BACKGROUND_ALPHA_PER_SECOND = 0.03f;
	public static final double STARS_ALPHA_PER_SECOND = 0.07f;
	
	public static final int PLAYER_ANIMATION_FPS = 24;
	
	public static final int MAX_BULLET_FIRE_PARTICLES = 20;
	public static final double BULLET_FIRE_PARTICLE_LIFETIME = 0.2;
	public static final double BULLET_FIRE_PARTICLE_SPAWN_INTERVAL = 0.1;
	
	// OGI
	// Player
	public static Vector2D TOP_PLAYER_START_POS = new Vector2D(WINDOW_WIDTH / 2, 30);
	public static Vector2D BOTTOM_PLAYER_START_POS = new Vector2D(WINDOW_WIDTH / 2, WINDOW_HEIGHT - 30);
	public static double PLAYER_SPEED = 200;
	public static double PLAYER_RADIUS = 20;
	public static int PLAYER_HEALTH = 10;
	
	// Bullet
	public static double BULLET_SPEED = 400;
	public static Vector2D BULLET_SIZE = new Vector2D(3, 3);
	public static final double BULLET_COOLDOWN = 0.5;
	
	// Mirror
	public static double MIRROR_SPEED = 0;
	public static double MIRROR_RADIUS = 50;
}
