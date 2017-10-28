package model;

import java.util.ArrayList;

import model.abilities.Ability;

public class Player extends RoundEntity {
	private int health;
	private int maxHealth;
	private ArrayList<Ability> abilities;
	
	public boolean isAlive() {
		return this.health > 0;
	}
}
