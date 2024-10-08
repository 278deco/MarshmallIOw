package marshmalliow.core.exceptions;

import java.io.IOException;

public class JSONParseException extends IOException {

	private static final long serialVersionUID = 3223167123639662713L;

	public JSONParseException() {
		super();
	}
	
	public JSONParseException(String msg) {
		super(msg);
	}
	
	public JSONParseException(String msg, Exception cause) {
		super(msg, cause);
	}
}
