package main;

public class ServerMain {

	public static void main(String[] args) {
		HeadlessGameFrame gf = new HeadlessGameFrame();
		while (true) {
			// This call will pause until  the game is finished, and a new one is restarted.
			// In case clients exit, HeadlessGameFrame will terminate the program using 
			// System.exit.
			gf.startGame();
		}
	}

}
