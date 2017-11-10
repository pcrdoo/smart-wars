package view;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import main.GameState;
import main.Constants;

public class MainView {
	private BackdropView backdrop;

	private TreeMap<Integer, ArrayList<Drawable>> drawables;
	private ArrayList<Updatable> updatables;

	public MainView() {
		drawables = new TreeMap<>();
		updatables = new ArrayList<>();

		backdrop = new BackdropView();
		addDrawable(backdrop, Constants.Z_BACKDROP);
		updatables.add(backdrop);
	}

	public void addDrawable(Drawable d, int zIndex) {
		ArrayList<Drawable> drawablesInZIndex;
		if (!drawables.containsKey(zIndex)) {
			drawablesInZIndex = new ArrayList<>();
			drawables.put(zIndex, drawablesInZIndex);
			
		} else {
			drawablesInZIndex = drawables.get(zIndex);
		}
		
		drawablesInZIndex.add(d);
	}

	public void removeDrawable(Drawable d) {
		for (Map.Entry<Integer, ArrayList<Drawable>> dr : drawables.entrySet()) {
			dr.getValue().remove(d);
		}
	}

	public void addUpdatable(Updatable u) {
		updatables.add(u);
	}

	public void removeUpdatable(Updatable u) {
		updatables.remove(u);
	}

	public void update(double dt) {
		synchronized (this) {
			for (Updatable u : updatables) {
				u.update(dt);
			}
		}
	}

	public void draw(Graphics2D g) {
		synchronized (this) {
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
		addDrawable(backdrop, Constants.Z_BACKDROP);
		updatables.add(backdrop);
		addDrawable(new GameOverView(gameState), Constants.Z_OVERLAY);
	}
}