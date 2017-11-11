package memory;

import main.Constants;
import util.Vector2D;
import view.gfx.Explosion;

public class ExplosionPool extends ObjectPool<Explosion> {
	public ExplosionPool() {
		super(Constants.EXPLOSION_POOL_SIZE, "PoolExplosionFree");
	}

	@Override
	protected Explosion createEmpty() {
		return new Explosion();
	}

	public Explosion create(Vector2D position, double duration, double debrisDuration) {
		Explosion obj = super.create();
		obj.init(position, duration, debrisDuration);
		
		return obj;
	}
}
