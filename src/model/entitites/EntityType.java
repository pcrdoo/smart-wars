package model.entitites;

public enum EntityType {
	PLAYER(0x01),
	BULLET(0x02),
	ASTEROID(0x03),
	MIRROR(0x04),
	WORMHOLE(0x05);
	
	private int num;
	
	EntityType(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
	
	public static EntityType fromNum(int num) {
		switch(num) {
		case 0x01: return PLAYER;
		case 0x02: return BULLET;
		case 0x03: return ASTEROID;
		case 0x04: return MIRROR;
		case 0x05: return WORMHOLE;
		}
		
		return null;
	}
}
