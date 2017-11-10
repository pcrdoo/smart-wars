package model;

import java.awt.Rectangle;

import main.Constants;
import model.entitites.RectEntity;
import util.Vector2D;

public class Bullet extends RectEntity {
	private Player owner;
	private double damage;

	public Bullet(Vector2D position, Vector2D velocity, Player owner) {
		super(position, velocity, Constants.BULLET_SIZE);
		this.owner = owner;
		this.damage = Constants.BULLET_DAMAGE;
	}

	public Player getOwner() {
		return owner;
	}

	public double getDamage() {
		return damage;
	}

	@Override
	public boolean shouldCull() {
		Rectangle boundingBox = getBoundingBox();
		if (owner.getPlayerSide() == PlayerSide.LEFT_PLAYER && boundingBox.getMinX() > Constants.WINDOW_WIDTH) {
			return true;
		} else if (owner.getPlayerSide() == PlayerSide.RIGHT_PLAYER && boundingBox.getMinX() < 0) {
			return true;
		}
		return false;
	}

	public void bounce(Mirror mirror) {
		// TODO bounce
	}
}
