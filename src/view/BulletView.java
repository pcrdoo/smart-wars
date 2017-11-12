package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Constants;
import memory.Poolable;
import memory.Pools;
import model.Bullet;
import model.Wormhole;
import util.ImageCache;
import util.Vector2D;
import view.gfx.particles.ParticleAffectorDecay;
import view.gfx.particles.ParticleSystem;
import view.gfx.particles.PointParticleEmitter;
import view.gfx.particles.AdditiveSpriteParticleRenderer;

public class BulletView extends EntityView implements Poolable {
	private Bullet bullet;
	private BufferedImage bulletSprite;
	private double alpha;

	public BulletView() {
		bulletSprite = ImageCache.getInstance().get("bullet.png");
	}

	@Override
	public void reset() {
		bullet = null;
	}
	
	@Override
	public void onRemoved() {
		Pools.BULLET_VIEW.free(this);
	}
	public void init(Bullet bullet) {
		this.bullet = bullet;
		alpha = 1.0;
	}

	public void wormholeAffect(Wormhole w) {
		double distance = bullet.getPosition().sub(w.getPosition()).length();
		if (distance < Constants.WORMHOLE_BULLET_FADE_DISTANCE) {
			alpha = distance / Constants.WORMHOLE_BULLET_FADE_DISTANCE;
		} else {
			alpha = 1.0;
		}
	}
	
	@Override
	public void update(double dt) {
	}

	@Override
	public void draw(Graphics2D g) {
		int w = bulletSprite.getWidth(),
				h = bulletSprite.getHeight();
		
		int x = (int)(bullet.getPosition().getX()) - w / 2,
				y = (int)(bullet.getPosition().getY()) - h / 2;
		
		Composite old = g.getComposite();

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)alpha));
		g.drawImage(bulletSprite, x, y, null);
		g.setComposite(old);
	}
}