package view.gfx;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Constants;
import view.Drawable;
import view.Updatable;

public class ParticleSystem implements Drawable, Updatable {
	private ParticleCompositor compositor;
	private Particles particles;
	private double decayStartTime; // particleAlpha pa affector TODO 
	private int partWidth, partHeight;
	private BufferedImage sprite;
	private ArrayList<ParticleEmitter> emitters;
	private ArrayList<ParticleAffector> affectors;
	private int maxParticles;
	
	public ParticleSystem(BufferedImage sprite, int maxParticles, double decayStartTime) {
		particles = new Particles(maxParticles);
		
		emitters = new ArrayList<>();
		affectors = new ArrayList<>();
		
		this.maxParticles = maxParticles;
		this.decayStartTime = decayStartTime;
		this.sprite = sprite;
		this.partWidth = sprite.getWidth();
		this.partHeight = sprite.getHeight();
		
		compositor = new ParticleCompositor(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
	}
	
	public void addEmitter(ParticleEmitter emitter) {
		emitters.add(emitter);
	}
	
	public void removeEmitter(ParticleEmitter emitter) {
		emitters.remove(emitter);
	}
	
	public void addAffector(ParticleAffector affector) {
		affectors.add(affector);
	}
	
	public void removeAffector(ParticleAffector affector) {
		affectors.remove(affector);
	}
	
	@Override
	public void update(double dt) {
		// Emit new particles
		for (ParticleEmitter e : emitters) {
			e.updateAndEmit(dt, particles);
		}
		
		// Affect existing particles
		for (ParticleAffector a : affectors) {
			a.updateAndAffect(dt, particles);
		}
		
		// Time-step the particles
		for (int i = 0; i < maxParticles; i++) {
			if (particles.getTime(i) > 0.0) {
				particles.setPosition(i, particles.getPosition(i).add(particles.getVelocity(i).scale(dt)));
				particles.setTime(i, particles.getTime(i) - dt);
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		compositor.compose(g, particles, sprite);
	}
	
}
