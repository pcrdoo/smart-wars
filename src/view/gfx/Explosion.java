package view.gfx;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import util.ImageCache;
import util.Vector2D;
import view.Drawable;
import view.Updatable;

public class Explosion implements Drawable, Updatable {
	private Vector2D position;
	private BufferedImage flare, particle;
	private ParticleSystem debris;
	private PointParticleEmitter debrisEmitter;
	private double time;
	private double duration;
	private double debrisDuration;
	
	public Explosion(Vector2D position, double duration, double debrisDuration) {
		this.position = position;
		flare = ImageCache.getInstance().get("assets/explosion-flare.png");
		particle = ImageCache.getInstance().get("assets/debris-particle.png");
		this.duration = this.time = duration;
		debris = new ParticleSystem(particle, 100, 0.7);
		debrisEmitter = new PointParticleEmitter(200.0, 0.7, 0.0, position, new Vector2D(2, 2), 150.0, 10.0, 0, 2 * Math.PI);
		this.debrisDuration = debrisDuration;
		debris.addEmitter(debrisEmitter);
		debris.addAffector(new ParticleAffectorDeceleration(150));
		debris.update(0.1);

		debrisEmitter.setSpawnsPerSecond(0.0);
	}

	public boolean isDone() {
		return time <= 0.0;
	}
	
	@Override
	public void update(double dt) {
		time -= dt;
		if (time < 0.0) {
			time = 0.0;
			return;
		}
		
		if (time < duration - debrisDuration) {
		}
		
		debris.update(dt);
	}

	@Override
	public void draw(Graphics2D g) {
		Composite old = g.getComposite();
		
		g.setComposite(AdditiveComposite.getInstance((int)(255 * time / duration)));
		int x = (int)position.getdX(), y = (int)position.getdY(), w = flare.getWidth(), h = flare.getHeight();
		
		g.drawImage(flare, x - w / 2, y - h /2, x + w / 2, y + h / 2, 0, 0, w, h, null);
		g.setComposite(old);
		
		debris.draw(g);
	}
}
