package model;

public enum MirrorState {
	TRAVELLING(0x01),
	SPINNING(0x02),
	STABLE(0x03);
	
	private int num;
	
	MirrorState(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
}
