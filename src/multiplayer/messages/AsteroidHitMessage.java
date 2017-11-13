package multiplayer.messages;

import java.nio.ByteBuffer;
import java.util.UUID;

import model.Asteroid;
import model.Model;
import model.entitites.Entity;
import model.entitites.EntityType;
import multiplayer.SerializationHelpers;
import util.Vector2D;

public class AsteroidHitMessage implements Message {
	private final static int MESSAGE_SIZE = 16 + Vector2D.getSerializedSize();
	
	private Asteroid asteroid;
	private Vector2D position;
	
	public AsteroidHitMessage(Asteroid asteroid, Vector2D position) {
		this.asteroid = asteroid;
		this.position = position;
	}
	
	public AsteroidHitMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}
	
	public AsteroidHitMessage() {
		
	}
	
	@Override
	public void serializeTo(ByteBuffer buffer) {
		SerializationHelpers.serializeUuid(buffer, asteroid.getUuid());
		position.serializeTo(buffer);
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		UUID uuid = SerializationHelpers.deserializeUuid(buffer);
		Entity e = model.getEntityById(uuid);
		if (e == null) {
			System.err.println("Warning: Entity " + uuid.toString() + " sent as a hit asteroid doesn't exist");
			return;
		}
		
		asteroid = (Asteroid) e;
		if (asteroid == null) {
			System.err.println("Warning: Entity " + uuid.toString() + " sent as a hit asteroid is of type " + EntityType.fromEntity(e));
		}
		
		position = Vector2D.deserializeFrom(buffer);
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE;
	}

	@Override
	public MessageType getType() {
		return MessageType.VIEW_BULLET_ASTEROID_HIT;
	}

	public Asteroid getAsteroid() {
		return asteroid;
	}

	public void setAsteroid(Asteroid asteroid) {
		this.asteroid = asteroid;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}
}
