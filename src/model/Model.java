package model;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import debug.DebugDisplay;
import debug.PerformanceMonitor;
import main.GameState;
import memory.Pools;
import model.entitites.Entity;
import util.Vector2D;

public class Model {

	private Player leftPlayer;
	private Player rightPlayer;
	private ArrayList<Entity> entities;
	private ArrayList<Bullet> bullets; // separate bullets for culling
	private ArrayList<Asteroid> asteroids; // separate asteroids for culling
	private ArrayList<Mirror> mirrors; // separate mirrors for culling
	private ArrayList<Wormhole> wormholes; // separate wormholes for culling

	private GameState gameState;

	public Model() {
		gameState = GameState.RUNNING;
		leftPlayer = new Player(PlayerSide.LEFT_PLAYER);
		rightPlayer = new Player(PlayerSide.RIGHT_PLAYER);
		bullets = new ArrayList<>();
		asteroids = new ArrayList<>();
		mirrors = new ArrayList<>();
		wormholes = new ArrayList<>();

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
		
		PerformanceMonitor m = PerformanceMonitor.getInstance();
		m.recordStatistic("CountEntities", entities.size());
		m.recordStatistic("CountBullets", bullets.size());
		m.recordStatistic("CountAsteroids", asteroids.size());
		m.recordStatistic("CountMirrors", mirrors.size());
		m.recordStatistic("CountWormholes", wormholes.size());
		
		DebugDisplay dd = DebugDisplay.getInstance();
		if (dd.isEnabled()) {
			for (Entity e : entities) {
				dd.addRectangle("EntityBoundingBoxes", e.getBoundingBox());
			}
			
			dd.addOval("PlayerCircles", leftPlayer.getBoundingBox());
			dd.addOval("PlayerCircles", rightPlayer.getBoundingBox());
			
			for (Wormhole w : wormholes) {
				dd.addOval("WormholeCircles", w.getBoundingBox());
			}
			
			for (Mirror mr : mirrors) {
				Vector2D pos = mr.getPosition();
				Vector2D lv = mr.getLineVector();
				
				Vector2D tl = pos.sub(lv.scale(0.5)), br = pos.add(lv.scale(0.5));
				dd.addLine("MirrorLines", new Point((int)tl.getX(), (int)tl.getY()), new Point((int)br.getX(), (int)br.getY()));
				
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

	public void addMirror(Mirror mirror) {
		mirrors.add(mirror);
	}

	public void removeMirror(Mirror mirror) {
		mirrors.remove(mirror);
	}

	public ArrayList<Mirror> getMirrors() {
		return mirrors;
	}

	public void addWormhole(Wormhole wormhole) {
		wormholes.add(wormhole);
	}

	public void removeWormhole(Wormhole wormhole) {
		wormholes.remove(wormhole);
	}

	public ArrayList<Wormhole> getWormholes() {
		return wormholes;
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
		if (gameState != GameState.RUNNING) {
			// Purge everything
			for (Asteroid a : asteroids) {
				Pools.ASTEROID.free(a);
			}
			
			for (Bullet b : bullets) {
				Pools.BULLET.free(b);
			}
		}
	}

	public GameState getGameState() {
		return gameState;
	}
}
