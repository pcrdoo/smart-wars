package model;

import java.nio.ByteBuffer;

import main.Constants;
import model.entitites.RoundEntity;
import util.Vector2D;

public class Wormhole extends RoundEntity {
	private final static int SERIALIZED_SIZE = 8;

	private double timeToLive;

	public Wormhole(Vector2D position) {
		super(position, Constants.WORMHOLE_RADIUS);
		timeToLive = Constants.WORMHOLE_LIFETIME;
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		timeToLive -= dt;
		if (timeToLive < 0) {
			timeToLive = 0;
		}
	}

	public boolean isDead() {
		return timeToLive == 0;
	}

	public double getTimeRemaining() {
		return timeToLive;
	}

	@Override
	public boolean shouldCull() {
		return false;
	}

	@Override
	public void serializeTo(ByteBuffer buffer) {
		super.serializeTo(buffer);
		buffer.putDouble(timeToLive);
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		super.deserializeFrom(model, buffer);
		timeToLive = buffer.getDouble();
	}

	@Override
	public int getSerializedSize() {
		return super.getSerializedSize() + SERIALIZED_SIZE;
	}
}
