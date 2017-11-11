package view.gfx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import debug.Measurement;
import debug.PerformanceMonitor;
import memory.Poolable;
import util.Vector2D;
import view.Drawable;
import view.gfx.particles.LineParticleRenderer;
import view.gfx.particles.ParticleAffectorDecay;
import view.gfx.particles.ParticleAffectorDeceleration;
import view.gfx.particles.ParticleSystem;
import view.gfx.particles.PointParticleEmitter;

public class GlassSparks extends TimedGfx implements Drawable, Poolable {
	private static final int SPARK_LEFT_COLOR = 0xff227f7f, SPARK_RIGHT_COLOR = 0xffffffff;
	private Vector2D position;
	private double angle;
	private ParticleSystem sparks;
	private PointParticleEmitter sparksEmitter;
	private double sparkDuration;
	
	public GlassSparks() {
		super();
		
		sparks = new ParticleSystem(new LineParticleRenderer(new Color(SPARK_LEFT_COLOR), new Color(SPARK_RIGHT_COLOR), 1.0, 10.0, 0.0, 350.0), 200);
		sparksEmitter = new PointParticleEmitter(0.0, 0.5, 0.0, null, new Vector2D(5, 5), 350.0, 30.0, 0, 0);
		
		sparks.addEmitter(sparksEmitter);
		sparks.addAffector(new ParticleAffectorDeceleration(350));
		sparks.addAffector(new ParticleAffectorDecay(0.5));
	}

	public void init(Vector2D position, double angle, boolean bottomSide, double duration, double sparkDuration) {
		super.init(duration);
		
		this.sparkDuration = sparkDuration;
		this.position = position;
		
		this.angle = angle + (bottomSide? -1 : 1) * Math.PI / 2;
		sparksEmitter.setPosition(position);
		sparksEmitter.setAngles(this.angle - 0.4, this.angle + 0.4);
		sparksEmitter.setSpawnsPerSecond(400.0);
	}
	
	@Override
	public void reset() {
		sparks.reset();
		this.position = null;
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
		Measurement ms = PerformanceMonitor.getInstance().measure("CompGlassSparks");
		sparks.draw(g);
		ms.done();
	}
}
