package multiplayer;

import java.util.LinkedList;

public class LocalPipe implements Pipe {

	private LinkedList<Message> queue;

	public LocalPipe() {
		queue = new LinkedList<>();
	}

	@Override
	public void scheduleMessageWrite(Message message) {
		System.out.println("ADDED " + message.getType());
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
		System.out.println("READ " + message.getType());
		queue.pop();
		return message;
	}
}
