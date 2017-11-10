package main;

import java.awt.Graphics2D;

import controller.MainController;
import model.Model;
import rafgfxlib.GameFrame;
import view.MainView;

@SuppressWarnings("serial")
public class GameWindow extends GameFrame implements GameStarter {
	private long lastUpdateTime;

	private Model model;
	private MainView view;
	private MainController controller;

	public GameWindow() {
		super("Smart Wars", Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		
		// Run game thread
		setUpdateRate(60);
		startThread();
		setHighQuality(true);
		
		// Start the war
		startGame();
	}

	@Override
	public void startGame() {
		model = new Model();
		view = new MainView();
		controller = new MainController(this, view, model);
		lastUpdateTime = System.nanoTime();
	}
	
	@Override
	public void handleWindowInit() {
	}

	@Override
	public void handleWindowDestroy() {
	}

	@Override
	public void render(Graphics2D g, int sw, int sh) {
		view.draw(g);
	}

	@Override
	public void update() {
		long currentTime = System.nanoTime();
		double dt = (double) (currentTime - lastUpdateTime) / 1e9;

		synchronized (this) {
			controller.update(dt);
			model.update(dt);
			view.update(dt);
		}

		lastUpdateTime = System.nanoTime();
	}

	@Override
	public void handleMouseDown(int x, int y, GFMouseButton button) {
	}

	@Override
	public void handleMouseUp(int x, int y, GFMouseButton button) {
	}

	@Override
	public void handleMouseMove(int x, int y) {
	}

	@Override
	public void handleKeyDown(int keyCode) {
		controller.handleKeyDown(keyCode);
	}

	@Override
	public void handleKeyUp(int keyCode) {
		controller.handleKeyUp(keyCode);
	}
}
