package multiplayer;

import java.util.Collection;
import java.util.LinkedList;

import multiplayer.messages.Message;

// Right now it might not make sense to just wrap a LinkedList but the implementation might change.
public class LocalMessageQueue {

	private LinkedList<Message> queue;

	public LocalMessageQueue() {
		queue = new LinkedList<>();
	}
	
	public void write(Message message) {
		queue.add(message);
	}
	
	public void writeAll(Collection<Message> collection) {
		queue.addAll(collection);
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public Message read() {
		Message message = isEmpty() ? null : queue.getFirst();
		queue.pop();
		return message;
	}
}
