package view.gfx.particles;

public class ParticleAffectorDeceleration implements ParticleAffector {
	private double decelFactor;
	public ParticleAffectorDeceleration(double decelFactor) {
		this.decelFactor = decelFactor;
	}
	
	@Override
	public void updateAndAffect(double dt, Particles particles) {
		for (int i = 0; i < particles.size(); i++) {
			double len = particles.getVelocity(i).length();
			double newLen = Math.max(0, len - decelFactor * dt);
			double lenFactor = newLen / len;
			
			particles.setVelocity(i, particles.getVelocity(i).scale(lenFactor));
		}
	}
}
