package model.abilities;

import view.Updatable;

public abstract class Ability implements Updatable {
	protected double maxCooldown;
	protected double cooldown;
	
	public Ability(double cooldown) {
		this.maxCooldown = this.cooldown = cooldown;
	}
	//TODO:terminologija
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
