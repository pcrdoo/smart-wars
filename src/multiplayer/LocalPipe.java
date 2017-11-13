package multiplayer;

import java.util.LinkedList;

import model.Model;
import multiplayer.messages.Message;

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
	public Message readMessage(Model model) {
		Message message = queue.getFirst();
		queue.pop();
		return message;
	}
}
