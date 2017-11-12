package multiplayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class NetworkPipe {

	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private ArrayList<Message> buffer;
	private Thread writerThread;

	public NetworkPipe(Socket socket) {
		try {
			inputStream = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			buffer = new ArrayList<>();
		} catch (IOException e) {
			throw new NetworkException("IOException in initialize: " + e.getMessage());
		}
		Thread writerThread = new Thread() {
			public void run() {
				while (true) {
					synchronized (buffer) {
						try {
							buffer.wait();
							for (Message message : buffer) {
								outputStream.writeObject(message);
							}
							buffer.clear();
						} catch (IOException e) {
							throw new NetworkException("IOException in writerThread: " + e.getMessage());
						} catch (InterruptedException e) {
							throw new NetworkException("InterruptedException in writerThread: " + e.getMessage());
						}
					}
				}
			}
		};
		writerThread.start();
	}

	public void scheduleMessageWrite(Message message) {
		synchronized (buffer) {
			buffer.add(message);
		}
	}

	public void writeScheduledMessages() {
		synchronized (buffer) {
			buffer.notify();
		}
	}

	public boolean hasMessages() {
		try {
			return inputStream.available() > 0;
		} catch (IOException e) {
			throw new NetworkException("IOException in hasMessages: " + e.getMessage());
		}
	}

	public Message readMessage() {
		try {
			return (Message) inputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new NetworkException("ClassNotFoundException in readMessage: " + e.getMessage());
		} catch (IOException e) {
			throw new NetworkException("IOException in readMessage: " + e.getMessage());
		}
	}
}
