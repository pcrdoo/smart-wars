package main;

public class Main {

	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "false");
		System.setProperty("sun.java2d.transaccel", "false");
		System.setProperty("sun.java2d.ddforcevram", "false");
		new view.MainView().initGameWindow();
	}

}
