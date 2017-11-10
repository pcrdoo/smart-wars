package view;

import java.awt.Graphics2D;
import java.util.ArrayList;

import main.GameState;

public class MainView {
	private BackdropView backdrop;

	private ArrayList<Drawable> drawables;
	private ArrayList<Updatable> updatables;

	public MainView() {
		drawables = new ArrayList<>();
		updatables = new ArrayList<>();

		backdrop = new BackdropView();
		drawables.add(backdrop);
		updatables.add(backdrop);
	}

	public void addDrawable(Drawable d) {
		drawables.add(d);
	}

	public void removeDrawable(Drawable d) {
		drawables.remove(d);
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
			for (Drawable d : drawables) {
				d.draw(g);
			}
		}
	}

	public void onGameOver(GameState gameState) {
		drawables.clear();
		updatables.clear();
		drawables.add(backdrop);
		updatables.add(backdrop);
		drawables.add(new GameOverView(gameState));
	}
}