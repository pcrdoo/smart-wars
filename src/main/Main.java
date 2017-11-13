package main;

import java.awt.Color;

import debug.DebugDisplay;
import debug.PerformanceMonitor;
import debug.PerformanceOverlay;
import view.GameModeDialog;

public class Main {

	public static boolean isStringInArray(String needle, String[] haystack) {
		for (int i = 0; i < haystack.length; i++) {
			if (needle.equals(haystack[i])) {
				return true;
			}
		}

		return false;
	}

	public static boolean isPerfMeasurement(String name) {
		return isStringInArray(name, Constants.PERF_MEASUREMENTS_NAMES);
	}

	public static boolean isPerfStatistic(String name) {
		return isStringInArray(name, Constants.PERF_STATISTICS_NAMES);
	}

	public static boolean isDebugCategory(String name) {
		return isStringInArray(name, Constants.DEBUG_CATEGORY_NAMES);
	}

	public static boolean displayPerfItem(PerformanceOverlay po, String name) {
		if (isPerfMeasurement(name)) {
			po.addDisplayedMeasurement(name);
		} else if (isPerfStatistic(name)) {
			po.addDisplayedStatistic(name);
		} else {
			return false;
		}

		return true;
	}

	public static boolean displayDebugCategory(String category, Color c) {
		if (!isDebugCategory(category)) {
			return false;
		}

		DebugDisplay.getInstance().showCategory(category);
		DebugDisplay.getInstance().setCategoryColor(category, c);

		return true;
	}

	public static void main(String[] args) {
		PerformanceOverlay po = null;
		boolean fullscreen = false;
		boolean server = false;

		for (String arg : args) {
			if (arg.equals("+perf")) {
				PerformanceMonitor.getInstance().setEnabled(true);
				po = new PerformanceOverlay(PerformanceMonitor.getInstance());
			} else if (arg.startsWith("+perf:")) {
				if (po != null) {
					String[] names = arg.substring("+perf:".length()).split(",");
					for (String name : names) {
						if (!displayPerfItem(po, name)) {
							System.err.println("Unknown perf item: " + name);
							return;
						}
					}
				}
			} else if (arg.equals("+debug")) {
				DebugDisplay.getInstance().setEnabled(true);
			} else if (arg.startsWith("+debug:")) {
				String[] names = arg.substring("+debug:".length()).split(",");
				for (String name : names) {
					String category;
					Color c = Color.WHITE;
					if (name.indexOf('=') != -1) {
						int pos = name.indexOf('=');
						category = name.substring(0, pos);
						String color = name.substring(pos + 1);

						try {
							long colorInt = Long.parseLong(color, 16);
							c = new Color(((colorInt >> 16) & 0xff) / 255.0f, ((colorInt >> 8) & 0xff) / 255.0f,
									((colorInt >> 0) & 0xff) / 255.0f, ((colorInt >> 24) & 0xff) / 255.0f);
						} catch (NumberFormatException e) {
							System.err.println("Invalid color: " + color);
							return;
						}
					} else {
						category = name;
					}

					if (!displayDebugCategory(category, c)) {
						System.err.println("Unknown debug category: " + category);
						return;
					}
				}
			} else if (arg.equals("+fullscreen")) {
				fullscreen = true;
			} else if (arg.equals("+server")) {
				server = true;
			} else {
				System.err.println("Invalid argument: " + arg);
				return;
			}
		}

		if (server) {
			HeadlessGameFrame gf = new HeadlessGameFrame();
			gf.initGameWindow(false);
			gf.startGame();
		} else {
			GameModeDialog gameModeDialog = new GameModeDialog();
			GameWindow gw = new GameWindow(fullscreen, gameModeDialog.getGameMode(), gameModeDialog.getAddress());

			gw.usePerformanceOverlay(po);
			gw.initGameWindow(fullscreen);
			gw.startGame();
		}
	}

}
