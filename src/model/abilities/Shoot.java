package model.abilities;

import main.Constants;
import model.Bullet;
import model.Player;
import util.Vector2D;

public class Shoot extends Ability {
	private Player player;
	
	public Shoot(Player player) {
		super(Constants.BULLET_COOLDOWN);
		this.player = player;
	}
	
	public Bullet fire(Vector2D position, Vector2D velocity) {
		if (!super.fire()) {
			return null;
		}
		return new Bullet(position, velocity, player);
	}
}
