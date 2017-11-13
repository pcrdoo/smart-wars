package multiplayer;

import java.util.ArrayList;

import multiplayer.messages.Message;

public class OpenPipes {
	// Only server uses this.

	private ArrayList<Pipe> pipes;
	private static OpenPipes instance;

	protected OpenPipes() {
		pipes = new ArrayList<>();
	}

	public void addPipe(Pipe pipe) {
		pipes.add(pipe);
	}
	
	public ArrayList<Pipe> getPipes() {
		return pipes;
	}
	
	public static OpenPipes getInstance() {
		if (instance == null) {
			instance = new OpenPipes();
		}
		return instance;
	}

	public void scheduleMessageWriteToAll(Message message) {
		for(Pipe pipe: pipes) {
			pipe.scheduleMessageWrite(message);
		}
	}
	
	public void writeScheduledMessagesOnAll() {
		for(Pipe pipe: pipes) {
			pipe.writeScheduledMessages();
		}
	}

}
