package multiplayer.messages;

import java.nio.ByteBuffer;

import model.Model;
import model.entitites.Entity;
import model.entitites.EntityType;
import multiplayer.SerializationHelpers;

public abstract class EntityMessage implements Message {
	private final static int MESSAGE_SIZE = 1;
	
	protected Entity entity;
	
	public EntityMessage(Entity entity) {
		this.entity = entity;
	}
	
	public EntityMessage() {
		
	}
	
	public EntityMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}
	
	@Override
	public void serializeTo(ByteBuffer buffer) {
		buffer.put((byte)EntityType.fromEntity(entity).getNum());
		entity.serializeTo(buffer);
	}
	
	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		EntityType type = EntityType.fromNum(buffer.get());
		entity = SerializationHelpers.deserializeEntity(model, type, buffer, null);
	}
	
	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE + entity.getSerializedSize();
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	
}
