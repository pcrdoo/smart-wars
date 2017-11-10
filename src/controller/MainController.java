package controller;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import main.Constants;
import main.GameStarter;
import model.Bullet;
import model.GameState;
import model.Model;
import model.Player;
import util.Vector2D;
import view.BulletView;
import view.MainView;
import view.PlayerView;

public class MainController {

	private Model model;
	private MainView view;
	private GameStarter gameStarter;
	private HashMap<Integer, Boolean> keyboardState;
	private HashMap<Player, PlayerView> playerViewMap;
	private HashMap<Bullet, BulletView> bulletViewMap;

	public MainController(GameStarter gameStarter, MainView view, Model model) {
		this.view = view;
		this.model = model;
		this.gameStarter = gameStarter;

		bulletViewMap = new HashMap<>();

		initKeyboardState();

		PlayerView rightPlayerView = new PlayerView(model.getRightPlayer());
		view.addDrawable(rightPlayerView);
		view.addUpdatable(rightPlayerView);

		PlayerView leftPlayerView = new PlayerView(model.getLeftPlayer());
		view.addDrawable(leftPlayerView);
		view.addUpdatable(leftPlayerView);

		playerViewMap = new HashMap<>();
		playerViewMap.put(model.getLeftPlayer(), leftPlayerView);
		playerViewMap.put(model.getRightPlayer(), rightPlayerView);
	}

	private void initKeyboardState() {
		keyboardState = new HashMap<Integer, Boolean>();
		keyboardState.put(KeyEvent.VK_W, false);
		keyboardState.put(KeyEvent.VK_S, false);
		keyboardState.put(KeyEvent.VK_D, false);

		keyboardState.put(KeyEvent.VK_I, false);
		keyboardState.put(KeyEvent.VK_K, false);
		keyboardState.put(KeyEvent.VK_J, false);

		keyboardState.put(KeyEvent.VK_ENTER, false);
	}

	private void cullBullets(ArrayList<Bullet> toCull) {
		ArrayList<Bullet> bulletsToCull = new ArrayList<>();
		bulletsToCull.addAll(toCull);

		for (Bullet bullet : model.getBullets()) {
			Vector2D p = bullet.getPosition();
			if (p.getdX() < -Constants.BULLET_CULLING_X
					|| p.getdX() > Constants.WINDOW_WIDTH + Constants.BULLET_CULLING_X
					|| p.getdY() < -Constants.BULLET_CULLING_Y
					|| p.getdY() > Constants.WINDOW_HEIGHT + Constants.BULLET_CULLING_Y) {
				bulletsToCull.add(bullet);
			}
		}

		for (Bullet bullet : bulletsToCull) {
			model.removeBullet(bullet);
			model.removeEntity(bullet);

			BulletView bulletView = bulletViewMap.get(bullet);
			if (bulletView != null) {
				view.removeUpdatable(bulletView);
				view.removeDrawable(bulletView);
				bulletViewMap.remove(bullet);
			}
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
		playerViewMap.get(player).onPlayerHit();
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
	}

	private void fireBullet(Player player) {
		Bullet bullet = player.fireBullet();
		if (bullet == null) {
			return;
		}
		model.addEntity(bullet);
		model.addBullet(bullet);
		playerViewMap.get(player).onBulletFired();

		BulletView bulletView = new BulletView(bullet);
		bulletViewMap.put(bullet, bulletView);

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
