package multiplayer.messages;

import java.nio.ByteBuffer;

import model.Model;

public class ClientHelloMessage implements Message {
	private static final int MESSAGE_SIZE = 4;

	private byte[] username;
	
	public ClientHelloMessage() {	
	}
	
	public ClientHelloMessage(String username) {
		setUsername(username);
	}
	
	public ClientHelloMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}

	@Override
	public void serializeTo(ByteBuffer buffer) {
		buffer.putInt(username.length);
		buffer.put(username);
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		int length = buffer.getInt();
		username = new byte[length];
		buffer.get(username);
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE + username.length;
	}

	@Override
	public MessageType getType() {
		return MessageType.CLIENT_HELLO;
	}
	
	public String getUsername() {
		return new String(username);
	}
	
	public void setUsername(String username) {
		this.username = username.getBytes();
	}
}
