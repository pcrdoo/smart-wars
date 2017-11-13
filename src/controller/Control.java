package controller;

public enum Control {
	MOVE_UP(0x01),
	MOVE_DOWN(0x02),
	START_GUN(0x03),
	STOP_GUN(0x04),
	SHORT_MIRROR_MAGIC(0x05),
	LONG_MIRROR_MAGIC(0x06),
	STOP(0x07),
	NEW_GAME(0x08);
	
	private int num;
	
	Control(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
	
	public static Control fromNum(int num) {
		switch(num) {
		case 0x01: return MOVE_UP;
		case 0x02: return MOVE_DOWN;
		case 0x03: return START_GUN;
		case 0x04: return STOP_GUN;
		case 0x05: return SHORT_MIRROR_MAGIC;
		case 0x06: return LONG_MIRROR_MAGIC;
		case 0x07: return STOP;
		case 0x08: return NEW_GAME;
		}
		
		return null;
	}
}
