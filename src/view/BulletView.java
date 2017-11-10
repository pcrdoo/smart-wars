package view;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import model.Bullet;
import util.ImageCache;
import util.Vector2D;
import view.gfx.ParticleAffectorDecay;
import view.gfx.ParticleSystem;
import view.gfx.PointParticleEmitter;

public class BulletView implements Drawable, Updatable {
	private Bullet bullet;
	private BufferedImage bulletSprite, particleSprite;
	private ParticleSystem trail;
	private PointParticleEmitter trailEmitter;

	private static final int MAX_PARTICLES = 30;
	private static final double PARTICLE_LIFETIME = 0.2;
	private static final double PARTICLES_PER_SECOND = 75;
	private static final double PARTICLE_MIN_EMIT_ANGLE = 0.6 * Math.PI;
	private static final double PARTICLE_MAX_EMIT_ANGLE = 1.4 * Math.PI;
	private static final double PARTICLE_VELOCITY = -50.0;
	private static final double PARTICLE_VELOCITY_JITTER = 0.0;
	private static final double PARTICLE_POSITION_JITTER = 2.0;
	private static final double PARTICLE_DECAY_TIME = 0.3;

	public BulletView(Bullet bullet) {
		this.bullet = bullet;

		particleSprite = ImageCache.getInstance().get("assets/fire.png");
		bulletSprite = ImageCache.getInstance().get("assets/bullet.png");
		trail = new ParticleSystem(particleSprite, MAX_PARTICLES, PARTICLE_DECAY_TIME);
		trailEmitter = new PointParticleEmitter(PARTICLES_PER_SECOND, PARTICLE_LIFETIME, 0.0, bullet.getPosition(),
				new Vector2D(PARTICLE_POSITION_JITTER, 0), PARTICLE_VELOCITY, PARTICLE_VELOCITY_JITTER,
				PARTICLE_MIN_EMIT_ANGLE, PARTICLE_MAX_EMIT_ANGLE);

		trail.addEmitter(trailEmitter);
		trail.addAffector(new ParticleAffectorDecay(PARTICLE_DECAY_TIME));
	}

	@Override
	public void update(double dt) {
		trailEmitter.setPosition(bullet.getPosition());
		trail.update(dt);
	}

	@Override
	public void draw(Graphics2D g) {
		//trail.draw(g);
		
		int w = bulletSprite.getWidth(),
				h = bulletSprite.getHeight();
		
		int x = (int)(bullet.getPosition().getdX()) - w / 2,
				y = (int)(bullet.getPosition().getdY()) - h / 2;
		
		g.drawImage(bulletSprite, x, y, null);
	}
}
