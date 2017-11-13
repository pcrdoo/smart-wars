package memory;

public class Pools {
	public static final BulletPool BULLET = new BulletPool();
	public static final BulletViewPool BULLET_VIEW = new BulletViewPool();
	public static final AsteroidPool ASTEROID = new AsteroidPool();
	public static final AsteroidViewPool ASTEROID_VIEW = new AsteroidViewPool();
	public static final ExplosionPool EXPLOSION = new ExplosionPool();
	public static final SparksPool SPARKS = new SparksPool();
	public static final GlassSparksPool GLASS_SPARKS = new GlassSparksPool();
	
	public static void repopulate(boolean view) {
		BULLET.repopulate();
		BULLET_VIEW.repopulate();
		ASTEROID.repopulate();
		ASTEROID_VIEW.repopulate();
		if (view) {
			EXPLOSION.repopulate();
			SPARKS.repopulate();
			GLASS_SPARKS.repopulate();
		}
	}
}
