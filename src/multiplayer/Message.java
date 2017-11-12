package multiplayer;

import java.util.ArrayList;

public class Message {
	private MessageType type;
	private ArrayList<Object> payload; // overhead?
	
	public Message(MessageType type) {
		this.type = type;
		payload = new ArrayList<>();
	}
	
	public void addToPayload(Object object) {
		payload.add(object);
	}
	
	public ArrayList<Object> getPayload() {
		return payload;
	}
	
	public MessageType getType() {
		return type;
	}

}
