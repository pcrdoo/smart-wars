package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import main.Constants;
import model.Bullet;
import model.Model;
import model.Player;
import util.Vector2D;
import view.BulletView;
import view.MainView;
import view.PlayerView;

public class MainController {
	
	private MainView view;
	private Model model;
	private HashMap<Integer, Boolean> keyboardState;
	private HashMap<Bullet, BulletView> bulletViewMap;
	private ArrayList<Bullet> bullets;
	private HashMap<Player, PlayerView> playerViewMap;
	
	public MainController(MainView view, Model model) {
		this.view = view;
		this.model = model;
		
		bulletViewMap = new HashMap<>();
		bullets = new ArrayList<>();
		
		this.initKeyboardState();
		
		PlayerView bottomPlayerView = new PlayerView(model.getBottomPlayer(), false);
		view.addDrawable(bottomPlayerView);
		view.addUpdatable(bottomPlayerView);

		PlayerView topPlayerView = new PlayerView(model.getTopPlayer(), true);
		view.addDrawable(topPlayerView);
		view.addUpdatable(topPlayerView);
		
		playerViewMap = new HashMap<>();
		playerViewMap.put(model.getTopPlayer(), topPlayerView);
		playerViewMap.put(model.getBottomPlayer(), bottomPlayerView);
	}
	
	private void initKeyboardState() {
		this.keyboardState = new HashMap<Integer, Boolean>();
		keyboardState.put(KeyEvent.VK_NUMPAD8, false);
		keyboardState.put(KeyEvent.VK_NUMPAD4, false);
		keyboardState.put(KeyEvent.VK_NUMPAD6, false);
		
		keyboardState.put(KeyEvent.VK_W, false);
		keyboardState.put(KeyEvent.VK_A, false);
		keyboardState.put(KeyEvent.VK_D, false);
	}
	
	private void cullBullets(ArrayList<Bullet> toCull) {
		ArrayList<Bullet> bulletsToCull;
		if (toCull != null) {
			bulletsToCull = toCull;
		} else {
			bulletsToCull = new ArrayList<>();
			for (Bullet b : bullets) {
				Vector2D p = b.getPosition();
				if (p.getdX() < -Constants.BULLET_CULLING_X || p.getdX() > Constants.WINDOW_WIDTH + Constants.BULLET_CULLING_X ||
						p.getdY() < -Constants.BULLET_CULLING_Y || p.getdY() > Constants.WINDOW_HEIGHT + Constants.BULLET_CULLING_Y) {
					bulletsToCull.add(b);
				}
			}
		}
		
		for (Bullet b : bulletsToCull) {
			bullets.remove(b);
			model.removeUpdatable(b);
			
			BulletView v = bulletViewMap.get(b);
			if (v != null) {
				view.removeUpdatable(v);
				view.removeDrawable(v);
				bulletViewMap.remove(b);
			}
		}
	}
	
	private void checkBulletCollisions() {
		ArrayList<Bullet> impactedBullets = new ArrayList<>();
		for (Bullet b : bullets) {
			if (b.getOwner() ==  model.getBottomPlayer() && model.getTopPlayer().hitTest(b.getPosition())) {
				handlePlayerHit(model.getTopPlayer());
				impactedBullets.add(b);
			} else if (b.getOwner() ==  model.getTopPlayer() && model.getBottomPlayer().hitTest(b.getPosition())) {
				handlePlayerHit(model.getBottomPlayer());
				impactedBullets.add(b);
			}
		}
		
		cullBullets(impactedBullets);
	}
	
	public void handlePlayerHit(Player player) {
		playerViewMap.get(player).onHit();
		// TODO: Update health
	}
	
	public void update() {		
		checkBulletCollisions();
		
		if (keyboardState.get(KeyEvent.VK_NUMPAD8))
			this.doShoot(this.model.getBottomPlayer());
		if (keyboardState.get(KeyEvent.VK_NUMPAD4))
			model.getBottomPlayer().moveLeft();
		if (keyboardState.get(KeyEvent.VK_NUMPAD6))
			model.getBottomPlayer().moveRight();
		if (keyboardState.get(KeyEvent.VK_NUMPAD4) && keyboardState.get(KeyEvent.VK_NUMPAD6))
			model.getBottomPlayer().stopMoving();
		if (!keyboardState.get(KeyEvent.VK_NUMPAD4) && !keyboardState.get(KeyEvent.VK_NUMPAD6))
			model.getBottomPlayer().stopMoving();
		
		if (keyboardState.get(KeyEvent.VK_W))
			this.doShoot(this.model.getTopPlayer());
		if (keyboardState.get(KeyEvent.VK_A))
			model.getTopPlayer().moveLeft();
		if (keyboardState.get(KeyEvent.VK_D))
			model.getTopPlayer().moveRight();
		if (keyboardState.get(KeyEvent.VK_A) && keyboardState.get(KeyEvent.VK_D))
			model.getTopPlayer().stopMoving();
		if (!keyboardState.get(KeyEvent.VK_A) && !keyboardState.get(KeyEvent.VK_D))
			model.getTopPlayer().stopMoving();
	}

	private void doShoot(Player player) {
		cullBullets(null);
		
		Bullet b = player.shoot();
		if(b == null) {
			return;
		}
		model.addUpdatable(b);
		bullets.add(b);
		playerViewMap.get(player).onShoot();
		
		BulletView bv = new BulletView(b);
		bulletViewMap.put(b, bv);
		
		view.addDrawable(bv);
		view.addUpdatable(bv);
	}

	public void handleKeyDown(int keyCode) {
		this.keyboardState.put(keyCode, true);
	}

	public void handleKeyUp(int keyCode) {
		this.keyboardState.put(keyCode, false);
	}
}

