package multiplayer.messages;

import java.nio.ByteBuffer;
import java.util.UUID;

import model.Model;
import model.entitites.Entity;
import multiplayer.SerializationHelpers;

public class RemoveEntityMessage implements Message {
	private final static int MESSAGE_SIZE = 16;

	private UUID uuid;
	private Entity entity;

	public RemoveEntityMessage(Entity entity) {
		this.entity = entity;
		uuid = entity.getUuid();
	}

	public RemoveEntityMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}

	public RemoveEntityMessage() {
	}

	@Override
	public MessageType getType() {
		return MessageType.ENTITY_REMOVED;
	}

	@Override
	public void serializeTo(ByteBuffer buffer) {
		SerializationHelpers.serializeUuid(buffer, uuid);
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		UUID uuid = SerializationHelpers.deserializeUuid(buffer);
		entity = model.getEntityById(uuid);
		if (entity == null) {
			System.err.println(
					"Warning: Could not find entity with id " + uuid + " for deletion; memory leaks may occur!");
		}
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}
