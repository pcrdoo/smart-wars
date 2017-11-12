package controller;

import java.io.IOException;

public abstract class GameStateController {
	public GameStateController() {
		
	}

	public abstract void update(double dt);
	
	// Blocks until all clients and server are ready.
	public abstract void setUpConnections() throws IOException;
}
