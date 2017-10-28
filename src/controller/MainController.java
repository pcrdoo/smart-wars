package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import model.Model;
import view.MainView;

public class MainController {
	
	private MainView view;
	private Model model;

//	private boolean wDown = false;
//	private boolean aDown = false;
//	private boolean dDown = false;
//
//	private boolean upDown = false;
//	private boolean leftDown = false;
//	private boolean rightDown = false;

	public MainController(MainView view, Model model) {
		this.view = view;
		this.model = model;
		
		this.view.addKeyListener(new SKeyListener());
	}
	
	class SKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				model.getBottomPlayer().moveLeft();
				break;
			case KeyEvent.VK_RIGHT:
				model.getBottomPlayer().moveRight();
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				model.getBottomPlayer().stop();
				break;
			case KeyEvent.VK_RIGHT:
				model.getBottomPlayer().stop();
				break;
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
}

