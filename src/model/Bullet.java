package model;

import java.awt.Rectangle;

import main.Constants;
import memory.Poolable;
import model.entitites.RectEntity;
import util.Vector2D;

public class Bullet extends RectEntity implements Poolable {
	private Player owner;
	private double damage;
	private int bounces;
	private double teleportCooldown;

	public Bullet() {
		super(null, null, Constants.BULLET_SIZE);
		damage = Constants.BULLET_DAMAGE;
	}
	
	public void init(Vector2D position, Vector2D velocity, Player owner) {
		this.position = position;
		this.velocity = velocity;
		this.owner = owner;
		teleportCooldown = 0;
		bounces = 0;
	}
	
	public void reset() {
		this.position = null;
		this.velocity = null;
		this.owner = null;
	}

	public Player getOwner() {
		return owner;
	}

	public double getDamage() {
		return damage;
	}

	private Vector2D reflect(Vector2D velocity, Vector2D line) {
		Vector2D dir = line.normalize();
		Vector2D perp = new Vector2D(-dir.getY(), dir.getX());
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
		if (bounces > Constants.MAX_BULLET_BOUNCES) {
			return true;
		}
		
		Rectangle boundingBox = getBoundingBox();
		Rectangle screen = new Rectangle(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		
		return !boundingBox.intersects(screen);
	}

	public int getBounces() {
		return bounces;
	}
	
	public void bounce(Mirror mirror) {
		setVelocity(reflect(getVelocity(), mirror.getLineVector()));
		bounces++;
	}

	public void teleport(Wormhole inWormhole, Wormhole outWormhole) {
		if (teleportCooldown == 0) {
			Vector2D toCenter = inWormhole.getPosition().sub(position);
			position = outWormhole.getPosition().add(toCenter);
			teleportCooldown = Constants.BULLET_TELEPORT_COOLDOWN;
		}
	}
}
