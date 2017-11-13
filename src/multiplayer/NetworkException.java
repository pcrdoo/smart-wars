package multiplayer;

public class NetworkException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NetworkException(String message) {
		super(message);
	}
}
