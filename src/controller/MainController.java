package controller;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import debug.Measurement;
import debug.PerformanceMonitor;
import main.Constants;
import main.GameStarter;
import main.GameState;
import model.Asteroid;
import model.Wormhole;
import model.Bullet;
import model.Mirror;
import model.MirrorState;
import model.Model;
import model.Player;
import model.abilities.MirrorMagic;
import model.entitites.Entity;
import util.Vector2D;
import view.AsteroidView;
import view.WormholeView;
import view.BulletView;
import view.EntityView;
import view.MainView;
import view.MirrorView;
import view.PlayerView;

public class MainController {

	private Model model;
	private MainView view;
	private GameStarter gameStarter;
	private HashMap<Controls, Integer> leftPlayerControls;
	private HashMap<Controls, Integer> rightPlayerControls;
	private HashMap<Integer, Boolean> keyboardState;
	private ArrayList<AsteroidView> disintegratingAsteroids;

	// View-related dependencies for each entity.
	private HashMap<Entity, EntityView> viewMap;

	private double timeToNextAsteroidSpawn;
	private double timeToNextWormholeSpawn;
	private Random asteroidRandom;

	public MainController(GameStarter gameStarter, MainView view, Model model) {
		this.view = view;
		this.model = model;
		this.gameStarter = gameStarter;

		viewMap = new HashMap<>();
		disintegratingAsteroids = new ArrayList<>();

		PlayerView leftPlayerView = new PlayerView(model.getLeftPlayer());
		view.addDrawable(leftPlayerView, Constants.Z_PLAYER);
		view.addUpdatable(leftPlayerView);
		leftPlayerControls = new HashMap<Controls, Integer>();
		leftPlayerControls.put(Controls.MOVE_UP, KeyEvent.VK_W);
		leftPlayerControls.put(Controls.MOVE_DOWN, KeyEvent.VK_S);
		leftPlayerControls.put(Controls.FIRE_GUN, KeyEvent.VK_D);
		leftPlayerControls.put(Controls.SHORT_MIRROR_MAGIC, KeyEvent.VK_Q);
		leftPlayerControls.put(Controls.LONG_MIRROR_MAGIC, KeyEvent.VK_A);

		PlayerView rightPlayerView = new PlayerView(model.getRightPlayer());
		view.addDrawable(rightPlayerView, Constants.Z_PLAYER);
		view.addUpdatable(rightPlayerView);
		rightPlayerControls = new HashMap<Controls, Integer>();
		rightPlayerControls.put(Controls.MOVE_UP, KeyEvent.VK_I);
		rightPlayerControls.put(Controls.MOVE_DOWN, KeyEvent.VK_K);
		rightPlayerControls.put(Controls.FIRE_GUN, KeyEvent.VK_J);
		rightPlayerControls.put(Controls.SHORT_MIRROR_MAGIC, KeyEvent.VK_O);
		rightPlayerControls.put(Controls.LONG_MIRROR_MAGIC, KeyEvent.VK_L);

		initKeyboardState();

		viewMap.put(model.getLeftPlayer(), leftPlayerView);
		viewMap.put(model.getRightPlayer(), rightPlayerView);

		timeToNextAsteroidSpawn = 1;
		timeToNextWormholeSpawn = 1;
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
			if ((bullet.getBounces() > 0 || bullet.getOwner() == model.getRightPlayer()) && model.getLeftPlayer().hitTest(bullet.getPosition())) {
				handlePlayerHit(model.getLeftPlayer(), bullet);
				impactedBullets.add(bullet);
			} else if ((bullet.getBounces() > 0 || bullet.getOwner() == model.getLeftPlayer())
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
				if (impactedBullets.contains(bullet)) {
					continue;
				}
				// Mirror collision
				for (Mirror mirror : model.getMirrors()) {
					if (mirror.hitTest(bullet.getPosition())) {
						handleMirrorHit(mirror, bullet);
					}
				}
				// Black hole collision
				for (Wormhole wormhole : model.getWormholes()) {
					if (wormhole.hitTest(bullet.getPosition())) {
						handleWormholeHit(wormhole, bullet);
					}
				}
			}
			
			// Affect the bullet views by near wormholes
			Wormhole nearestWormhole = null;
			double nearestWormholeDistance = 0.0;
			for (Wormhole w : model.getWormholes()) {
				double dist = w.getPosition().sub(bullet.getPosition()).length(); 
				if (nearestWormhole == null || dist < nearestWormholeDistance) {
					nearestWormhole = w;
					nearestWormholeDistance = dist;
				}
			}
			
			if (nearestWormhole != null) {
				((BulletView) viewMap.get(bullet)).wormholeAffect(nearestWormhole);
			}
		}
		cullBullets(impactedBullets);
	}

	private void disintegrateAsteroid(Asteroid asteroid) {
		// Delete asteroid, keep just the view for the animation.
		model.removeEntity(asteroid);
		model.removeAsteroid(asteroid);
		AsteroidView asteroidView = (AsteroidView) viewMap.get(asteroid);
		viewMap.remove(asteroid);
		disintegratingAsteroids.add(asteroidView);
		asteroidView.onAsteroidDisintegrated();
	}

	private void checkAsteroidPlayerCollisions() {
		ArrayList<Asteroid> toDisintegrate = new ArrayList<>();
		for (Asteroid asteroid : model.getAsteroids()) {
			if (asteroid.hitTest(model.getLeftPlayer())) {
				handlePlayerAsteroidHit(model.getLeftPlayer(), asteroid);
				toDisintegrate.add(asteroid);
			} else if (asteroid.hitTest(model.getRightPlayer())) {
				handlePlayerAsteroidHit(model.getRightPlayer(), asteroid);
				toDisintegrate.add(asteroid);
			}
		}
		for (Asteroid asteroid : toDisintegrate) {
			disintegrateAsteroid(asteroid);
		}
	}

	private void handlePlayerAsteroidHit(Player player, Asteroid asteroid) {
		// Hurt the player.
		player.receiveDamage(Constants.ASTEROID_PLAYER_DAMAGE);
		((PlayerView) viewMap.get(player)).onPlayerHit();
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

	private void checkDisintegratingAsteroids() {
		ArrayList<AsteroidView> finished = new ArrayList<>();
		for (AsteroidView asteroidView : disintegratingAsteroids) {
			if (asteroidView.isDisintegrated()) {
				// Done!
				finished.add(asteroidView);
				view.removeUpdatable(asteroidView);
				view.removeDrawable(asteroidView);
			}
		}
		for (AsteroidView asteroidView : finished) {
			disintegratingAsteroids.remove(asteroidView);
		}
	}

	private void checkDyingWormholes() {
		ArrayList<Entity> deadWormholes = new ArrayList<>();
		for (Wormhole wormhole : model.getWormholes()) {
			if (wormhole.isDead()) {
				deadWormholes.add(wormhole);
			}
		}
		cullEntities(deadWormholes);
	}

	private void maybeSpawnAsteroids(double dt) {
		timeToNextAsteroidSpawn -= dt;
		if (timeToNextAsteroidSpawn <= 0) {
			if (asteroidRandom.nextDouble() <= Constants.ASTEROID_SPAWN_PROBABILITY) {
				int type = asteroidRandom.nextInt(Constants.ASTEROID_TYPE_COUNT) + 1;
				int frame = asteroidRandom.nextInt(Constants.ASTEROID_SPRITES_X * Constants.ASTEROID_SPRITES_Y);
				double spawnXRange = Constants.ASTEROID_SPAWN_X_MAX - Constants.ASTEROID_SPAWN_X_MIN;
				double spawnX = asteroidRandom.nextDouble() * spawnXRange + Constants.ASTEROID_SPAWN_X_MIN;
				Vector2D location = new Vector2D(spawnX, Constants.ASTEROID_SPAWN_Y);
				Vector2D velocity = new Vector2D(
						(Math.random() > 0.5 ? -1 : 1) * Constants.ASTEROID_X_VELOCITY
								+ (Math.random() * 2.0 - 1.0) * Constants.ASTEROID_X_VELOCITY_JITTER,
						Constants.ASTEROID_Y_VELOCITY
								+ (Math.random() * 2.0 - 1.0) * Constants.ASTEROID_Y_VELOCITY_JITTER);
				Asteroid asteroid = new Asteroid(location, velocity, type, frame);
				model.addEntity(asteroid);
				model.addAsteroid(asteroid);
				AsteroidView asteroidView = new AsteroidView(asteroid);
				viewMap.put(asteroid, asteroidView);
				view.addDrawable(asteroidView, Constants.Z_ASTEROID);
				view.addUpdatable(asteroidView);
			}
			timeToNextAsteroidSpawn = 1;
		}
	}

	private void SpawnWormholeFromAsteroid(Asteroid asteroid) {
		Wormhole wormhole = new Wormhole(asteroid.getPosition());
		disintegrateAsteroid(asteroid);
		model.addEntity(wormhole);
		model.addWormhole(wormhole);
		WormholeView wormholeView = new WormholeView(wormhole);
		viewMap.put(wormhole, wormholeView);
		view.addDrawable(wormholeView, Constants.Z_BLACK_HOLE);
		view.addUpdatable(wormholeView);
	}

	private void maybeSpawnWormholes(double dt) {
		timeToNextWormholeSpawn -= dt;
		if (timeToNextWormholeSpawn <= 0) {
			if (asteroidRandom.nextDouble() <= Constants.WORMHOLE_SPAWN_PROBABILITY && model.getAsteroids().size() >= 2
					&& model.getWormholes().size() == 0) {
				Collections.shuffle(model.getAsteroids());
				ArrayList<Asteroid> asteroidsToTransform = new ArrayList<Asteroid>(model.getAsteroids().subList(0, 2));
				for (Asteroid asteroid : asteroidsToTransform) {
					SpawnWormholeFromAsteroid(asteroid);
				}
			}
			timeToNextWormholeSpawn = 1;
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
			if (entity instanceof Wormhole) {
				model.removeWormhole((Wormhole) entity);
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

		PerformanceMonitor m = PerformanceMonitor.getInstance();
		Measurement ms;
		
		ms = m.measure("CollisionBullet");
		checkBulletCollisions();
		ms.done();
		
		ms = m.measure("CollisionAsteroidPlayer");
		checkAsteroidPlayerCollisions();
		ms.done();

		checkPlayerControls(model.getLeftPlayer(), leftPlayerControls);
		checkPlayerControls(model.getRightPlayer(), rightPlayerControls);

		checkDisintegratingAsteroids();
		checkDyingWormholes();

		maybeSpawnWormholes(dt);
		maybeSpawnAsteroids(dt);
		
		ms = m.measure("CullEntity");
		cullEntities(getEntitiesToCull());
		ms.done();
		
		checkGameOver();
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

		view.addDrawable(bulletView, Constants.Z_BULLETS);
		view.addUpdatable(bulletView);
	}

	private void doMirrorMagic(MirrorMagic mirrorMagic) {
		if (mirrorMagic.getMirror() == null) {
			if (!mirrorMagic.launchMirror()) {
				return;
			}
			Mirror mirror = mirrorMagic.getMirror();
			model.addEntity(mirror);
			model.addMirror(mirror);
			MirrorView mirrorView = new MirrorView(mirror);
			viewMap.put(mirror, mirrorView);
			view.addDrawable(mirrorView, Constants.Z_MIRROR);
			view.addUpdatable(mirrorView);
		} else {
			switch (mirrorMagic.getMirror().getMirrorState()) {
			case TRAVELLING:
				mirrorMagic.getMirror().setMirrorState(MirrorState.SPINNING);
				break;
			case SPINNING:
				mirrorMagic.getMirror().setMirrorState(MirrorState.STABLE);
				break;
			case STABLE:
				Mirror mirror = mirrorMagic.getMirror();
				model.removeEntity(mirror);
				model.removeMirror(mirror);
				deleteView(mirror);
				mirrorMagic.removeMirror();
				break;
			default:
				break;
			}
		}
	}

	private void handleWormholeHit(Wormhole wormhole, Bullet bullet) {
		// Find other black hole.
		assert (model.getWormholes().size() == 2);
		int otherWormholeIndex = wormhole == model.getWormholes().get(1) ? 0 : 1;
		System.out.println(otherWormholeIndex);
		Wormhole otherWormhole = model.getWormholes().get(otherWormholeIndex);
		bullet.teleport(wormhole, otherWormhole);
	}

	private void handleMirrorHit(Mirror mirror, Bullet bullet) {
		bullet.bounce(mirror);
		((MirrorView) viewMap.get(mirror)).onMirrorHit(bullet);
	}

	private void handleAsteroidHit(Asteroid asteroid, Bullet bullet) {
		asteroid.getPushed(bullet.getVelocity());
		((AsteroidView) viewMap.get(asteroid)).onAsteroidHit(bullet.getPosition());
	}

	private void handlePlayerHit(Player player, Bullet bullet) {
		player.receiveDamage(bullet.getDamage());
		((PlayerView) viewMap.get(player)).onPlayerHit();
	}

	public void handleKeyDown(int keyCode) {
		keyboardState.put(keyCode, true);
		if (keyCode == leftPlayerControls.get(Controls.SHORT_MIRROR_MAGIC)) {
			doMirrorMagic(model.getLeftPlayer().getShortMirrorMagic());
		}
		if (keyCode == leftPlayerControls.get(Controls.LONG_MIRROR_MAGIC)) {
			doMirrorMagic(model.getLeftPlayer().getLongMirrorMagic());
		}
		if (keyCode == rightPlayerControls.get(Controls.SHORT_MIRROR_MAGIC)) {
			doMirrorMagic(model.getRightPlayer().getShortMirrorMagic());
		}
		if (keyCode == rightPlayerControls.get(Controls.LONG_MIRROR_MAGIC)) {
			doMirrorMagic(model.getRightPlayer().getLongMirrorMagic());
		}
	}

	public void handleKeyUp(int keyCode) {
		keyboardState.put(keyCode, false);
	}
}
