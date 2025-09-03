package marshmalliow.core.exceptions;

public class MOBFInitializationException extends Exception {

	private static final long serialVersionUID = -1287188842732445178L;

	public MOBFInitializationException(String message) {
		super(message);
	}
	
	public MOBFInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

}
