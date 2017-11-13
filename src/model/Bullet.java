package model;

import java.awt.Rectangle;
import java.nio.ByteBuffer;

import main.Constants;
import memory.Poolable;
import model.entitites.RectEntity;
import multiplayer.SerializationHelpers;
import util.Vector2D;

public class Bullet extends RectEntity implements Poolable {
	private final static int SERIALIZED_SIZE = 16 + 4 + 4 + 8;
	
	private Player owner;
	private double damage;
	private int bounces;
	private double velocityChangeCooldown;
	
	public Bullet() {
		super(null, null, Constants.BULLET_SIZE);
		damage = Constants.BULLET_DAMAGE;
	}
	
	public void init(Vector2D position, Vector2D velocity, Player owner) {
		this.position = position;
		this.velocity = velocity;
		this.owner = owner;
		velocityChangeCooldown = 0;
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
		velocityChangeCooldown -= dt;
		if (velocityChangeCooldown < 0) {
			velocityChangeCooldown = 0;
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
	
	public boolean bounce(Mirror mirror) {
		if (velocityChangeCooldown == 0) {
			setVelocity(reflect(getVelocity(), mirror.getLineVector()));
			bounces++;
			velocityChangeCooldown = Constants.BULLET_VELOCITY_CHANGE_COOLDOWN;
			return true;
		}
		return false;
	}

	public boolean teleport(Wormhole inWormhole, Wormhole outWormhole) {
		if (velocityChangeCooldown == 0) {
			Vector2D toCenter = inWormhole.getPosition().sub(position);
			position = outWormhole.getPosition().add(toCenter);
			velocityChangeCooldown = Constants.BULLET_VELOCITY_CHANGE_COOLDOWN;
			return true;
		}
		return false;
	}
	
	@Override
	public void serializeTo(ByteBuffer buffer) {
		super.serializeTo(buffer);
		SerializationHelpers.serializeUuid(buffer, owner.getUuid());
		buffer.putFloat((float)damage);
		buffer.putInt(bounces);
		buffer.putDouble(velocityChangeCooldown);
	}
	
	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		super.deserializeFrom(model, buffer);
		owner = (Player) model.getEntityById(SerializationHelpers.deserializeUuid(buffer));
		damage = buffer.getFloat();
		bounces = buffer.getInt();
		velocityChangeCooldown = buffer.getDouble();
	}
	
	@Override
	public int getSerializedSize() {
		return super.getSerializedSize() + SERIALIZED_SIZE;
	}
}
