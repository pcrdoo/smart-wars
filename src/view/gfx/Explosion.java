package view.gfx;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import util.ImageCache;
import util.Vector2D;
import view.Drawable;
import view.Updatable;
import view.gfx.particles.ParticleAffectorDecay;
import view.gfx.particles.ParticleAffectorDeceleration;
import view.gfx.particles.ParticleSystem;
import view.gfx.particles.PointParticleEmitter;
import view.gfx.particles.SpriteParticleRenderer;

public class Explosion extends TimedGfx implements Drawable, Updatable {
	private Vector2D position;
	private BufferedImage flare, particle;
	private ParticleSystem debris;
	private PointParticleEmitter debrisEmitter;
	private double debrisDuration;
	
	public Explosion(Vector2D position, double duration, double debrisDuration) {
		super(duration);
		
		this.position = position;
		flare = ImageCache.getInstance().get("assets/explosion-flare.png");
		particle = ImageCache.getInstance().get("assets/debris-particle.png");
		debris = new ParticleSystem(new SpriteParticleRenderer(particle), 100);
		debrisEmitter = new PointParticleEmitter(500.0, 0.7, 0.0, position, new Vector2D(2, 2), 150.0, 10.0, 0, 2 * Math.PI);
		this.debrisDuration = debrisDuration;
		debris.addEmitter(debrisEmitter);
		debris.addAffector(new ParticleAffectorDeceleration(150));
		debris.addAffector(new ParticleAffectorDecay(0.7));
		debris.update(0.1);
	}
	
	@Override
	public void update(double dt) {
		super.update(dt);
		
		if (time < duration - debrisDuration) {
			debrisEmitter.setSpawnsPerSecond(0.0);
		}
		
		debris.update(dt);
	}

	@Override
	public void draw(Graphics2D g) {
		Composite old = g.getComposite();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(time / duration)));
		int x = (int)position.getdX(), y = (int)position.getdY(), w = flare.getWidth(), h = flare.getHeight();
		
		g.drawImage(flare, x - w / 2, y - h / 2, null);
		g.setComposite(old);
		
		debris.draw(g);
	}
}
