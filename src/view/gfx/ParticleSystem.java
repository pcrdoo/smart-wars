package view.gfx;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import view.Drawable;
import view.Updatable;

public class ParticleSystem implements Drawable, Updatable {
	private double[] particleX, particleY, particleVX, particleVY, particleTime;
	private double decayStartTime;
	private int partWidth, partHeight;
	private BufferedImage sprite;
	private ArrayList<ParticleEmitter> emitters;
	private ArrayList<ParticleAffector> affectors;
	private int maxParticles;
	
	public ParticleSystem(BufferedImage sprite, int maxParticles, double decayStartTime) {
		emitters = new ArrayList<>();
		affectors = new ArrayList<>();
		
		this.maxParticles = maxParticles;
		this.decayStartTime = decayStartTime;
		this.sprite = sprite;
		this.partWidth = sprite.getWidth();
		this.partHeight = sprite.getHeight();
		
		particleX = new double[maxParticles];
		particleY = new double[maxParticles];
		particleVX = new double[maxParticles];
		particleVY = new double[maxParticles];
		particleTime = new double[maxParticles];
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
			e.updateAndEmit(dt, particleX, particleY, particleVX, particleVY, particleTime);
		}
		
		// Affect existing particles
		for (ParticleAffector a : affectors) {
			a.updateAndAffect(dt, particleX, particleY, particleVX, particleVY, particleTime);
		}
		
		// Time-step the particles
		for (int i = 0; i < maxParticles; i++) {
			if (particleTime[i] > 0.0) {
				particleX[i] += particleVX[i] * dt;
				particleY[i] += particleVY[i] * dt;
				particleTime[i] -= dt;
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		Composite old = g.getComposite();
		for (int i = 0; i < maxParticles; i++) {
			if (particleTime[i] > 0.0) {
				int intensity = 255;
				if (particleTime[i] < decayStartTime) {
					intensity = Math.max(0, (int)(255.0 * particleTime[i] / decayStartTime));
				}
				
				g.setComposite(AdditiveComposite.getInstance(intensity));
				
				g.drawImage(sprite, (int)particleX[i] - partWidth / 2, (int)particleY[i] - partHeight / 2,
						(int)particleX[i] + partWidth / 2, (int)particleY[i] + partHeight / 2, 0, 0, partWidth, partHeight, null);
			}
		}
		g.setComposite(old);
	}
	
}
