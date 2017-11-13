package multiplayer.messages;

public enum MessageType {
	SIDE_ASSIGNMENT(0x01),
	PLAYER_CONTROL(0x02),
	NEW_GAME_STARTING(0x03),
	GAME_OVER(0x04),
	ENTITY_REMOVED(0x05),
	ENTITY_UPDATED(0x06),
	ENTITY_ADDED(0x07),
	POSITION_SYNC(0x08),
	VIEW_DISINTEGRATE_ASTEROID(0x09),
	VIEW_PLAYER_HIT(0x0A),
	VIEW_MIRROR_BOUNCE(0x0B),
	VIEW_BULLET_ASTEROID_HIT(0x0C);
	
	private int num;
	
	MessageType(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
	
	public static MessageType fromNum(int num) {
	switch (num) {
	case 0x01: return SIDE_ASSIGNMENT;
	case 0x02: return PLAYER_CONTROL;
	case 0x03: return NEW_GAME_STARTING;
	case 0x04: return GAME_OVER;
	case 0x05: return ENTITY_REMOVED;
	case 0x06: return ENTITY_UPDATED;
	case 0x07: return ENTITY_ADDED;
	case 0x08: return POSITION_SYNC;
	case 0x09: return VIEW_DISINTEGRATE_ASTEROID;
	case 0x0A: return VIEW_PLAYER_HIT;
	case 0x0B: return VIEW_MIRROR_BOUNCE;
	case 0x0C: return VIEW_BULLET_ASTEROID_HIT;
	default:
		System.err.println("Warning: Unknown message type " + num);
		return null;
	}
	}
}
