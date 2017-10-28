package model.abilities;

public class Ability {
	protected double maxCooldown;
	protected double cooldown;
	
	public void update(double dt) {
		this.cooldown -= dt;
		this.cooldown = Math.max(this.cooldown, 0);
	}
	
	public boolean isAvailable() {
		return this.cooldown == 0;
	}
	
	public boolean isBusy() {
		return this.cooldown > 0;
	}
	
	public boolean fire() {
		if(this.isBusy())
			return false;
		this.cooldown = this.maxCooldown;
		return true;
	}
}
