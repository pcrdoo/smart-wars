package view.gfx;

import util.Vector2D;

public class Particles {
	private double[] particleX, particleY, particleVX, particleVY, particleTime;
	
	public Particles(int count) {
		particleX = new double[count];
		particleY = new double[count];
		particleVX = new double[count];
		particleVY = new double[count];
		particleTime = new double[count];
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
	
	public void setPosition(int i, Vector2D position) {
		particleX[i] = position.getdX();
		particleY[i] = position.getdY();
	}
	
	public void setVelocity(int i, Vector2D velocity) {
		particleVX[i] = velocity.getdX();
		particleVY[i] = velocity.getdY();
	}
	
	public void setTime(int i, double time) {
		particleTime[i] = time;
	}
	
	public int size() {
		return particleX.length;
	}
	
	public void add(Vector2D position, Vector2D velocity, double time) {
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

		particleX[minI] = position.getdX();
		particleY[minI] = position.getdY();
		particleVX[minI] = velocity.getdX();
		particleVY[minI] = velocity.getdY();
		particleTime[minI] = time;
	}
}
