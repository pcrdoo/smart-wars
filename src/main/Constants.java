package main;

import util.Vector2D;

public class Constants {
	public static final int WINDOW_WIDTH = 1280;
	public static final int WINDOW_HEIGHT = 720;

	public static final double BACKGROUND_ALPHA_PER_SECOND = 0.05f;
	public static final double STARS_ALPHA_PER_SECOND = 0.12f;

	public static final int PLAYER_ANIMATION_FPS = 24;

	// Z-index
	public static final int Z_BACKDROP = 0;
	public static final int Z_BLACK_HOLE = 1;
	public static final int Z_BULLETS = 2;
	public static final int Z_ASTEROID = 3;
	public static final int Z_PLAYER = 4;
	public static final int Z_OVERLAY = 5;
	public static final int Z_MIRROR = 6;

	// Player
	public static final Vector2D LEFT_PLAYER_START_POS = new Vector2D(60, WINDOW_HEIGHT / 2);
	public static final Vector2D RIGHT_PLAYER_START_POS = new Vector2D(WINDOW_WIDTH - 60, WINDOW_HEIGHT / 2);
	public static final double PLAYER_SPEED = 300;
	public static final double PLAYER_RADIUS = 20;
	public static final int PLAYER_HEALTH = 100;

	// Bullet
	public static final double BULLET_SPEED = 500;
	public static final Vector2D BULLET_SIZE = new Vector2D(8, 8);
	public static final double BULLET_COOLDOWN = 0.1;
	public static final double BULLET_DAMAGE = 10;
	public static final int MAX_BULLET_BOUNCES = 10;
	public static final double BULLET_VELOCITY_CHANGE_COOLDOWN = 10;

	// Asteroid
	public static final double ASTEROID_SPAWN_PROBABILITY = 0.9; // per second
	public static final int ASTEROID_TYPE_COUNT = 6;
	public static final int ASTEROID_SPRITES_X = 4;
	public static final int ASTEROID_SPRITES_Y = 4;
	public static final int ASTEROID_SPAWN_X_MIN = (int) (LEFT_PLAYER_START_POS.getX() + 80);
	public static final int ASTEROID_SPAWN_X_MAX = (int) (RIGHT_PLAYER_START_POS.getX() - 80);
	public static final int ASTEROID_SPAWN_Y = -50;
	public static final double ASTEROID_DISINTEGRATION_TIME = 0.4;
	
	public static final int ASTEROID_PLAYER_DAMAGE = 20;
	
	public static final int ASTEROID_Y_VELOCITY = 100;
	public static final int ASTEROID_Y_VELOCITY_JITTER = 20;
	public static final int ASTEROID_X_VELOCITY = 15;
	public static final int ASTEROID_X_VELOCITY_JITTER = 5;
	public static final int ASTEROID_FPS = 5;
	public static final double ASTEROID_PUSH_VELOCITY_INTENSITY = 2;
	public static final double ASTEROID_PUSH_POSITION_INTENSITY = 2;
	
	// Mirror
	public static final double MIRROR_MAGIC_COOLDOWN = 5;
	public static final double MIRROR_VELOCITY = 200;
	public static final double SHORT_MIRROR_LENGTH = 70;
	public static final double LONG_MIRROR_LENGTH = 100;
	public static final double MIRROR_DIST_EPS = 15;
	public static final int MIRROR_X_MIN = (int) (LEFT_PLAYER_START_POS.getX() + 20);
	public static final int MIRROR_X_MAX = (int) (RIGHT_PLAYER_START_POS.getX() - 20);
	public static final int MIRROR_X_LAUNCH_LEFT = MIRROR_X_MIN + 10;
	public static final int MIRROR_X_LAUNCH_RIGHT = MIRROR_X_MAX - 10;
	public static final double MIRROR_ANGULAR_VELOCITY = Math.PI / 2;
	
	// Black hole
	public static final double WORMHOLE_SPAWN_PROBABILITY = 0.1;
	public static final double WORMHOLE_RADIUS = 65;
	public static final double WORMHOLE_BIRTH_TIME = 0.7;
	public static final double WORMHOLE_ANGULAR_VELOCITY = Math.PI / 2;
	public static final double WORMHOLE_LIFETIME = 30;
	public static final double WORMHOLE_DEATH_START_TIME = 29;
	public static final double WORMHOLE_BULLET_FADE_DISTANCE = 150;
	
	// Object pooling
	public static final int BULLET_POOL_SIZE = 50;
	public static final int ASTEROID_POOL_SIZE = 30;
	public static final int EXPLOSION_POOL_SIZE = 10;
	public static final int SPARKS_POOL_SIZE = 50;
	public static final int GLASS_SPARKS_POOL_SIZE = 50;
	
	// Images
	public static final String IMAGE_FILENAME_PREFIX = "assets/textures/";
	public static final String[] IMAGES_TO_PRELOAD = new String[] {
		"asteroid-1.png",
		"asteroid-2.png",
		"asteroid-3.png",
		"asteroid-4.png",
		"asteroid-5.png",
		"asteroid-6.png",
		"background.png",
		"bullet.png",
		"debris-particle.png",
		"explosion-flare.png",
		"fire.png",
		"healthbar.png",
		"mirror-left-long.png",
		"mirror-left-short.png",
		"mirror-right-long.png",
		"mirror-right-short.png",
		"player1.png",
		"player2.png",
		"player-flare.png",
		"player-left-trail.png",
		"player-right-trail.png",
		"stars.png",
		"wormhole.png",
		"wormhole-glow.png",
		"wormhole-particle.png",
		"wormhole-shadow.png"
	};
	
	// Debug
	public static final String[] PERF_MEASUREMENTS_NAMES = new String[] {
			"DrawTotal",                // Total time spent in MainView.draw
			"ControllerUpdateTotal",    // Total time spent in MainController.update
			"ModelUpdateTotal",         // Total time spent in Model.update
			"ViewUpdateTotal",          // Total time spent in MainView.update
			
			"CompPlayerTrail",          // Time spent in compositing ONE player trail particle system
			"CompExplosion",            // Time spent in compositing ONE explosion particle system
			"CompSwirl",                // Time spent in compositing ONE wormhole swirl particle system
			"CompSparks",               // Time spent in compositing ONE sparks particle system
			"CompGlassSparks",          // Time spent in compositing ONE glass sparks particle system
			
			"CullEntity",               // Time spent culling entities
			"CollisionAsteroidPlayer",  // Time spent testing for asteroid-player collisions
			"CollisionBullet"           // Time spent testing for bullet-player, bullet-mirror and bullet-wormhole collisions
	};
	
	public static final String[] PERF_STATISTICS_NAMES = new String[] {
			"CountEntities",            // Number of entities
			"CountBullets",             // Number of bullets
			"CountAsteroids",           // Number of asteroids
			"CountWormholes",    		// Number of wormholes
			"CountMirrors",             // Number of mirrors
			
			"CountDrawables",           // Number of drawables
			"CountUpdatables",          // Number of view updatables
			
			"PoolBulletFree",           // Number of remaining bullets in the pool
			"PoolBulletViewFree",       // Number of remaining bullet views in the pool
			"PoolAsteroidFree",         // Number of remaining asteroids in the pool
			"PoolAsteroidViewFree",     // Number of remaining asteroid views in the pool
			"PoolExplosionFree",        // Number of remaining explosions in the pool
			"PoolSparksFree",           // Number of remaining sparks in the pool
			"PoolGlassSparksFree",      // Number of remaining glass sparks in the pool
			
			"ImageCacheHits",           // Number of image cache hits
			"ImageCacheMisses",         // Number of image cache misses
	};
	
	public static final String[] DEBUG_CATEGORY_NAMES = new String[] {
			"EntityBoundingBoxes",      // Bounding boxes for all entities
			"MirrorLines",              // Lines representing the mirrors
			"PlayerCircles",            // Circles representing the players
			"WormholeCircles",          // Circles representing the wormholes
			"ParticleCompositorRects",  // Rectangles inside which particle systems are compositing
	};
	
	public static final int PERF_NAME_MAX_WIDTH = 26;
	
}

