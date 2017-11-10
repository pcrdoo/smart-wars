package controller;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import main.Constants;
import main.GameStarter;
import model.Asteroid;
import model.Bullet;
import model.Entity;
import model.GameState;
import model.Model;
import model.Player;
import util.Vector2D;
import view.AsteroidView;
import view.BulletView;
import view.EntityView;
import view.MainView;
import view.PlayerView;

public class MainController {

	private Model model;
	private MainView view;
	private GameStarter gameStarter;
	private HashMap<Controls, Integer> leftPlayerControls;
	private HashMap<Controls, Integer> rightPlayerControls;
	private HashMap<Integer, Boolean> keyboardState;

	// View-related dependencies for each entity.
	private HashMap<Entity, EntityView> viewMap;

	private double timeToNextAsteroidSpawn;
	private Random asteroidRandom;

	public MainController(GameStarter gameStarter, MainView view, Model model) {
		this.view = view;
		this.model = model;
		this.gameStarter = gameStarter;

		viewMap = new HashMap<>();

		PlayerView leftPlayerView = new PlayerView(model.getLeftPlayer());
		view.addDrawable(leftPlayerView);
		view.addUpdatable(leftPlayerView);
		leftPlayerControls = new HashMap<Controls, Integer>();
		leftPlayerControls.put(Controls.MOVE_UP, KeyEvent.VK_W);
		leftPlayerControls.put(Controls.MOVE_DOWN, KeyEvent.VK_S);
		leftPlayerControls.put(Controls.FIRE_GUN, KeyEvent.VK_D);

		PlayerView rightPlayerView = new PlayerView(model.getRightPlayer());
		view.addDrawable(rightPlayerView);
		view.addUpdatable(rightPlayerView);
		rightPlayerControls = new HashMap<Controls, Integer>();
		rightPlayerControls.put(Controls.MOVE_UP, KeyEvent.VK_I);
		rightPlayerControls.put(Controls.MOVE_DOWN, KeyEvent.VK_K);
		rightPlayerControls.put(Controls.FIRE_GUN, KeyEvent.VK_J);

		initKeyboardState();

		viewMap.put(model.getLeftPlayer(), leftPlayerView);
		viewMap.put(model.getRightPlayer(), rightPlayerView);

		timeToNextAsteroidSpawn = 1;
		asteroidRandom = new Random();
	}

	private void initKeyboardState() {
		keyboardState = new HashMap<Integer, Boolean>();
		for (HashMap.Entry<Controls, Integer> entry : leftPlayerControls.entrySet()) {
			keyboardState.put((Integer) entry.getValue(), false);
		}
		for (HashMap.Entry<Controls, Integer> entry : rightPlayerControls.entrySet()) {
			keyboardState.put((Integer) entry.getValue(), false);
		}
		keyboardState.put(KeyEvent.VK_ENTER, false);
	}

	private void deleteView(Entity entity) {
		EntityView entityView = viewMap.get(entity);
		if (entityView != null) {
			view.removeUpdatable(entityView);
			view.removeDrawable(entityView);
			viewMap.remove(entity);
		}
	}

	private void cullBullets(ArrayList<Bullet> toCull) {
		for (Bullet bullet : toCull) {
			model.removeEntity(bullet);
			model.removeBullet(bullet);
			deleteView(bullet);
		}
	}

	private void checkBulletCollisions() {
		ArrayList<Bullet> impactedBullets = new ArrayList<>();
		for (Bullet bullet : model.getBullets()) {
			if (bullet.getOwner() == model.getRightPlayer() && model.getLeftPlayer().hitTest(bullet.getPosition())) {
				handlePlayerHit(model.getLeftPlayer(), bullet);
				impactedBullets.add(bullet);
			} else if (bullet.getOwner() == model.getLeftPlayer()
					&& model.getRightPlayer().hitTest(bullet.getPosition())) {
				handlePlayerHit(model.getRightPlayer(), bullet);
				impactedBullets.add(bullet);
			} else {
				// Asteroid collision
				for (Asteroid asteroid : model.getAsteroids()) {
					if (asteroid.hitTest(bullet.getPosition())) {
						handleAsteroidHit(asteroid, bullet);
						impactedBullets.add(bullet);
					}
				}
			}
		}
		cullBullets(impactedBullets);
	}

	private void checkPlayerControls(Player player, HashMap<Controls, Integer> controls) {
		if (keyboardState.get(controls.get(Controls.FIRE_GUN))) {
			fireBullet(player);
		}
		if (keyboardState.get(controls.get(Controls.MOVE_UP))) {
			player.moveUp();
		}
		if (keyboardState.get(controls.get(Controls.MOVE_DOWN))) {
			player.moveDown();
		}
		if (keyboardState.get(controls.get(Controls.MOVE_UP)) && keyboardState.get(controls.get(Controls.MOVE_DOWN))) {
			player.stopMoving();
		}
		if (!keyboardState.get(controls.get(Controls.MOVE_UP))
				&& !keyboardState.get(controls.get(Controls.MOVE_DOWN))) {
			player.stopMoving();
		}
	}

	private void maybeSpawnAsteroids(double dt) {
		timeToNextAsteroidSpawn -= dt;
		if (timeToNextAsteroidSpawn <= 0) {
			if (asteroidRandom.nextDouble() <= Constants.ASTEROID_SPAWN_PROBABILITY) {
				int type = asteroidRandom.nextInt(Constants.ASTEROID_TYPE_COUNT) + 1;
				double spawnXRange = Constants.ASTEROID_SPAWN_X_MAX - Constants.ASTEROID_SPAWN_X_MIN;
				double spawnX = asteroidRandom.nextDouble() * spawnXRange + Constants.ASTEROID_SPAWN_X_MIN;
				Vector2D location = new Vector2D(spawnX, Constants.ASTEROID_SPAWN_Y);
				Vector2D velocity = new Vector2D((Math.random() > 0.5 ? -1 : 1) * Constants.ASTEROID_X_VELOCITY + (Math.random() * 2.0 - 1.0) * Constants.ASTEROID_X_VELOCITY_JITTER,
						Constants.ASTEROID_Y_VELOCITY + (Math.random() * 2.0 - 1.0) * Constants.ASTEROID_Y_VELOCITY_JITTER);
				// TODO: Spawn asteroid logic with this hitMap
				Asteroid asteroid = new Asteroid(location, velocity, type);
				model.addEntity(asteroid);
				model.addAsteroid(asteroid);
				AsteroidView asteroidView = new AsteroidView(asteroid);
				viewMap.put(asteroid, asteroidView);
				view.addDrawable(asteroidView);
				view.addUpdatable(asteroidView);
			}
			timeToNextAsteroidSpawn = 1;
		}
	}

	private ArrayList<Entity> getEntitiesToCull() {
		ArrayList<Entity> toCull = new ArrayList<Entity>();
		for (Entity entity : model.getEntities()) {
			if (entity.shouldCull()) {
				toCull.add(entity);
			}
		}
		return toCull;
	}

	private void cullEntities(ArrayList<Entity> toCull) {
		for (Entity entity : toCull) {
			model.removeEntity(entity);
			if (entity instanceof Bullet) {
				model.removeBullet((Bullet) entity);
			}
			if (entity instanceof Asteroid) {
				model.removeAsteroid((Asteroid) entity);
			}
			deleteView(entity);
		}
	}

	private void checkGameOver() {
		if (!model.getLeftPlayer().isAlive() && !model.getRightPlayer().isAlive()) {
			model.setGameState(GameState.DRAW);
			view.onGameOver(GameState.DRAW);
		} else if (!model.getLeftPlayer().isAlive()) {
			model.setGameState(GameState.RIGHT_WIN);
			view.onGameOver(GameState.RIGHT_WIN);
		} else if (!model.getRightPlayer().isAlive()) {
			model.setGameState(GameState.LEFT_WIN);
			view.onGameOver(GameState.LEFT_WIN);
		}
	}

	public void update(double dt) {
		if (model.getGameState() != GameState.RUNNING) {
			if (keyboardState.get(KeyEvent.VK_ENTER)) {
				gameStarter.startGame(); // start a new game
			}
			return;
		}

		checkBulletCollisions();
		checkPlayerControls(model.getLeftPlayer(), leftPlayerControls);
		checkPlayerControls(model.getRightPlayer(), rightPlayerControls);
		maybeSpawnAsteroids(dt);
		cullEntities(getEntitiesToCull());
		checkGameOver();
		System.out.println(model.getEntities().size());
	}

	private void fireBullet(Player player) {
		Bullet bullet = player.fireBullet();
		if (bullet == null) {
			return;
		}
		model.addEntity(bullet);
		model.addBullet(bullet);
		((PlayerView) viewMap.get(player)).onBulletFired();

		BulletView bulletView = new BulletView(bullet);
		viewMap.put(bullet, bulletView);

		view.addDrawable(bulletView);
		view.addUpdatable(bulletView);
	}

	private void handleAsteroidHit(Asteroid asteroid, Bullet bullet) {
		((AsteroidView) viewMap.get(asteroid)).onAsteroidHit(bullet.getPosition());
	}

	private void handlePlayerHit(Player player, Bullet bullet) {
		player.receiveDamage(bullet.getDamage());
		((PlayerView) viewMap.get(player)).onPlayerHit();
	}

	public void handleKeyDown(int keyCode) {
		keyboardState.put(keyCode, true);
	}

	public void handleKeyUp(int keyCode) {
		keyboardState.put(keyCode, false);
	}
}
