package model.entitites;

import model.Asteroid;
import model.Bullet;
import model.Mirror;
import model.Player;
import model.Wormhole;

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
	
	public static EntityType fromEntity(Entity e) {
		if (e instanceof Player) {
			return PLAYER;
		} else if (e instanceof Bullet) {
			return BULLET;
		} else if (e instanceof Asteroid) {
			return ASTEROID;
		} else if (e instanceof Mirror) {
			return MIRROR;
		} else if (e instanceof Wormhole) {
			return WORMHOLE;
		}
		
		return null;
	}
}
