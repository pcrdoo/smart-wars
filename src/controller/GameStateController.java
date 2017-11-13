package controller;

import java.io.IOException;

import multiplayer.LocalPipe;

public abstract class GameStateController {
	public GameStateController() {
		
	}

	public abstract void update(double dt);
	
	// Blocks until all clients and server are ready.
	public abstract void setUpConnections() throws IOException;

	public abstract void setLocalPipe(LocalPipe pipe);
}
