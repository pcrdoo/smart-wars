package model;

import java.util.ArrayList;

import main.Constants;
import model.abilities.Ability;
import model.abilities.Shoot;
import util.Vector2D;
import view.Updatable;

public class Player extends RoundEntity {
	private PlayerSide playerSide;
	private int health;
	private int maxHealth;
	private double speed;

	private ArrayList<Updatable> updatables;
	private Shoot shoot;

	public Player(PlayerSide playerSide) {
		super(playerSide == PlayerSide.LEFT_PLAYER ? Constants.LEFT_PLAYER_START_POS : Constants.RIGHT_PLAYER_START_POS,
				Constants.PLAYER_RADIUS);
		this.playerSide = playerSide;
		this.health = this.maxHealth = Constants.PLAYER_HEALTH;
		this.speed = Constants.PLAYER_SPEED;
		this.shoot = new Shoot(this);
		this.updatables = new ArrayList<Updatable>();
		this.updatables.add(shoot);
	}

	public boolean isAlive() {
		return this.health > 0;
	}


	public void moveUp() {
		this.setVelocity(new Vector2D(0, -speed));
	}

	public void moveDown() {
		this.setVelocity(new Vector2D(0, +speed));
	}

	public void stopMoving() {
		this.setVelocity(new Vector2D(0, 0));
	}

	public Bullet shoot() {
		return this.shoot.fire(this.position,
				this.playerSide == PlayerSide.LEFT_PLAYER ? new Vector2D(Constants.BULLET_SPEED, 0)
						: new Vector2D(-Constants.BULLET_SPEED, 0));
	}

	public void update(double dt) {
		super.update(dt);
		for (Updatable updatable : updatables) {
			updatable.update(dt);
		}
	}
	
	public PlayerSide getPlayerSide() {
		return playerSide;
	}
}
