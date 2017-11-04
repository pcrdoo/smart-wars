package view.gfx;

import util.Vector2D;

public class PointParticleEmitter implements ParticleEmitter {
	private double spawnsPerSecond;
	private Vector2D position, positionJitter;
	private double velocity, velocityJitter;
	private double minAngle, maxAngle;
	private double emitInterval;
	private double accumulTime;
	private double lifetime, lifetimeJitter;

	public PointParticleEmitter(double spawnsPerSecond, double lifetime, double lifetimeJitter, Vector2D position, Vector2D positionJitter,
			double velocity, double velocityJitter, double minAngle, double maxAngle) {
		this.spawnsPerSecond = spawnsPerSecond;
		this.position = position;
		this.positionJitter = positionJitter;
		this.velocity = velocity;
		this.velocityJitter = velocityJitter;
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
		this.lifetime = lifetime;
		this.lifetimeJitter = lifetimeJitter;
		emitInterval = 1 / spawnsPerSecond;
	}

	public void setPosition(Vector2D newPosition) {
		position = newPosition;
	}

	public void setSpawnsPerSecond(double spawnsPerSecond) {
		this.spawnsPerSecond = spawnsPerSecond;
		emitInterval = 1 / spawnsPerSecond;
	}
	
	private void spawnAt(int i, double[] particleX, double[] particleY, double[] particleVX, double[] particleVY,
			double[] particleTime) {
		double x = position.getdX() + (2.0 * Math.random() - 1.0) * positionJitter.getdX(),
				y = position.getdY() + (2.0 * Math.random() - 1.0) * positionJitter.getdY();
		
		double angle = Math.random() * (maxAngle - minAngle) + minAngle;
		Vector2D v = new Vector2D(Math.sin(angle), Math.cos(angle)).scale(velocity + (2.0 * Math.random() - 1.0) * velocityJitter);
		double t = lifetime + (2.0 * Math.random() - 1.0) * lifetimeJitter;
		
		particleX[i] = x;
		particleY[i] = y;
		particleVX[i] = v.getdX();
		particleVY[i] = v.getdY();
		particleTime[i] = t;
	}
	
	private void spawn(double[] particleX, double[] particleY, double[] particleVX, double[] particleVY,
			double[] particleTime) {
		double minTime = 1.0;
		int minI = 0;
		for (int i = 1; i < particleX.length; i++) {
			if (particleTime[i] < minTime) {
				minTime = particleTime[i];
				minI = i;
			}
		}

		// Spawn the new particle instead of the one with the minimal time
		spawnAt(minI, particleX, particleY, particleVX, particleVY, particleTime);
	}

	@Override
	public void updateAndEmit(double dt, double[] particleX, double[] particleY, double[] particleVX,
			double[] particleVY, double[] particleTime) {
		accumulTime += dt;
		while (accumulTime >= emitInterval) {
			accumulTime -= emitInterval;
			spawn(particleX, particleY, particleVX, particleVY, particleTime);
		}
	}

}
