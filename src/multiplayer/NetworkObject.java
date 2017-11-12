package multiplayer;

import java.util.UUID;

// Anything that can be sent over network
public class NetworkObject {

	protected UUID uuid;

	public NetworkObject() {
		this.uuid = UUID.randomUUID();
	}
	
	public UUID getUuid() {
		return uuid;
	}

}
