package memory;

import main.Constants;
import util.Vector2D;
import view.gfx.GlassSparks;

public class GlassSparksPool extends ObjectPool<GlassSparks> {
	
	public GlassSparksPool() {
		super(Constants.GLASS_SPARKS_POOL_SIZE, "PoolGlassSparksFree");
	}

	@Override
	protected GlassSparks createEmpty() {
		return new GlassSparks();
	}

	public GlassSparks create(Vector2D position, double angle, boolean bottomSide, double duration, double debrisDuration) {
		GlassSparks obj = super.create();
		obj.init(position, angle, bottomSide, duration, debrisDuration);
		
		return obj;
	}
}
