package marshmalliow.core.exceptions;

import marshmalliow.core.json.assertions.AssertionType;

public class JSONAssertionException extends RuntimeException {
	private static final long serialVersionUID = -2201471957430897144L;

	public JSONAssertionException(String msg) {
		super(msg);
	}

	public JSONAssertionException(String msg, String errorKey) {
		super(msg + " [JSON Key : " + errorKey + "]");
	}

	public JSONAssertionException(String msg, String errorKey, AssertionType type) {
		super(msg + " [JSON Key: " + errorKey + ", Assertion Error: " + type.toString() + "]");
	}

	public JSONAssertionException(String msg, AssertionType type) {
		super(msg + " Assertion Error: " + type.toString() + "]");
	}

	public JSONAssertionException(String msg, String errorKey, AssertionType type, Object objOrMsg) {
		super(msg + " [JSON Key: " + errorKey + ", Assertion Error: " + type.toString() + ", Required: "
				+ objOrMsg.toString() + "]");
	}

	public JSONAssertionException(String msg, AssertionType type, Object objOrMsg) {
		super(msg + " [Assertion Error: " + type.toString() + ", Required: " + objOrMsg.toString() + "]");
	}
}
