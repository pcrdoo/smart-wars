package model;

import java.util.ArrayList;

import main.Constants;
import model.abilities.Ability;
import model.abilities.Shoot;
import util.Vector2D;

public class Player extends RoundEntity {
	private int health;
	private int maxHealth;
	private double speed;
	private ArrayList<Ability> abilities;
	
	public Player(Vector2D startPosition) {
		super(startPosition, Constants.PLAYER_RADIUS);
		this.health = this.maxHealth = Constants.PLAYER_HEALTH;
		this.speed = Constants.PLAYER_SPEED;
		this.abilities = new ArrayList<Ability>();
		this.abilities.add(new Shoot());
		
		System.out.println(this.position.getdX());
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
	
	public void stop() {
		this.setVelocity(new Vector2D(0, 0));
	}
}
