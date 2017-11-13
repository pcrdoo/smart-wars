package multiplayer.messages;

import java.nio.ByteBuffer;
import java.util.UUID;

import model.Model;
import model.Player;
import model.entitites.Entity;
import model.entitites.EntityType;
import multiplayer.SerializationHelpers;

public class PlayerHitMessage implements Message {
	private final static int MESSAGE_SIZE = 16;
	
	private Player player;
	
	public PlayerHitMessage(Player player) {
		this.player = player;
	}
	
	public PlayerHitMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}
	
	public PlayerHitMessage() {
		
	}
	
	@Override
	public void serializeTo(ByteBuffer buffer) {
		SerializationHelpers.serializeUuid(buffer, player.getUuid());
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		UUID uuid = SerializationHelpers.deserializeUuid(buffer);
		Entity e = model.getEntityById(uuid);

		if (e == null) {
			System.err.println("Warning: Entity " + uuid.toString() + " sent as a hit player doesn't exist");
			return;
		}
		
		player = (Player) e;
		if (player == null) {
			System.err.println("Warning: Entity " + uuid.toString() + " sent as a hit player is of type " + EntityType.fromEntity(e));
		}
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE;
	}

	@Override
	public MessageType getType() {
		return MessageType.VIEW_PLAYER_HIT;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
