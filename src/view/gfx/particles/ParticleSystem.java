package view.gfx.particles;

import java.awt.Graphics2D;
import java.util.ArrayList;

import view.Drawable;
import view.Updatable;

public class ParticleSystem implements Drawable, Updatable {
	private Particles particles;
	private ArrayList<ParticleEmitter> emitters;
	private ArrayList<ParticleAffector> affectors;
	private int maxParticles;
	private ParticleRenderer renderer;

	public ParticleSystem(ParticleRenderer renderer, int maxParticles) {
		particles = new Particles(maxParticles);

		emitters = new ArrayList<>();
		affectors = new ArrayList<>();

		this.maxParticles = maxParticles;
		this.renderer = renderer;
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

	public void reset() {
		for (int i = 0; i < maxParticles; i++) {
			particles.setTime(i, -1.0);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		renderer.render(g, particles);
	}

}
