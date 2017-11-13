package memory;

import main.Constants;
import model.Asteroid;
import util.Vector2D;

public class AsteroidPool extends ObjectPool<Asteroid> {
	public AsteroidPool() {
		super(Constants.ASTEROID_POOL_SIZE, "PoolAsteroidFree");
	}

	@Override
	public Asteroid createEmpty() {
		return new Asteroid();
	}

	public Asteroid create(Vector2D position, Vector2D velocity, int type, int frame) {
		Asteroid obj = super.create();
		obj.init(position, velocity, type, frame);

		return obj;
	}
}
