package multiplayer.messages;

import java.nio.ByteBuffer;

import model.Model;

public interface Message {
	void serializeTo(ByteBuffer buffer);
	void deserializeFrom(Model model, ByteBuffer buffer);

	int getSerializedSize();

	MessageType getType();
}
