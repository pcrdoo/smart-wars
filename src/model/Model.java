package model;

import java.util.ArrayList;

import main.Constants;
import view.Updatable;

public class Model {
	
	private Player leftPlayer;
	private Player rightPlayer;
	private ArrayList<Updatable> updatables;
	
	public Model() {
		this.leftPlayer = new Player(PlayerSide.LEFT_PLAYER);
		this.rightPlayer = new Player(PlayerSide.RIGHT_PLAYER);
		this.updatables = new ArrayList<Updatable>();
		this.updatables.add(leftPlayer);
		this.updatables.add(rightPlayer);
	}
	
	public Player getLeftPlayer() {
		return leftPlayer;
	}

	public Player getRightPlayer() {
		return rightPlayer;
	}
	
	// zasto se ovde referencira Updatble iz v iew?!TODO
	
	public void update(double dt) {
		for (Updatable updatable : updatables) {
			updatable.update(dt);
		}
	}
	
	public void addUpdatable(Updatable updatable) {
		this.updatables.add(updatable);
	}
	
	public void removeUpdatable(Updatable updatable) {
		this.updatables.remove(updatable);
	}
}
