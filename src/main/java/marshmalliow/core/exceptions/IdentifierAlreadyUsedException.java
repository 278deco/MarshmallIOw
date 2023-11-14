package marshmalliow.core.exceptions;

public class IdentifierAlreadyUsedException extends RuntimeException {

	private static final long serialVersionUID = 3873636067036982904L;

	public IdentifierAlreadyUsedException() { }
	
	public IdentifierAlreadyUsedException(String msg) {
		super(msg);
	}
	
}
