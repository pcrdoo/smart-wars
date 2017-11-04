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
	
	public Bullet fire(Vector2D position) {
		if (!super.fire()) {
			return null;
		}
		
		Vector2D velocity = new Vector2D(0, position.getdY() < Constants.WINDOW_HEIGHT / 2 ? Constants.BULLET_SPEED : -Constants.BULLET_SPEED);
		return new Bullet(position, velocity, player);
	}
}
