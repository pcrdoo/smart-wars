package main;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.net.InetSocketAddress;

import controller.ClientController;
import controller.ServerController;
import debug.DebugDisplay;
import debug.Measurement;
import debug.PerformanceMonitor;
import debug.PerformanceOverlay;
import memory.Pools;
import model.Model;
import multiplayer.LocalPipe;
import rafgfxlib.GameFrame;
import util.ImageCache;
import view.ClientView;

@SuppressWarnings("serial")
public class GameWindow extends GameFrame implements GameStarter {
	private long lastUpdateTime;

	private Model localServerModel;
	private Model model;
	private ClientView view;
	private ClientController controller;
	private ServerController localServerController;
	private PerformanceOverlay po;
	private LoadingWindow loadingWindow;
	private GraphicsDevice device;
	private DisplayMode oldDisplayMode;
	private boolean fullscreen;
	private GameMode gameMode;
	private InetSocketAddress serverAddress;

	public GameWindow(boolean fullscreen, GameMode gameMode, InetSocketAddress serverAddress) {
		super("Smart Wars", Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

		this.fullscreen = fullscreen;
		this.gameMode = gameMode;
		this.serverAddress = serverAddress;
		loadingWindow = new LoadingWindow();
		loadingWindow.setVisible(true);

		po = null;
	}

	@Override
	public void initGameWindow(boolean fullscreen) {
		super.initGameWindow(fullscreen);
		getWindow().setLocationRelativeTo(null);
		getWindow().setVisible(false);
		getWindow().setBackground(Color.BLACK);
	}

	public void usePerformanceOverlay(PerformanceOverlay po) {
		this.po = po;
	}

	@Override
	public void startGame() {
		ImageCache.getInstance().preload(Constants.IMAGES_TO_PRELOAD);
		Pools.repopulate();

		System.out.println(gameMode + " " + serverAddress);

		model = new Model();
		localServerModel = new Model();
		view = new ClientView();
		if (gameMode == GameMode.NETWORK) {
			controller = new ClientController(this, view, model, gameMode, serverAddress);
		} else {
			localServerController = new ServerController(this, null, localServerModel, gameMode);
			controller = new ClientController(this, view, model, gameMode, null);
		}
		lastUpdateTime = System.nanoTime();

		setUpdateRate(60);
		setBackgroundClear(false);
		setHighQuality(true);

		if (fullscreen) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			device = ge.getDefaultScreenDevice();

			oldDisplayMode = device.getDisplayMode();
			try {
				DisplayMode dm = new DisplayMode(1280, 720, DisplayMode.BIT_DEPTH_MULTI,
						DisplayMode.REFRESH_RATE_UNKNOWN);
				device.setFullScreenWindow(getWindow());
				device.setDisplayMode(dm);
			} catch (Exception e) {
				System.err.println("Setting fullscreen failed: " + e.getMessage());
				System.exit(-1);
			}
		}

		if (gameMode == GameMode.NETWORK) {
			// Run game thread after sync
			try {
				controller.setUpConnections();
			} catch (Exception ex) {
				System.out.println("Failed to start game. Crash?");
				// TODO: how to handle
			}
		} else {
			LocalPipe pipe = new LocalPipe();
			controller.setLocalPipe(pipe);
			localServerController.setLocalPipe(pipe);
		}
		
		startThread();
		loadingWindow.setVisible(false);
		getWindow().setVisible(true);
	}

	@Override
	public void handleWindowInit() {
	}

	@Override
	public void handleWindowDestroy() {
		if (fullscreen) {
			device.setDisplayMode(oldDisplayMode);
			device.setFullScreenWindow(null);
			getWindow().setVisible(false);
		}
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
			if (gameMode == GameMode.LOCAL) {
				localServerController.update(dt);
				localServerModel.update(dt);
			}
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
		if (keyCode == KeyEvent.VK_ESCAPE) {
			handleWindowDestroy();
			System.exit(0);
		}

		controller.handleKeyDown(keyCode);
	}

	@Override
	public void handleKeyUp(int keyCode) {
		controller.handleKeyUp(keyCode);
	}
}
