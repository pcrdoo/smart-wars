package view.gfx;

public class ParticleAffectorDecay implements ParticleAffector {
	private double decayStartTime;
	
	public ParticleAffectorDecay(double decayStartTime) {
		this.decayStartTime = decayStartTime;
	}
	
	@Override
	public void updateAndAffect(double dt, Particles particles) {
		for (int i = 0; i < particles.size(); i++) {
			if (particles.getTime(i) < decayStartTime) {
				particles.setAlpha(i, particles.getTime(i) / decayStartTime);
			}
		}
	}
}
