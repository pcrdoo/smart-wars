package multiplayer.messages;

import java.nio.ByteBuffer;
import java.util.UUID;

import model.Model;
import multiplayer.SerializationHelpers;
import util.Vector2D;

public class UuidPosition implements Message {
	public final static int MESSAGE_SIZE = 16 + Vector2D.getSerializedSize();
	
	private UUID uuid;
	private Vector2D position;
	
	public UuidPosition(UUID uuid, Vector2D position) {
		this.uuid = uuid;
		this.position = position;
	}
	
	public UuidPosition() {
		
	}
	
	public UuidPosition(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}
	
	@Override
	public void serializeTo(ByteBuffer buffer) {
		SerializationHelpers.serializeUuid(buffer, uuid);
		position.serializeTo(buffer);
		
	}
	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		uuid = SerializationHelpers.deserializeUuid(buffer);
		position = Vector2D.deserializeFrom(buffer);
		
	}
	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE;
	}
	
	@Override
	public MessageType getType() {
		return null;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}
}
