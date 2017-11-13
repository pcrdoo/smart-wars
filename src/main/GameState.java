package main;

public enum GameState {
	RUNNING(0x01),
	LEFT_WIN(0x02),
	RIGHT_WIN(0x03),
	DRAW(0x04);
	
	private int num;
	
	GameState(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
	
	public static GameState fromNum(int num) {
		switch(num) {
		case 0x01: return RUNNING;
		case 0x02: return LEFT_WIN;
		case 0x03: return RIGHT_WIN;
		case 0x04: return DRAW;
		}
		
		return null;
	}
}
