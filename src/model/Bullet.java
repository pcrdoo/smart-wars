package model;

import java.awt.Rectangle;

import main.Constants;
import model.entitites.RectEntity;
import util.Vector2D;

public class Bullet extends RectEntity {
	private Player owner;
	private double damage;
	private double teleportCooldown;

	public Bullet(Vector2D position, Vector2D velocity, Player owner) {
		super(position, velocity, Constants.BULLET_SIZE);
		this.owner = owner;
		damage = Constants.BULLET_DAMAGE;
		teleportCooldown = 0;
	}

	public Player getOwner() {
		return owner;
	}

	public double getDamage() {
		return damage;
	}

	private Vector2D reflect(Vector2D velocity, Vector2D line) {
		Vector2D dir = line.normalize();
		Vector2D perp = new Vector2D(-dir.getdY(), dir.getdX());
		return velocity.sub(perp.scale(2.0 * perp.dotProduct(velocity)));
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		teleportCooldown -= dt;
		if (teleportCooldown < 0) {
			teleportCooldown = 0;
		}
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
		setVelocity(reflect(getVelocity(), mirror.getLineVector()));
		System.out.println("reflekty");
	}

	public void teleport(Wormhole inWormhole, Wormhole outWormhole) {
		if (teleportCooldown == 0) {
			System.out.println(inWormhole.getPosition());
			System.out.println("TO");
			System.out.println(outWormhole.getPosition());
			Vector2D toCenter = inWormhole.getPosition().sub(position);
			position = outWormhole.getPosition().add(toCenter);
			teleportCooldown = Constants.BULLET_TELEPORT_COOLDOWN;
		}
	}
}
