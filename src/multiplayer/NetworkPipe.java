package multiplayer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import model.Model;
import multiplayer.messages.AddEntityMessage;
import multiplayer.messages.AsteroidHitMessage;
import multiplayer.messages.DisintegrateAsteroidMessage;
import multiplayer.messages.GameOverMessage;
import multiplayer.messages.Message;
import multiplayer.messages.MessageType;
import multiplayer.messages.MirrorBounceMessage;
import multiplayer.messages.NewGameStartingMessage;
import multiplayer.messages.PlayerControlMessage;
import multiplayer.messages.PlayerHitMessage;
import multiplayer.messages.PositionSyncMessage;
import multiplayer.messages.RemoveEntityMessage;
import multiplayer.messages.SideAssignmentMessage;
import multiplayer.messages.UpdateEntityMessage;

public class NetworkPipe implements Pipe {

	private InputStream inputStream;
	private OutputStream outputStream;
	private ArrayList<Message> buffer;
	private Thread writerThread;
	private NetworkException pipeException;

	public void writerThreadWorker() throws NetworkException {
		while (pipeException == null) {
			synchronized (buffer) {
				try {
					buffer.wait();

					int fullSize = 0;
					for (Message message : buffer) {
						fullSize += message.getSerializedSize() + 1 /* for type */ + 4 /* for length */;
					}

					byte[] bytes = new byte[fullSize];
					ByteBuffer buf = ByteBuffer.wrap(bytes);
					for (Message message : buffer) {
						buf.put((byte) message.getType().getNum());
						buf.putInt(message.getSerializedSize());
						message.serializeTo(buf);
					}

					buffer.clear();
					outputStream.write(bytes, 0, bytes.length);

				} catch (IOException e) {
					throw new NetworkException("IOException in writerThread: " + e.getMessage());
				} catch (InterruptedException e) {
					throw new NetworkException("InterruptedException in writerThread: " + e.getMessage());
				}
			}
		}
	}
	
	private void checkWriterThreadErrors() throws NetworkException {
		synchronized (this) {
			if (pipeException != null) {
				throw pipeException;
			}
		}
	}
	
	public NetworkPipe(Socket socket) throws NetworkException {
		try {
			inputStream = new BufferedInputStream(socket.getInputStream());
			outputStream = socket.getOutputStream();
			buffer = new ArrayList<>();
		} catch (IOException e) {
			throw new NetworkException("IOException in initialize: " + e.getMessage());
		}

		NetworkPipe self = this;
		pipeException = null;
		writerThread = new Thread(() -> {
			try {
				writerThreadWorker();
			} catch (NetworkException e) {
				synchronized (self) {
					pipeException = e;
				}
			}
		});

		writerThread.start();
	}

	@Override
	public void scheduleMessageWrite(Message message) throws NetworkException {
		checkWriterThreadErrors();
		
		synchronized (buffer) {
			buffer.add(message);
		}
	}

	@Override
	public void writeScheduledMessages() throws NetworkException {
		checkWriterThreadErrors();
		
		synchronized (buffer) {
			buffer.notify();
		}
	}

	@Override
	public boolean hasMessages() throws NetworkException {
		checkWriterThreadErrors();
		
		try {
			return inputStream.available() > 0;
		} catch (IOException e) {
			throw new NetworkException("IOException in hasMessages: " + e.getMessage());
		}
	}

	private Message deserializeMessage(Model model, MessageType type, ByteBuffer buffer) {
		switch (type) {
		case ENTITY_ADDED:
			return new AddEntityMessage(model, buffer);
		case ENTITY_UPDATED:
			return new UpdateEntityMessage(model, buffer);
		case ENTITY_REMOVED:
			return new RemoveEntityMessage(model, buffer);
		case GAME_OVER:
			return new GameOverMessage(model, buffer);
		case NEW_GAME_STARTING:
			return new NewGameStartingMessage(model, buffer);
		case POSITION_SYNC:
			return new PositionSyncMessage(model, buffer);
		case SIDE_ASSIGNMENT:
			return new SideAssignmentMessage(model, buffer);
		case PLAYER_CONTROL:
			return new PlayerControlMessage(model, buffer);
		case VIEW_PLAYER_HIT:
			return new PlayerHitMessage(model, buffer);
		case VIEW_BULLET_ASTEROID_HIT:
			return new AsteroidHitMessage(model, buffer);
		case VIEW_MIRROR_BOUNCE:
			return new MirrorBounceMessage(model, buffer);
		case VIEW_DISINTEGRATE_ASTEROID:
			return new DisintegrateAsteroidMessage(model, buffer);
		}

		System.err.println("Unrecognized message type: " + type);
		return null;
	}

	@Override
	public Message readMessage(Model model) throws NetworkException {
		try {
			byte[] header = new byte[1 + 4];
			inputStream.read(header, 0, 1 + 4);
			ByteBuffer buf = ByteBuffer.wrap(header);

			byte typeByte = buf.get();
			int length = buf.getInt();

			byte[] messageBytes = new byte[length];
			inputStream.read(messageBytes, 0, length);

			buf = ByteBuffer.wrap(messageBytes);
			return deserializeMessage(model, MessageType.fromNum(typeByte), buf);
		} catch (IOException e) {
			throw new NetworkException("IOException in readMessage: " + e.getMessage());
		}
	}
}
