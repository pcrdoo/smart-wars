package model;

public enum PlayerSide {
	LEFT_PLAYER(0x01), RIGHT_PLAYER(0x02);

	private int num;

	PlayerSide(int num) {
		this.num = num;
	}

	public int getNum() {
		return num;
	}
}
