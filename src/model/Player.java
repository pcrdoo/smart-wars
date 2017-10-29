package model;

import java.util.ArrayList;

import main.Constants;
import model.abilities.Ability;
import model.abilities.Shoot;
import util.Vector2D;
import view.Updatable;

public class Player extends RoundEntity {
	private int health;
	private int maxHealth;
	private double speed;
	
	private ArrayList<Updatable> updatables;
	private Shoot shoot;
	
	public Player(Vector2D startPosition) {
		super(startPosition, Constants.PLAYER_RADIUS);
		this.health = this.maxHealth = Constants.PLAYER_HEALTH;
		this.speed = Constants.PLAYER_SPEED;
		this.shoot = new Shoot();
		this.updatables = new ArrayList<Updatable>();
		this.updatables.add(shoot);
	}
	
	public boolean isAlive() {
		return this.health > 0;
	}

	public void moveLeft() {
		this.setVelocity(new Vector2D(-speed, 0));
	}

	public void moveRight() {
		this.setVelocity(new Vector2D(+speed, 0));
	}
	
	public void stopMoving() {
		this.setVelocity(new Vector2D(0, 0));
	}

	public Bullet shoot() {
		return this.shoot.fire(this.position);
	}
	
	public void update(double dt) {
		super.update(dt);
		for (Updatable updatable : updatables) {
			updatable.update(dt);
		}
	}
}
