package model;

import java.util.ArrayList;

public class Model {

	private Player leftPlayer;
	private Player rightPlayer;
	private ArrayList<Entity> entities;
	private ArrayList<Bullet> bullets; // separate bullets for culling
	private ArrayList<Asteroid> asteroids; // separate asteroids for culling

	private GameState gameState;

	public Model() {
		gameState = GameState.RUNNING;
		leftPlayer = new Player(PlayerSide.LEFT_PLAYER);
		rightPlayer = new Player(PlayerSide.RIGHT_PLAYER);
		bullets = new ArrayList<>();
		asteroids = new ArrayList<>();

		entities = new ArrayList<Entity>();
		entities.add(leftPlayer);
		entities.add(rightPlayer);
	}

	public Player getLeftPlayer() {
		return leftPlayer;
	}

	public Player getRightPlayer() {
		return rightPlayer;
	}

	public void update(double dt) {
		if (gameState == GameState.RUNNING) {
			for (Entity entity : entities) {
				entity.update(dt);
			}
		}
	}

	public void addEntity(Entity entity) {
		entities.add(entity);
	}

	public void removeEntity(Entity entity) {
		entities.remove(entity);
	}

	public void addBullet(Bullet bullet) {
		bullets.add(bullet);
	}

	public void removeBullet(Bullet bullet) {
		bullets.remove(bullet);
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}
	
	public void addAsteroid(Asteroid asteroid) {
		asteroids.add(asteroid);
	}

	public void removeAsteroid(Asteroid asteroid) {
		asteroids.remove(asteroid);
	}

	public ArrayList<Asteroid> getAsteroids() {
		return asteroids;
	}
	
	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public GameState getGameState() {
		return gameState;
	}
}
