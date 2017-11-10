package model;

import java.util.ArrayList;

import main.Constants;
import model.abilities.Ability;
import model.abilities.MirrorMagic;
import model.entitites.RoundEntity;
import model.abilities.Gun;
import util.Vector2D;
import view.Updatable;

public class Player extends RoundEntity {
	private PlayerSide playerSide;
	private int maxHealth;
	private int currHealth;
	private double speed; // pixels per second

	private ArrayList<Ability> abilities;
	private Gun gun;
	private MirrorMagic shortMirrorMagic;
	private MirrorMagic longMirrorMagic;

	public Player(PlayerSide playerSide) {
		super(playerSide == PlayerSide.LEFT_PLAYER ? Constants.LEFT_PLAYER_START_POS : Constants.RIGHT_PLAYER_START_POS,
				Constants.PLAYER_RADIUS);
		this.playerSide = playerSide;
		currHealth = maxHealth = Constants.PLAYER_HEALTH;
		speed = Constants.PLAYER_SPEED;
		gun = new Gun(this);
		shortMirrorMagic = new MirrorMagic(this, Constants.SHORT_MIRROR_LENGTH);
		longMirrorMagic = new MirrorMagic(this, Constants.LONG_MIRROR_LENGTH);
		abilities = new ArrayList<Ability>();
		abilities.add(gun);
	}

	public void moveUp() {
		setVelocity(new Vector2D(0, -speed));
	}

	public void moveDown() {
		setVelocity(new Vector2D(0, speed));
	}

	public void stopMoving() {
		setVelocity(new Vector2D(0, 0));
	}

	public Bullet fireBullet() {
		return gun.fireBullet(position,
				this.playerSide == PlayerSide.LEFT_PLAYER ? new Vector2D(Constants.BULLET_SPEED, 0)
						: new Vector2D(-Constants.BULLET_SPEED, 0));
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		for (Ability ability : abilities) {
			ability.update(dt);
		}
		position.clampdY(20, Constants.WINDOW_HEIGHT - 20);
	}

	public PlayerSide getPlayerSide() {
		return playerSide;
	}

	public boolean isAlive() {
		return currHealth > 0;
	}

	public void receiveDamage(double damage) {
		currHealth -= damage;
		if (currHealth < 0) {
			currHealth = 0;
		}
	}

	public int getCurrHealth() {
		return currHealth;
	}

	@Override
	public boolean shouldCull() {
		return false;
	}
	
	public void doMirrorMagic(MirrorMagic mirrorMagic) {
		
	}

	public MirrorMagic getShortMirrorMagic() {
		return shortMirrorMagic;
	}

	public MirrorMagic getLongMirrorMagic() {
		return longMirrorMagic;
	}
}
