package view.gfx.particles;

import util.Vector2D;

public class ParticleAffectorRotation implements ParticleAffector {
	private Vector2D center;
	private double speed;
	
	public ParticleAffectorRotation(Vector2D center, double speed) {
		this.center = center;
		this.speed = speed;
	}
	
	@Override
	public void updateAndAffect(double dt, Particles particles) {
		for (int i = 0; i < particles.size(); i++) {
			if (particles.getTime(i) > 0.0) {
				Vector2D pos = particles.getPosition(i);
				double angle = speed * dt;
				double sine = Math.sin(angle), cosine = Math.cos(angle);
				Vector2D dPos = pos.sub(center);
				
				double newX = dPos.getdX() * cosine - dPos.getdY() * sine,
					   newY = dPos.getdX() * sine + dPos.getdY() * cosine;
				
				particles.setPosition(i, new Vector2D(newX, newY).add(center));
			}
		}
	}
	
}
