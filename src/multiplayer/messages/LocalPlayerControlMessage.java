package multiplayer.messages;

import java.nio.ByteBuffer;

import controller.Control;
import model.Model;
import model.PlayerSide;

public class LocalPlayerControlMessage extends PlayerControlMessage {
	private final static int MESSAGE_SIZE = 1;
	
	private PlayerSide side;
	
	public LocalPlayerControlMessage(Control control, PlayerSide side) {
		super(control);
		this.side = side;
	}
	
	public LocalPlayerControlMessage(Model model, ByteBuffer buffer) {
		super(model, buffer);
		deserializeFrom(model, buffer);
	}
	
	public LocalPlayerControlMessage() {
		
	}
	
	@Override
	public void serializeTo(ByteBuffer buffer) {
		super.serializeTo(buffer);
		buffer.put((byte)side.getNum());
	}
	
	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		super.deserializeFrom(model, buffer);
		side = buffer.get() == PlayerSide.LEFT_PLAYER.getNum() ? PlayerSide.LEFT_PLAYER : PlayerSide.RIGHT_PLAYER;
	}
	
	@Override
	public int getSerializedSize() {
		return super.getSerializedSize() + MESSAGE_SIZE;
	}

	public PlayerSide getSide() {
		return side;
	}

	public void setSide(PlayerSide side) {
		this.side = side;
	}
	
	
}
