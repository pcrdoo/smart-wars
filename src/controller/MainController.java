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
	}

	public void handleKeyDown(int keyCode) {
		System.out.println(keyCode);
		switch(keyCode) {
		case KeyEvent.VK_LEFT:
			model.getBottomPlayer().moveLeft();
			break;
		case KeyEvent.VK_RIGHT:
			model.getBottomPlayer().moveRight();
			break;
		}
	}

	public void handleKeyUp(int keyCode) {
		switch(keyCode) {
		case KeyEvent.VK_LEFT:
			model.getBottomPlayer().stop();
			break;
		case KeyEvent.VK_RIGHT:
			model.getBottomPlayer().stop();
			break;
		}
	}
}

