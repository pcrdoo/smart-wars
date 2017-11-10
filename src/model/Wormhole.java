package model;

import main.Constants;
import model.abilities.Ability;
import model.entitites.RoundEntity;
import util.Vector2D;

public class Wormhole extends RoundEntity {

	private double timeToLive;

	public Wormhole(Vector2D position) {
		super(position, Constants.WORMHOLE_RADIUS);
		timeToLive = Constants.WORMHOLE_LIFETIME;
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		timeToLive -= dt;
		if (timeToLive < 0) {
			timeToLive = 0;
		}
	}

	public boolean isDead() {
		return timeToLive == 0;
	}

	@Override
	public boolean shouldCull() {
		return false;
	}

}
