package multiplayer.messages;

import java.nio.ByteBuffer;

import model.Model;

public class NewGameStartingMessage implements Message {
	public NewGameStartingMessage() {

	}

	public NewGameStartingMessage(Model model, ByteBuffer buffer) {

	}

	@Override
	public void serializeTo(ByteBuffer buffer) {
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {

	}

	@Override
	public int getSerializedSize() {
		return 0;
	}

	@Override
	public MessageType getType() {
		return MessageType.NEW_GAME_STARTING;
	}

}
