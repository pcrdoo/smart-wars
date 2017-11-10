package view.gfx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;

import util.Vector2D;
import view.Drawable;
import view.Updatable;
import view.gfx.particles.LineParticleRenderer;
import view.gfx.particles.ParticleAffectorDecay;
import view.gfx.particles.ParticleAffectorDeceleration;
import view.gfx.particles.ParticleSystem;
import view.gfx.particles.PointParticleEmitter;
import view.gfx.particles.SpriteParticleRenderer;

public class Sparks extends TimedGfx implements Updatable, Drawable {
	private static final int SPARK_LEFT_COLOR = 0xffA02200, SPARK_RIGHT_COLOR = 0xffFFDD20;
	private Vector2D position;
	private ParticleSystem sparks;
	private PointParticleEmitter sparksEmitter;
	private double sparkDuration;
	public Sparks(Vector2D position, double duration, double sparkDuration) {
		super(duration);
		
		sparks = new ParticleSystem(new LineParticleRenderer(new Color(SPARK_LEFT_COLOR), new Color(SPARK_RIGHT_COLOR), 1.0, 10.0, 0.0, 350.0), 200);
		sparksEmitter = new PointParticleEmitter(700.0, 0.8, 0.0, position, new Vector2D(5, 5), 150.0, 30.0, 0, 2 * Math.PI);
		this.sparkDuration = sparkDuration;
		sparks.addEmitter(sparksEmitter);
		sparks.addAffector(new ParticleAffectorDeceleration(150));
		sparks.addAffector(new ParticleAffectorDecay(0.8));
		//sparks.update(0.1);
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		
		if (time < duration - sparkDuration) {
			sparksEmitter.setSpawnsPerSecond(0.0);
		}
		
		sparks.update(dt);
	}

	@Override
	public void draw(Graphics2D g) {
		sparks.draw(g);
	}
}
