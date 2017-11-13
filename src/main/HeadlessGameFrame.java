package main;

import java.awt.Color;
import java.awt.Graphics2D;

import controller.ServerController;
import memory.Pools;
import model.Model;
import rafgfxlib.GameFrame;
import view.ServerView;

@SuppressWarnings("serial")
public class HeadlessGameFrame extends GameFrame implements GameStarter {
	private long lastUpdateTime;

	private Model model;
	private ServerView view;
	private ServerController controller;
	private LoadingWindow loadingWindow;

	public HeadlessGameFrame() {
		super("Smart Wars", Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		// TODO: ^ what else could I do :(

		loadingWindow = new LoadingWindow();
		loadingWindow.setVisible(true);
	}

	@Override
	public void initGameWindow(boolean fullscreen) {
		super.initGameWindow(false);

		getWindow().setLocationRelativeTo(null);
		getWindow().setVisible(false);
		setVisible(false);
		getWindow().setBackground(Color.BLACK);
	}

	@Override
	public void startGame() {
		Pools.repopulate();

		model = new Model();
		view = new ServerView();
		controller = new ServerController(this, view, model, GameMode.NETWORK);
		lastUpdateTime = System.nanoTime();

		loadingWindow.setVisible(false);
		// Never show the game or perform updates, show ServerView instead
		view.setVisible(true);

		// Run game thread
		setUpdateRate(60);
		
		try {
			controller.setUpConnections();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Failed to start game.");
			// TODO: how to handle
		}
		startThread();
	}

	@Override
	public void handleWindowInit() {
	}

	@Override
	public void handleWindowDestroy() {
	}

	@Override
	public void render(Graphics2D g, int sw, int sh) {
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
	}

	@Override
	public void handleKeyUp(int keyCode) {
	}
}
