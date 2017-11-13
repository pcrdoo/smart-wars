package multiplayer;

import java.util.ArrayList;
import java.util.Arrays;

import model.Model;
import multiplayer.messages.Message;

public class LocalPipe implements Pipe {
    
	private LocalMessageQueue inputQueue;
	private LocalMessageQueue outputQueue;
	private ArrayList<Message> buffer;

	public LocalPipe(LocalMessageQueue inputQueue, LocalMessageQueue outputQueue) {
		this.inputQueue = inputQueue;
		this.outputQueue = outputQueue;
		buffer = new ArrayList<>();
	}

	@Override
	public void scheduleMessageWrite(Message message) {
		buffer.add(message);
	}

	@Override
	public void writeScheduledMessages() {
		outputQueue.writeAll(buffer);
		buffer.clear();
	}

	@Override
	public boolean hasMessages() {
		return !inputQueue.isEmpty();
	}

	@Override
	public Message readMessage(Model model) {
		return inputQueue.read();
	}
}
