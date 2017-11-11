package memory;

import main.Constants;
import model.Bullet;
import view.BulletView;

public class BulletViewPool extends ObjectPool<BulletView> {
	public BulletViewPool() {
		super(Constants.BULLET_POOL_SIZE, "PoolBulletViewFree");
	}

	@Override
	protected BulletView createEmpty() {
		return new BulletView();
	}
	
	public BulletView create(Bullet bullet) {
		BulletView obj = super.create();
		obj.init(bullet);
		
		return obj;
	}
}
