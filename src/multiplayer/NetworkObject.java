package multiplayer;

import java.nio.ByteBuffer;
import java.util.UUID;

import model.Model;

// Anything that can be sent over network
public class NetworkObject {

	protected UUID uuid;

	public NetworkObject() {
		this.uuid = UUID.randomUUID();
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public void serializeTo(ByteBuffer buffer) {
		SerializationHelpers.serializeUuid(buffer, uuid);
	}
	
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		uuid = SerializationHelpers.deserializeUuid(buffer);
	}
}
