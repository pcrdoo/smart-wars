package multiplayer.messages;

import java.nio.ByteBuffer;
import java.util.UUID;

import model.Mirror;
import model.Model;
import model.entitites.Entity;
import model.entitites.EntityType;
import multiplayer.SerializationHelpers;
import util.Vector2D;

public class MirrorBounceMessage implements Message {
	private final static int MESSAGE_SIZE = 16 + Vector2D.getSerializedSize();

	private Mirror mirror;
	private Vector2D position;

	public MirrorBounceMessage(Mirror mirror, Vector2D position) {
		this.mirror = mirror;
		this.position = position;
	}

	public MirrorBounceMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}

	public MirrorBounceMessage() {

	}

	@Override
	public void serializeTo(ByteBuffer buffer) {
		SerializationHelpers.serializeUuid(buffer, mirror.getUuid());
		position.serializeTo(buffer);
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		UUID uuid = SerializationHelpers.deserializeUuid(buffer);
		Entity e = model.getEntityById(uuid);
		if (e == null) {
			System.err.println(
					"Warning: Entity " + uuid.toString() + " sent as a mirror in a bounce event doesn't exist");
			return;
		}

		mirror = (Mirror) e;
		if (mirror == null) {
			System.err.println("Warning: Entity " + uuid.toString() + " sent as a mirror in a bounce event is of type "
					+ EntityType.fromEntity(e));
		}

		position = Vector2D.deserializeFrom(buffer);
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE;
	}

	@Override
	public MessageType getType() {
		return MessageType.VIEW_MIRROR_BOUNCE;
	}

	public Mirror getMirror() {
		return mirror;
	}

	public void setMirror(Mirror mirror) {
		this.mirror = mirror;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}
}
