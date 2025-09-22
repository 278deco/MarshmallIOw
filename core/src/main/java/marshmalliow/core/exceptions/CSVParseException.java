package marshmalliow.core.exceptions;

import java.io.IOException;

public class CSVParseException extends IOException {

	private static final long serialVersionUID = 3223167123639662713L;

	public CSVParseException() {
		super();
	}
	
	public CSVParseException(String msg) {
		super(msg);
	}
	
	public CSVParseException(String msg, Exception cause) {
		super(msg, cause);
	}
}
