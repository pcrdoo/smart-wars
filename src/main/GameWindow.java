package main;

import java.awt.Graphics2D;

import controller.MainController;
import debug.DebugDisplay;
import debug.Measurement;
import debug.PerformanceMonitor;
import debug.PerformanceOverlay;
import model.Model;
import rafgfxlib.GameFrame;
import view.MainView;

@SuppressWarnings("serial")
public class GameWindow extends GameFrame implements GameStarter {
	private long lastUpdateTime;

	private Model model;
	private MainView view;
	private MainController controller;
	private PerformanceOverlay po;

	public GameWindow() {
		super("Smart Wars", Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		
		po = null;
		
		// Run game thread
		setUpdateRate(60);
		startThread();
		setHighQuality(true);
		
		// Start the war
		startGame();
	}

	public void usePerformanceOverlay(PerformanceOverlay po) {
		this.po = po;
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
