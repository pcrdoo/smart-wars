package model;

import java.util.ArrayList;

import model.abilities.Ability;

public class Player extends RoundEntity {
	private int health;
	private int maxHealth;
	private double speed;
	private ArrayList<Ability> abilities;
	
	public Player() {
		this.health = this.maxHealth = 0;// todo
	}
	
	public boolean isAlive() {
		return this.health > 0;
	}
}
