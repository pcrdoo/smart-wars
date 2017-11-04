package view.gfx;

public interface ParticleAffector {
	void updateAndAffect(double dt, double[] particleX, double[] particleY, double[] particleVX, double[] particleVY, double[] particleTime);
}
