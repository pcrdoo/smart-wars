package memory;

import main.Constants;
import model.Asteroid;
import view.AsteroidView;

public class AsteroidViewPool extends ObjectPool<AsteroidView> {
	public AsteroidViewPool() {
		super(Constants.ASTEROID_POOL_SIZE, "PoolAsteroidViewFree");
	}

	@Override
	protected AsteroidView createEmpty() {
		return new AsteroidView();
	}

	public AsteroidView create(Asteroid asteroid) {
		AsteroidView obj = super.create();
		obj.init(asteroid);

		return obj;
	}
}
