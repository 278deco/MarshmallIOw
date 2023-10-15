package marshmalliow.core.exceptions;

public class FileNotLoadedException extends RuntimeException {

	private static final long serialVersionUID = -3207547250422790320L;

	public FileNotLoadedException() {}
	
	public FileNotLoadedException(String msg) {
		super(msg);
	}
	
}
