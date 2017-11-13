package multiplayer.messages;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import model.Model;
import model.entitites.Entity;

public class PositionSyncMessage implements Message {
	private final static int MESSAGE_SIZE = 4;
	private ArrayList<UuidPosition> uuidPositions;

	public PositionSyncMessage(Collection<Entity> entities) {
		uuidPositions = new ArrayList<>();

		for (Entity e : entities) {
			uuidPositions.add(new UuidPosition(e.getUuid(), e.getPosition()));
		}
	}

	public PositionSyncMessage(Model model, ByteBuffer buffer) {
		deserializeFrom(model, buffer);
	}

	@Override
	public void serializeTo(ByteBuffer buffer) {
		buffer.putInt(uuidPositions.size());
		for (UuidPosition p : uuidPositions) {
			p.serializeTo(buffer);
		}
	}

	@Override
	public void deserializeFrom(Model model, ByteBuffer buffer) {
		uuidPositions = new ArrayList<>();
		int length = buffer.getInt();
		for (int i = 0; i < length; i++) {
			uuidPositions.add(new UuidPosition(model, buffer));
		}
	}

	@Override
	public int getSerializedSize() {
		return MESSAGE_SIZE + uuidPositions.size() * UuidPosition.MESSAGE_SIZE;
	}

	@Override
	public MessageType getType() {
		return MessageType.POSITION_SYNC;
	}

	public ArrayList<UuidPosition> getUuidPositions() {
		return uuidPositions;
	}

	public void setUuidPositions(ArrayList<UuidPosition> uuidPositions) {
		this.uuidPositions = uuidPositions;
	}
}
