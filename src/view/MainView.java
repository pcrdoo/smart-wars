package view;

import java.awt.Graphics2D;

public abstract class MainView {
	public MainView() {

	}

	public abstract void update(double dt);
	public abstract void draw(Graphics2D g);
}