package view.gfx;

public interface ParticleEmitter {
	void updateAndEmit(double dt, double[] particleX, double[] particleY, double[] particleVX, double[] particleVY, double[] particleTime);
}
