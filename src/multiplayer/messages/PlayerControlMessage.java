package multiplayer.messages;

import java.nio.ByteBuffer;

import controller.Control;
import model.Model;

public class PlayerControlMessage implements Message {
	private final static int MESSAGE_SIZE = 1;
	
	private Control control;
	
	public PlayerControlMessage(Control control) {
		this.control = control;
	}
	
	public PlayerControlMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}
	
	public PlayerControlMessage() {
		
	}
	
	@Override
	public void serializeTo(ByteBuffer buffer) {
		buffer.put((byte)control.getNum());
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		control = Control.fromNum(buffer.get());
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE;
	}

	@Override
	public MessageType getType() {
		return MessageType.PLAYER_CONTROL;
	}

	public Control getControl() {
		return control;
	}

	public void setControl(Control control) {
		this.control = control;
	}
}
