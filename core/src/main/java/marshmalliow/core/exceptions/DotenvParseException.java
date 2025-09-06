package marshmalliow.core.exceptions;

import java.io.IOException;

public class DotenvParseException extends IOException {

	private static final long serialVersionUID = -5326530380671992112L;

	public DotenvParseException() {
		super();
	}
	
	public DotenvParseException(String msg) {
		super(msg);
	}
	
	public DotenvParseException(String msg, Exception cause) {
		super(msg, cause);
	}
}
