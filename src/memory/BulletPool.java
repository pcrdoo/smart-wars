package memory;

import main.Constants;
import model.Bullet;
import model.Player;
import util.Vector2D;

public class BulletPool extends ObjectPool<Bullet> {

	public BulletPool() {
		super(Constants.BULLET_POOL_SIZE, "PoolBulletFree");
	}

	@Override
	public Bullet createEmpty() {
		return new Bullet();
	}
	
	public Bullet create(Vector2D position, Vector2D velocity, Player owner) {
		Bullet obj = super.create();
		obj.init(position, velocity, owner);
		
		return obj;
	}
}
