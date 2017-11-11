package model.abilities;

import main.Constants;
import memory.Pools;
import model.Bullet;
import model.Player;
import util.Vector2D;

public class Gun extends Ability {
	private Player player;

	public Gun(Player player) {
		super(Constants.BULLET_COOLDOWN);
		this.player = player;
	}

	public Bullet fireBullet(Vector2D position, Vector2D velocity) {
		if (!execute()) {
			return null;
		}
		return Pools.BULLET.create(position, velocity, player);
	}
}
