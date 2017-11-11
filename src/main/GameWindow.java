package main;

import java.awt.Color;
import java.awt.Graphics2D;

import controller.MainController;
import debug.DebugDisplay;
import debug.Measurement;
import debug.PerformanceMonitor;
import debug.PerformanceOverlay;
import memory.Pools;
import model.Model;
import rafgfxlib.GameFrame;
import util.ImageCache;
import view.MainView;

@SuppressWarnings("serial")
public class GameWindow extends GameFrame implements GameStarter {
	private long lastUpdateTime;

	private Model model;
	private MainView view;
	private MainController controller;
	private PerformanceOverlay po;
	private LoadingWindow loadingWindow;
	
	public GameWindow() {
		super("Smart Wars", Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		
		loadingWindow = new LoadingWindow();
		loadingWindow.setVisible(true);

		po = null;
	}
	
	@Override
	public void initGameWindow() {
		super.initGameWindow();
		
		getWindow().setLocationRelativeTo(null);
		getWindow().setVisible(false);
		setVisible(false);
		getWindow().setBackground(Color.BLACK);
	}

	public void usePerformanceOverlay(PerformanceOverlay po) {
		this.po = po;
	}
	
	@Override
	public void startGame() {
		ImageCache.getInstance().preload(Constants.IMAGES_TO_PRELOAD);
		Pools.repopulate();
		
		model = new Model();
		view = new MainView();
		controller = new MainController(this, view, model);
		lastUpdateTime = System.nanoTime();
		
		loadingWindow.setVisible(false);

		// Run game thread
		setUpdateRate(60);
		startThread();
		setHighQuality(true);
		
		getWindow().setVisible(true);
		setVisible(true);
	}
	
	@Override
	public void handleWindowInit() {
	}

	@Override
	public void handleWindowDestroy() {
	}

	@Override
	public void render(Graphics2D g, int sw, int sh) {
		Measurement ms = PerformanceMonitor.getInstance().measure("DrawTotal");
		view.draw(g);
		ms.done();
		
		if (po != null) {
			po.draw(g);
		}
		
		DebugDisplay.getInstance().draw(g);
	}

	@Override
	public void update() {
		long currentTime = System.nanoTime();
		double dt = (double) (currentTime - lastUpdateTime) / 1e9;
		
		Measurement ms;
		PerformanceMonitor m = PerformanceMonitor.getInstance();
		synchronized (this) {
			ms = m.measure("ControllerUpdateTotal");
			controller.update(dt);
			ms.done();
			
			ms = m.measure("ModelUpdateTotal");
			model.update(dt);
			ms.done();
			
			ms = m.measure("ViewUpdateTotal");
			view.update(dt);
			ms.done();
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
