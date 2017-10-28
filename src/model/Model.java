package model;

import main.Constants;

public class Model {
	
	private Player topPlayer;
	private Player bottomPlayer;
	
	public Model() {
		this.topPlayer = new Player(Constants.TOP_PLAYER_START_POS);
		this.bottomPlayer = new Player(Constants.BOTTOM_PLAYER_START_POS);
	}
	
	public Player getTopPlayer() {
		return topPlayer;
	}

	public Player getBottomPlayer() {
		return bottomPlayer;
	}
	
	public void update(double dt) {
		topPlayer.update(dt);
		bottomPlayer.update(dt);
	}
}
