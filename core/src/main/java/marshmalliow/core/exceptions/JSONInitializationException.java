package marshmalliow.core.exceptions;

public class JSONInitializationException extends Exception {

	private static final long serialVersionUID = -9119228850857238778L;

	public JSONInitializationException(String message) {
		super(message);
	}
	
	public JSONInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

}
