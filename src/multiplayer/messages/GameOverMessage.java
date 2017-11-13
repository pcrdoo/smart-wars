package multiplayer.messages;

import java.nio.ByteBuffer;

import main.GameState;
import model.Model;

public class GameOverMessage implements Message {
	private final static int MESSAGE_SIZE = 1;
	
	private GameState state;
	
	public GameOverMessage(GameState state) {
		this.state = state;
	}
	
	public GameOverMessage() {
		
	}
	
	public GameOverMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}
	
	@Override
	public void serializeTo(ByteBuffer buffer) {
		buffer.put((byte)state.getNum());
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		state = GameState.fromNum(buffer.get());
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE;
	}

	@Override
	public MessageType getType() {
		return MessageType.GAME_OVER;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	
}
