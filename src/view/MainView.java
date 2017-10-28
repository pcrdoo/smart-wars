package view;
import java.awt.Graphics2D;
import java.util.ArrayList;

import main.Constants;
import model.Model;
import rafgfxlib.GameFrame;

public class MainView extends GameFrame {
	private BackdropView backdrop;
	private long lastUpdateTime;
	
	private ArrayList<Drawable> drawables;
	private ArrayList<Updatable> updatables;
	
	private Model model;
	
	public MainView() {
		super("Smart Wars", Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

		drawables = new ArrayList<>();
		updatables = new ArrayList<>();
	
		lastUpdateTime = System.nanoTime();
		backdrop = new BackdropView();
		drawables.add(backdrop);
		
		this.model = new Model();
		
		setUpdateRate(60);
		startThread();
	}

	@Override
	public void handleWindowInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleWindowDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Graphics2D g, int sw, int sh) {
		for (Drawable d : drawables) {
			d.draw(g);
		}
	}

	@Override
	public void update() {
		long currentTime = System.nanoTime();
		double dt = (currentTime - lastUpdateTime) / 1000000.0;
		for (Updatable u : updatables) {
			u.update(dt);
		}
		
		lastUpdateTime = currentTime;
	}

	@Override
	public void handleMouseDown(int x, int y, GFMouseButton button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMouseUp(int x, int y, GFMouseButton button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMouseMove(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleKeyDown(int keyCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleKeyUp(int keyCode) {
		// TODO Auto-generated method stub
		
	}

}
