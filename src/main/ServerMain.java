package main;

import java.awt.Color;

import debug.DebugDisplay;
import debug.PerformanceMonitor;
import debug.PerformanceOverlay;

public class ServerMain {

	public static void main(String[] args) {
		com.sun.javafx.application.PlatformImpl.startup(()->{});

		HeadlessGameFrame gf = new HeadlessGameFrame();
		gf.initGameWindow(false);
		gf.startGame();
	}

}
