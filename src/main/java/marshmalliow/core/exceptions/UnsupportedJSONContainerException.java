package marshmalliow.core.exceptions;

public class UnsupportedJSONContainerException extends RuntimeException {

	private static final long serialVersionUID = -2721758176992069244L;
	
	public UnsupportedJSONContainerException() {
		super();
	}
	
	public UnsupportedJSONContainerException(String msg) {
		super(msg);
	}

	public UnsupportedJSONContainerException(String msg, Exception cause) {
		super(msg, cause);
	}
}
