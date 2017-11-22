package multiplayer.messages;

public enum MessageType {
	CLIENT_HELLO(0x01),
	SIDE_ASSIGNMENT(0x02),
	PLAYER_CONTROL(0x03),
	NEW_GAME_STARTING(0x04),
	GAME_OVER(0x05),
	ENTITY_REMOVED(0x06),
	ENTITY_UPDATED(0x07),
	ENTITY_ADDED(0x08),
	POSITION_SYNC(0x09),
	VIEW_DISINTEGRATE_ASTEROID(0x0A),
	VIEW_PLAYER_HIT(0x0B),
	VIEW_MIRROR_BOUNCE(0x0C),
	VIEW_BULLET_ASTEROID_HIT(0x0D);
	
	private int num;
	
	MessageType(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
	
	public static MessageType fromNum(int num) {
		switch (num) {
		case 0x01: return CLIENT_HELLO;
		case 0x02: return SIDE_ASSIGNMENT;
		case 0x03: return PLAYER_CONTROL;
		case 0x04: return NEW_GAME_STARTING;
		case 0x05: return GAME_OVER;
		case 0x06: return ENTITY_REMOVED;
		case 0x07: return ENTITY_UPDATED;
		case 0x08: return ENTITY_ADDED;
		case 0x09: return POSITION_SYNC;
		case 0x0A: return VIEW_DISINTEGRATE_ASTEROID;
		case 0x0B: return VIEW_PLAYER_HIT;
		case 0x0C: return VIEW_MIRROR_BOUNCE;
		case 0x0D: return VIEW_BULLET_ASTEROID_HIT;
		default:
			System.err.println("Warning: Unknown message type " + num);
			return null;
		}
	}
}
