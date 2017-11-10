package model;

import main.Constants;
import util.Vector2D;

public class Bullet extends RectEntity {
	private Player owner;
	private double damage;
	
	public Bullet(Vector2D position, Vector2D velocity, Player owner) {
		super(position, velocity);
		this.owner = owner;
		this.damage = Constants.BULLET_DAMAGE;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public double getDamage() {
		return damage;
	}
}
