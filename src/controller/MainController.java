package controller;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import main.Constants;
import main.GameStarter;
import model.Bullet;
import model.Entity;
import model.GameState;
import model.Model;
import model.Player;
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
	}

	private void initKeyboardState() {
		keyboardState = new HashMap<Integer, Boolean>();
		for(HashMap.Entry<Controls, Integer> entry : leftPlayerControls.entrySet()) {
			keyboardState.put((Integer) entry.getValue(), false);
		}
		for(HashMap.Entry<Controls, Integer> entry : rightPlayerControls.entrySet()) {
			keyboardState.put((Integer) entry.getValue(), false);
		}
		keyboardState.put(KeyEvent.VK_ENTER, false);
	}

	private ArrayList<Entity> findEntitiesOutOfBounds() {
		ArrayList<Entity> outOfBounds = new ArrayList<Entity>();
		for (Entity entity : model.getEntities()) {
			Rectangle boundingBox = entity.getBoundingBox();
			if (boundingBox.getMaxX() < 0 || boundingBox.getMinX() > Constants.WINDOW_WIDTH || boundingBox.getMaxY() < 0
					|| boundingBox.getMinY() > Constants.WINDOW_HEIGHT) {
				outOfBounds.add(entity);
			}
		}
		return outOfBounds;
	}

	private void cullEntities(ArrayList<Entity> toCull) {
		for (Entity entity : toCull) {
			model.removeEntity(entity);
			if (entity instanceof Bullet) {
				model.removeBullet((Bullet) entity);
			}
			deleteView(entity);
		}
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
			}
		}
		cullBullets(impactedBullets);
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

	public void handlePlayerHit(Player player, Bullet bullet) {
		player.receiveDamage(bullet.getDamage());
		((PlayerView) viewMap.get(player)).onPlayerHit();
	}

	public void update() {
		if (model.getGameState() != GameState.RUNNING) {
			if (keyboardState.get(KeyEvent.VK_ENTER)) {
				gameStarter.startGame(); // start a new game
			}
			return;
		}
		
		checkBulletCollisions();

		if (keyboardState.get(KeyEvent.VK_J))
			fireBullet(model.getRightPlayer());
		if (keyboardState.get(KeyEvent.VK_I))
			model.getRightPlayer().moveUp();
		if (keyboardState.get(KeyEvent.VK_K))
			model.getRightPlayer().moveDown();
		if (keyboardState.get(KeyEvent.VK_I) && keyboardState.get(KeyEvent.VK_K))
			model.getRightPlayer().stopMoving();
		if (!keyboardState.get(KeyEvent.VK_I) && !keyboardState.get(KeyEvent.VK_K))
			model.getRightPlayer().stopMoving();

		if (keyboardState.get(KeyEvent.VK_D))
			fireBullet(model.getLeftPlayer());
		if (keyboardState.get(KeyEvent.VK_W))
			model.getLeftPlayer().moveUp();
		if (keyboardState.get(KeyEvent.VK_S))
			model.getLeftPlayer().moveDown();
		if (keyboardState.get(KeyEvent.VK_W) && keyboardState.get(KeyEvent.VK_S))
			model.getLeftPlayer().stopMoving();
		if (!keyboardState.get(KeyEvent.VK_W) && !keyboardState.get(KeyEvent.VK_S))
			model.getLeftPlayer().stopMoving();

		checkGameOver();
		cullEntities(findEntitiesOutOfBounds());
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

	public void handleKeyDown(int keyCode) {
		keyboardState.put(keyCode, true);
	}

	public void handleKeyUp(int keyCode) {
		keyboardState.put(keyCode, false);
	}
}
