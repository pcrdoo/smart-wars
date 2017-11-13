package multiplayer.messages;

import java.nio.ByteBuffer;
import java.util.UUID;

import model.Model;
import model.PlayerSide;
import multiplayer.SerializationHelpers;

public class SideAssignmentMessage implements Message {
	private int MESSAGE_SIZE = 1 + 16 + 16;
	
	private PlayerSide clientSide;
	private UUID leftPlayerUuid, rightPlayerUuid;
	
	public SideAssignmentMessage() {
		
	}
	
	public SideAssignmentMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}
	
	public SideAssignmentMessage(PlayerSide clientSide, UUID leftPlayerUuid, UUID rightPlayerUuid) {
		this.clientSide = clientSide;
		this.leftPlayerUuid = leftPlayerUuid;
		this.rightPlayerUuid = rightPlayerUuid;
	}

	@Override
	public void serializeTo(ByteBuffer buffer) {
		buffer.put((byte)clientSide.getNum());
		SerializationHelpers.serializeUuid(buffer, leftPlayerUuid);
		SerializationHelpers.serializeUuid(buffer, rightPlayerUuid);
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		byte sideByte = buffer.get();
		switch (sideByte) {
		case 0x01: clientSide = PlayerSide.LEFT_PLAYER; break;
		case 0x02: clientSide = PlayerSide.RIGHT_PLAYER; break;
		}
		
		leftPlayerUuid = SerializationHelpers.deserializeUuid(buffer);
		rightPlayerUuid = SerializationHelpers.deserializeUuid(buffer);
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE;
	}

	@Override
	public MessageType getType() {
		return MessageType.SIDE_ASSIGNMENT;
	}

	public PlayerSide getClientSide() {
		return clientSide;
	}

	public void setClientSide(PlayerSide clientSide) {
		this.clientSide = clientSide;
	}

	public UUID getLeftPlayerUuid() {
		return leftPlayerUuid;
	}

	public void setLeftPlayerUuid(UUID leftPlayerUuid) {
		this.leftPlayerUuid = leftPlayerUuid;
	}

	public UUID getRightPlayerUuid() {
		return rightPlayerUuid;
	}

	public void setRightPlayerUuid(UUID rightPlayerUuid) {
		this.rightPlayerUuid = rightPlayerUuid;
	}
	
	
}
