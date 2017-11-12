package multiplayer;

import java.util.ArrayList;

public class OpenPipes {

	private ArrayList<NetworkPipe> pipes;
	private static OpenPipes instance;
	private Side side;

	protected OpenPipes() {
		pipes = new ArrayList<>();
	}
	
	public void setSide(Side side) {
		this.side = side;
	}
	
	public void addPipe(NetworkPipe pipe) {
		pipes.add(pipe);
	}
	
	public ArrayList<NetworkPipe> getPipes() {
		return pipes;
	}
	
	public static OpenPipes getInstance() {
		if (instance == null) {
			instance = new OpenPipes();
		}
		return instance;
	}

	public void scheduleMessageWriteToAll(Message message) {
		for(NetworkPipe pipe: pipes) {
			pipe.scheduleMessageWrite(message);
		}
	}
	
	public void writeScheduledMessagesOnAll() {
		for(NetworkPipe pipe: pipes) {
			pipe.writeScheduledMessages();
		}
	}

	public boolean isServer() {
		return side == Side.SERVER;
	}

}
