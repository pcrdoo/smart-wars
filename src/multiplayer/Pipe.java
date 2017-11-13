package multiplayer;

import model.Model;
import multiplayer.messages.Message;

public interface Pipe {
	public abstract void scheduleMessageWrite(Message message);
	public abstract void writeScheduledMessages();
	public abstract boolean hasMessages();
	public abstract Message readMessage(Model model);
}
