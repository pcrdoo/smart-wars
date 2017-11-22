package multiplayer.messages;

import java.nio.ByteBuffer;

import model.Model;

public class NewGameStartingMessage implements Message {
	private final static int MESSAGE_SIZE = 4 + 4;
	
	private byte[] leftPlayerUsername, rightPlayerUsername;
	
	public NewGameStartingMessage(String leftPlayerUsername, String rightPlayerUsername) {
		setPlayerUsernames(leftPlayerUsername, rightPlayerUsername);
	}

	public NewGameStartingMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}

	@Override
	public void serializeTo(ByteBuffer buffer) {
		buffer.putInt(leftPlayerUsername.length);
		buffer.put(leftPlayerUsername);
		
		buffer.putInt(rightPlayerUsername.length);
		buffer.put(rightPlayerUsername);
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		int leftLength = buffer.getInt();
		leftPlayerUsername = new byte[leftLength];
		buffer.get(leftPlayerUsername);
		
		int rightLength = buffer.getInt();
		rightPlayerUsername = new byte[rightLength];
		buffer.get(rightPlayerUsername);
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE + leftPlayerUsername.length + rightPlayerUsername.length;
	}

	@Override
	public MessageType getType() {
		return MessageType.NEW_GAME_STARTING;
	}

	public void setPlayerUsernames(String left, String right) {
		leftPlayerUsername = left.getBytes();
		rightPlayerUsername = right.getBytes();
	}
	
	public String getLeftPlayerUsername() {
		return new String(leftPlayerUsername);
	}
	
	public String getRightPlayerUsername() {
		return new String(rightPlayerUsername);
	}
}
