package model;

import util.Vector2D;

public class Bullet extends RectEntity {
	private Player owner;
	
	public Bullet(Vector2D position, Vector2D velocity, Player owner) {
		super(position, velocity);
		this.owner = owner;
	}
	
	public Player getOwner() {
		return owner;
	}
}
