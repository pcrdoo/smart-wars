package multiplayer;

public interface Pipe {
	public abstract void scheduleMessageWrite(Message message);
	public abstract void writeScheduledMessages();
	public abstract boolean hasMessages();
	public abstract Message readMessage();
}
