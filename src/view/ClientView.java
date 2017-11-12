package view;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import debug.PerformanceMonitor;
import main.Constants;
import main.GameState;

public class ClientView extends MainView {
	private BackdropView backdrop;

	private TreeMap<Integer, ArrayList<Drawable>> drawables;
	private ArrayList<Updatable> updatables;

	public ClientView() {
		super();
		drawables = new TreeMap<>();
		updatables = new ArrayList<>();

		backdrop = new BackdropView();
		addDrawable(backdrop, Constants.Z_BACKDROP);
		updatables.add(backdrop);
	}

	public void addDrawable(Drawable d, int zIndex) {
		synchronized (drawables) {
			ArrayList<Drawable> drawablesInZIndex;
			if (!drawables.containsKey(zIndex)) {
				drawablesInZIndex = new ArrayList<>();
				drawables.put(zIndex, drawablesInZIndex);

			} else {
				drawablesInZIndex = drawables.get(zIndex);
			}

			drawablesInZIndex.add(d);
		}
	}

	public void removeDrawable(Drawable d) {
		synchronized (drawables) {
			for (Map.Entry<Integer, ArrayList<Drawable>> dr : drawables.entrySet()) {
				dr.getValue().remove(d);
			}
		}
	}

	public void addUpdatable(Updatable u) {
		synchronized (updatables) {
			updatables.add(u);
		}
	}

	public void removeUpdatable(Updatable u) {
		synchronized (updatables) {
			updatables.remove(u);
		}
	}

	public void update(double dt) {
		synchronized (updatables) {
			for (Updatable u : updatables) {
				u.update(dt);
			}
		}

		PerformanceMonitor m = PerformanceMonitor.getInstance();
		if (m.isEnabled()) {
			m.recordStatistic("CountUpdatables", updatables.size());

			int countDr = 0;
			synchronized (drawables) {
				for (Map.Entry<Integer, ArrayList<Drawable>> dr : drawables.entrySet()) {
					countDr += dr.getValue().size();
				}
			}
			m.recordStatistic("CountDrawables", countDr);
		}
	}

	public void draw(Graphics2D g) {
		synchronized (drawables) {
			for (Map.Entry<Integer, ArrayList<Drawable>> dr : drawables.entrySet()) {
				for (Drawable d : dr.getValue()) {
					d.draw(g);
				}
			}
		}
	}

	public void onGameOver(GameState gameState) {
		drawables.clear();
		updatables.clear();
		GameOverView gameOver = new GameOverView(gameState);
		addDrawable(gameOver, Constants.Z_OVERLAY);
		addUpdatable(gameOver);
	}
}