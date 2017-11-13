package multiplayer;

import java.nio.ByteBuffer;
import java.util.UUID;

public class SerializationHelpers {
	public static void serializeUuid(ByteBuffer buffer, UUID uuid) {
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
	}
	
	public static UUID deserializeUuid(ByteBuffer buffer) {
		long ms = buffer.getLong();
		long ls = buffer.getLong();
		return new UUID(ms, ls);
	}
}
