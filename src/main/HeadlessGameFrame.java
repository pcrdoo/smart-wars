package main;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

import controller.ServerController;
import memory.Pools;
import model.Model;
import multiplayer.NetworkException;
import rafgfxlib.GameFrame;
import view.ServerView;

@SuppressWarnings("serial")
public class HeadlessGameFrame implements GameStarter {
	private long lastUpdateTime;
	private long lastModelUpdateTime;

	private boolean stopThread = false;

	private Model model;
	private ServerController controller;
	private Thread runnerThread;
	
	public HeadlessGameFrame() {
		System.out.println("Initializing server...");
	}
	
	private void threadWorker() {
		System.out.println("Game started!");
		
		double updateIntervalMilis = 1000.0 / 60.0;
		long lastUpdate = System.nanoTime();
		while (true) {
			if (stopThread) {
				stopThread = false;
				return;
			}
			
			update();
			try {
				long waitTime = (long) (updateIntervalMilis - (System.nanoTime() - lastUpdate) / 1.0e6);
				if (waitTime > 0) {
					Thread.sleep(waitTime);
				}
			} catch (InterruptedException e) { }
			
			lastUpdate = System.nanoTime();
		}
	}
	
	public void stopThread() {
		if (runnerThread != null && runnerThread.isAlive()) {
			stopThread = true;
			try {
				runnerThread.join();
			} catch (InterruptedException e) { }
			stopThread = false;
		}
	}
	
	public void startThread() {
		stopThread();
		runnerThread = new Thread(() -> {
			threadWorker();
		});
		runnerThread.start();
	}
	
	@Override
	public void startGame() {
		if (Thread.currentThread() == runnerThread) {
			// Restarting the game from the runner thread just means we want to stop it.
			// The main thread will join on it and then restart it properly.
			stopThread = true;
			return;
		}
		
		stopThread();
		Pools.repopulate(false);

		model = new Model();
		controller = new ServerController(this, model, GameMode.NETWORK);
		lastUpdateTime = System.nanoTime();
		
		try {
			System.out.println("Waiting for client connections...");
			controller.setUpConnections();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Failed to start game.");
		}

		System.out.println("Starting game thread.");
		startThread();
		lastUpdateTime = System.nanoTime();
		lastModelUpdateTime = System.nanoTime();
		
		// Pause until the game is finished.
		try {
			runnerThread.join();
		} catch (InterruptedException e) { }
	}
	
	public void update() {
		try {
			controller.update((System.nanoTime() - lastUpdateTime) / 1.0e9);
			lastUpdateTime = System.nanoTime();
			model.update((System.nanoTime() - lastModelUpdateTime) / 1.0e9);
			lastModelUpdateTime = System.nanoTime();
		} catch (NetworkException e) {
			System.err.println("Network error: " + e.getMessage());
			System.err.println("Restarting game.");
			
			startGame();
		}
	}
}
