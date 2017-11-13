package multiplayer;

import java.util.LinkedList;

public class LocalPipe implements Pipe {

	private LinkedList<Message> queue;

	public LocalPipe() {
		queue = new LinkedList<>();
	}

	@Override
	public void scheduleMessageWrite(Message message) {
		queue.add(message);
	}

	@Override
	public void writeScheduledMessages() {
		
	}

	@Override
	public boolean hasMessages() {
		return !queue.isEmpty();
	}

	@Override
	public Message readMessage() {
		Message message = queue.getFirst();
		queue.pop();
		return message;
	}
}
