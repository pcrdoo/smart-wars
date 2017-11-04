package view.gfx;

public class ParticleAffectorDeceleration implements ParticleAffector {
	private double decelFactor;
	public ParticleAffectorDeceleration(double decelFactor) {
		this.decelFactor = decelFactor;
	}
	
	@Override
	public void updateAndAffect(double dt, double[] particleX, double[] particleY, double[] particleVX,
			double[] particleVY, double[] particleTime) {
		for (int i = 0; i < particleX.length; i++) {
			double len = Math.sqrt(particleVX[i] * particleVX[i] + particleVY[i] * particleVY[i]);
			double newLen = Math.max(0, len - decelFactor * dt);
			double lenFactor = newLen / len;
			
			particleVX[i] *= lenFactor;
			particleVY[i] *= lenFactor;
		}
	}

}
