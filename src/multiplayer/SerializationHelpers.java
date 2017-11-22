package multiplayer;

import java.nio.ByteBuffer;
import java.util.UUID;

import memory.Pools;
import model.Mirror;
import model.Model;
import model.Player;
import model.PlayerSide;
import model.Wormhole;
import model.entitites.Entity;
import model.entitites.EntityType;

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

	public static Entity deserializeEntity(Model model, EntityType type, ByteBuffer buffer, Entity destination) {
		Entity e;
		if (destination == null) {
			switch (type) {
			case PLAYER:
				e = new Player(PlayerSide.LEFT_PLAYER, null);
				break;
			case ASTEROID:
				e = Pools.ASTEROID.createEmpty();
				break;
			case BULLET:
				e = Pools.BULLET.createEmpty();
				break;
			case MIRROR:
				e = new Mirror(null, null, null, 0.0, false);
				break;
			case WORMHOLE:
				e = new Wormhole(null);
				break;
			default:
				System.err.println("Unknown entity type: " + type.toString());
				return null;
			}
		} else {
			e = destination;

			EntityType destType = EntityType.fromEntity(e);
			if (destType != type) {
				System.err.println("Entity type " + type.toString() + " requested, but entity of type "
						+ destType.toString() + " provided as destination");
				return null;
			}
		}

		e.deserializeFrom(model, buffer);
		return e;
	}
}
