package marshmalliow.core.exceptions;

public class JSONDepthException extends JSONParseException {

	private static final long serialVersionUID = 4260594875019189829L;

	public JSONDepthException() {
	}
	
	public JSONDepthException(String msg) {
		super(msg);
	}
	
}
