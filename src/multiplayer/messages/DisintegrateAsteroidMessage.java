package multiplayer.messages;

import java.nio.ByteBuffer;
import java.util.UUID;

import model.Asteroid;
import model.Model;
import model.entitites.Entity;
import model.entitites.EntityType;
import multiplayer.SerializationHelpers;

public class DisintegrateAsteroidMessage implements Message {
	private final static int MESSAGE_SIZE = 16;
	private Asteroid asteroid;
	
	public DisintegrateAsteroidMessage(Asteroid asteroid) {
		this.asteroid = asteroid;
	}

	public DisintegrateAsteroidMessage() {
		
	}
	
	public DisintegrateAsteroidMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}
	
	@Override
	public void serializeTo(ByteBuffer buffer) {
		SerializationHelpers.serializeUuid(buffer, asteroid.getUuid());
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		UUID uuid = SerializationHelpers.deserializeUuid(buffer);
		Entity e = model.getEntityById(uuid);
		if (e == null) {
			System.err.println("Warning: Entity " + uuid.toString() + " sent as asteroid for disintegration doesn't exist");
			return;
		}
		
		asteroid = (Asteroid) e;
		if (asteroid == null) {
			System.err.println("Warning: Entity " + uuid.toString() + " sent as asteroid for disintegration is of type " + EntityType.fromEntity(e));
		}
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE;
	}

	@Override
	public MessageType getType() {
		return MessageType.VIEW_DISINTEGRATE_ASTEROID;
	}

	public Asteroid getAsteroid() {
		return asteroid;
	}

	public void setAsteroid(Asteroid asteroid) {
		this.asteroid = asteroid;
	}
}
