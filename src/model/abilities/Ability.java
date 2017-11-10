package model.abilities;

public abstract class Ability {
	protected double timeCooldown;
	protected double timeToWait;

	public Ability(double timeCooldown) {
		this.timeCooldown = timeCooldown;
		timeToWait = timeCooldown;
	}

	public void update(double dt) {
		timeToWait -= dt;
		timeToWait = Math.max(timeToWait, 0);
	}

	public boolean isOnCooldown() {
		return timeToWait > 0;
	}

	public boolean execute() {
		if (isOnCooldown()) {
			return false;
		}
		timeToWait = timeCooldown;
		return true;
	}
}
