package marshmalliow.core.exceptions;

public class TextFileInitializationException extends Exception {

	private static final long serialVersionUID = -2972718409482990007L;

	public TextFileInitializationException(String message) {
		super(message);
	}
	
	public TextFileInitializationException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
