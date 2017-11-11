package memory;

import main.Constants;
import util.Vector2D;
import view.gfx.Sparks;

public class SparksPool extends ObjectPool<Sparks> {
	public SparksPool() {
		super(Constants.SPARKS_POOL_SIZE, "PoolSparksFree");
	}

	@Override
	protected Sparks createEmpty() {
		return new Sparks();
	}

	public Sparks create(Vector2D position, double duration, double debrisDuration) {
		Sparks obj = super.create();
		obj.init(position, duration, debrisDuration);
		
		return obj;
	}
}
