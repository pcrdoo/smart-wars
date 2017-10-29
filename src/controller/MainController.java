package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import model.Bullet;
import model.Model;
import model.Player;
import view.BulletView;
import view.MainView;

public class MainController {
	
	private MainView view;
	private Model model;
	private HashMap<Integer, Boolean> keyboardState;

	public MainController(MainView view, Model model) {
		this.view = view;
		this.model = model;
		
		this.initKeyboardState();
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
	
	public void update() {		
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
		Bullet b = player.shoot();
		if(b == null) {
			return;
		}
		model.addUpdatable(b);
		BulletView bv = new BulletView(b);
		view.addDrawable(bv);
		view.addUpdatable(bv);
	}

	public void handleKeyDown(int keyCode) {
		this.keyboardState.put(keyCode, true);
		System.out.println(keyCode);
	}

	public void handleKeyUp(int keyCode) {
		this.keyboardState.put(keyCode, false);
	}
}

