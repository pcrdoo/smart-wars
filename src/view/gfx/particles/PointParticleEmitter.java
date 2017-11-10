package view.gfx.particles;

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
	
	private void spawn(Particles particles) {
		Vector2D newPosition = position.add(positionJitter.scale(2.0 * Math.random() - 1.0));
		
		double angle = Math.random() * (maxAngle - minAngle) + minAngle;
		Vector2D newVelocity = new Vector2D(Math.sin(angle), Math.cos(angle)).scale(velocity + (2.0 * Math.random() - 1.0) * velocityJitter);
		double t = lifetime + (2.0 * Math.random() - 1.0) * lifetimeJitter;
		
		particles.add(newPosition, newVelocity, t, 1.0);
	}

	@Override
	public void updateAndEmit(double dt, Particles particles) {
		accumulTime += dt;
		while (accumulTime >= emitInterval) {
			accumulTime -= emitInterval;
			spawn(particles);
		}
	}

}
