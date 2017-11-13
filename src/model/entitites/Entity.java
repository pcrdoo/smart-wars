package model.entitites;

import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import model.Model;
import multiplayer.NetworkObject;
import multiplayer.OpenPipes;
import multiplayer.messages.Message;
import multiplayer.messages.MessageType;
import util.Vector2D;

public abstract class Entity extends NetworkObject {
	private final static int SERIALIZED_SIZE = 2 * Vector2D.getSerializedSize();
	
	protected Vector2D position;
	protected Vector2D velocity; // pixels per second

	public Entity(Vector2D position) {
		super();
		this.position = position;
		this.velocity = new Vector2D(0, 0);
	}

	public Entity(Vector2D position, Vector2D velocity) {
		super();
		this.position = position;
		this.velocity = velocity;
	}

	public void update(double dt) {
		position = position.add(velocity.scale(dt));
	}

	public abstract boolean hitTest(Vector2D point);
	
	public abstract Rectangle getBoundingBox();

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}
	
	public abstract boolean shouldCull();
	
	@Override
	public void serializeTo(ByteBuffer buffer) {
		super.serializeTo(buffer);
		position.serializeTo(buffer);
		velocity.serializeTo(buffer);
	}
	
	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		super.deserializeFrom(model, buffer);
		position = Vector2D.deserializeFrom(buffer);
		velocity = Vector2D.deserializeFrom(buffer);
	}
	
	@Override
	public int getSerializedSize() {
		return super.getSerializedSize() + SERIALIZED_SIZE;
	}
	
}
