package multiplayer.messages;

import java.nio.ByteBuffer;
import java.util.UUID;

import model.Model;
import model.entitites.Entity;
import model.entitites.EntityType;
import multiplayer.SerializationHelpers;

public class UpdateEntityMessage implements Message {
	private final static int SERIALIZED_SIZE = 16 + 4;
	private UUID uuid;
	private byte[] serializedEntity;

	public UpdateEntityMessage(UUID uuid, Entity entity) {
		this.uuid = uuid;
		serializedEntity = new byte[entity.getSerializedSize()];
		entity.serializeTo(ByteBuffer.wrap(serializedEntity));
	}

	public UpdateEntityMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}

	@Override
	public MessageType getType() {
		return MessageType.ENTITY_UPDATED;
	}

	@Override
	public void serializeTo(ByteBuffer buffer) {
		SerializationHelpers.serializeUuid(buffer, uuid);
		buffer.putInt(serializedEntity.length);
		buffer.put(serializedEntity);
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		uuid = SerializationHelpers.deserializeUuid(buffer);
		int length = buffer.getInt();
		serializedEntity = new byte[length];
		buffer.get(serializedEntity, 0, length);
	}

	public void apply(Model model) {
		Entity e = model.getEntityById(uuid);
		if (e != null) {
			EntityType type = EntityType.fromEntity(e);
			SerializationHelpers.deserializeEntity(model, type, ByteBuffer.wrap(serializedEntity), e);
		} else {
			System.err.println("Warning: Could not find entity " + uuid.toString() + " to apply update");
		}
	}

	@Override
	public int getSerializedSize() {
		return SERIALIZED_SIZE + serializedEntity.length;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public byte[] getSerializedEntity() {
		return serializedEntity;
	}

	public void setSerializedEntity(byte[] serializedEntity) {
		this.serializedEntity = serializedEntity;
	}
}
