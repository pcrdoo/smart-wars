package view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import model.Asteroid;
import util.ImageCache;
import util.Vector2D;
import view.gfx.Sparks;
import view.gfx.particles.ParticleAffectorDecay;
import view.gfx.particles.ParticleSystem;
import view.gfx.particles.PointParticleEmitter;
import view.gfx.particles.SpriteParticleRenderer;

public class AsteroidView extends EntityView {
	private Asteroid asteroid;
	private BufferedImage particleSprite, asteroidSprite;
	private ParticleSystem trail;
	private PointParticleEmitter trailEmitter;
	private Sparks sparks;

	private static final int MAX_PARTICLES = 30;
	private static final double PARTICLE_LIFETIME = 0.2;
	private static final double PARTICLES_PER_SECOND = 75;
	private static final double PARTICLE_MIN_EMIT_ANGLE = 0.4 * Math.PI;
	private static final double PARTICLE_MAX_EMIT_ANGLE = 0.6 * Math.PI;
	private static final double PARTICLE_VELOCITY = 50.0;
	private static final double PARTICLE_VELOCITY_JITTER = 0.0;
	private static final double PARTICLE_POSITION_JITTER = 2.0;
	private static final double PARTICLE_DECAY_TIME = 0.3;

	public AsteroidView(Asteroid asteroid) {
		this.asteroid = asteroid;

		particleSprite = ImageCache.getInstance().get("assets/fire.png");
		asteroidSprite = ImageCache.getInstance().get("assets/asteroid.png");
		trail = new ParticleSystem(new SpriteParticleRenderer(particleSprite), MAX_PARTICLES);
		trailEmitter = new PointParticleEmitter(PARTICLES_PER_SECOND, PARTICLE_LIFETIME, 0.0, asteroid.getPosition(),
				new Vector2D(PARTICLE_POSITION_JITTER, 0), PARTICLE_VELOCITY, PARTICLE_VELOCITY_JITTER,
				PARTICLE_MIN_EMIT_ANGLE, PARTICLE_MAX_EMIT_ANGLE);

		trail.addEmitter(trailEmitter);
		trail.addAffector(new ParticleAffectorDecay(PARTICLE_DECAY_TIME));
	}

	public void onAsteroidHit() {
		sparks = new Sparks(asteroid.getPosition(), 1.5, 0.15);
	}

	@Override
	public void update(double dt) {
		trailEmitter.setPosition(asteroid.getPosition());
		trail.update(dt);

		if (sparks != null) {
			sparks.update(dt);

			if (sparks.isDone()) {
				sparks = null;
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		//trail.draw(g);

		int w = asteroidSprite.getWidth(), h = asteroidSprite.getHeight();

		int x = (int) (asteroid.getPosition().getdX()) - w / 2, y = (int) (asteroid.getPosition().getdY()) - h / 2;

		g.drawImage(asteroidSprite, x, y, null);

		if (sparks != null) {
			sparks.draw(g);
		}

	}
}
