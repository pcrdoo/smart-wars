package model;

import java.util.ArrayList;

import main.Constants;
import view.Updatable;

public class Model {
	
	private Player topPlayer;
	private Player bottomPlayer;
	private ArrayList<Updatable> updatables;
	
	public Model() {
		this.topPlayer = new Player(Constants.TOP_PLAYER_START_POS);
		this.bottomPlayer = new Player(Constants.BOTTOM_PLAYER_START_POS);
		this.updatables = new ArrayList<Updatable>();
		this.updatables.add(topPlayer);
		this.updatables.add(bottomPlayer);
	}
	
	public Player getTopPlayer() {
		return topPlayer;
	}

	public Player getBottomPlayer() {
		return bottomPlayer;
	}
	
	public void update(double dt) {
		for (Updatable updatable : updatables) {
			updatable.update(dt);
		}
	}
	
	public void addUpdatable(Updatable updatable) {
		this.updatables.add(updatable);
	}
}
