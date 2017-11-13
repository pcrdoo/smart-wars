package multiplayer.messages;

import java.nio.ByteBuffer;

import model.Model;
import model.entitites.Entity;

public class AddEntityMessage extends EntityMessage implements Message {
	public AddEntityMessage(Entity entity) {
		super(entity);
	}

	public AddEntityMessage(Model model, ByteBuffer buffer) {
		super(model, buffer);
	}

	public AddEntityMessage() {
		super();
	}

	@Override
	public MessageType getType() {
		return MessageType.ENTITY_ADDED;
	}
}
