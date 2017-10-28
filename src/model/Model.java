package model;

public class Model {
	private Player topPlayer;
	private Player bottomPlayer;
	
	public Model() {
		this.topPlayer = new Player();
		this.bottomPlayer = new Player();
	}

	public Player getTopPlayer() {
		return topPlayer;
	}

	public Player getBottomPlayer() {
		return bottomPlayer;
	}
}
