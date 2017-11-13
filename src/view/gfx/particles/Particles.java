package view.gfx.particles;

import util.Vector2D;

public class Particles {
	private double[] particleX, particleY, particleVX, particleVY, particleTime, particleAlpha;

	public Particles(int count) {
		particleX = new double[count];
		particleY = new double[count];
		particleVX = new double[count];
		particleVY = new double[count];
		particleTime = new double[count];
		particleAlpha = new double[count];
	}

	public Vector2D getPosition(int i) {
		return new Vector2D(particleX[i], particleY[i]);
	}

	public Vector2D getVelocity(int i) {
		return new Vector2D(particleVX[i], particleVY[i]);
	}

	public double getTime(int i) {
		return particleTime[i];
	}

	public double getAlpha(int i) {
		return particleAlpha[i];
	}

	public void setPosition(int i, Vector2D position) {
		particleX[i] = position.getX();
		particleY[i] = position.getY();
	}

	public void setVelocity(int i, Vector2D velocity) {
		particleVX[i] = velocity.getX();
		particleVY[i] = velocity.getY();
	}

	public void setTime(int i, double time) {
		particleTime[i] = time;
	}

	public void setAlpha(int i, double alpha) {
		particleAlpha[i] = alpha;
	}

	public int size() {
		return particleX.length;
	}

	public void add(Vector2D position, Vector2D velocity, double time, double alpha) {
		double minTime = particleTime[0];
		int minI = 0;
		for (int i = 1; i < particleX.length; i++) {
			if (particleTime[i] < 0.0) {
				minI = i;
				break;
			}

			if (particleTime[i] < minTime) {
				minTime = particleTime[i];
				minI = i;
			}
		}

		particleX[minI] = position.getX();
		particleY[minI] = position.getY();
		particleVX[minI] = velocity.getX();
		particleVY[minI] = velocity.getY();
		particleTime[minI] = time;
		particleAlpha[minI] = alpha;
	}
}
