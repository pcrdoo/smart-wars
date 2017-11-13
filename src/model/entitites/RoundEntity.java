package model.entitites;

import java.awt.Rectangle;
import java.nio.ByteBuffer;

import model.Model;
import util.Vector2D;

public abstract class RoundEntity extends Entity {
	private final static int SERIALIZED_SIZE = 4;

	protected double radius;

	protected RoundEntity(Vector2D position, double radius) {
		super(position);
		this.radius = radius;
	}

	public boolean hitTest(Vector2D point) {
		return position.sub(point).length() < radius;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle((int) (position.getX() - radius), (int) (position.getY() - radius), (int) (2 * radius),
				(int) (2 * radius));
	}

	@Override
	public void serializeTo(ByteBuffer buffer) {
		super.serializeTo(buffer);
		buffer.putFloat((float) radius);
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		super.deserializeFrom(model, buffer);
		radius = buffer.getFloat();
	}

	@Override
	public int getSerializedSize() {
		return super.getSerializedSize() + SERIALIZED_SIZE;
	}
}
