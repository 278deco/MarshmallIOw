package marshmalliow.core.binary.exceptions;

import java.io.IOException;
import java.util.Arrays;

public class MalformedHeaderException extends IOException {

	private static final long serialVersionUID = 867980986621269682L;

	public MalformedHeaderException() {

	}

	public MalformedHeaderException(String msg) {
		super(msg);
	}

	public MalformedHeaderException(String msg, byte expected, byte actual) {
		super(msg+". [Expected:"+expected+", Actual:"+actual+"]");
	}

	public MalformedHeaderException(String msg, int bytePosition, byte expected, byte received) {
		super(msg+". At position "+bytePosition+" [Expected:"+expected+", Actual:"+received+"]");
	}

	public MalformedHeaderException(String msg, byte[] expected, byte received) {
		super(msg+". [Expected:"+Arrays.toString(expected)+", Actual:"+received+"]");
	}

	public MalformedHeaderException(String msg, int bytePosition, byte[] expected, byte received) {
		super(msg+". At position "+bytePosition+" [Expected:"+Arrays.toString(expected)+", Actual:"+received+"]");
	}

	public MalformedHeaderException(String msg, byte expected, byte[] received) {
		super(msg+". [Expected:"+expected+", Actual:"+Arrays.toString(received)+"]");
	}

	public MalformedHeaderException(String msg, int bytePosition, byte expected, byte[] received) {
		super(msg+". At position "+bytePosition+" [Expected:"+expected+", Actual:"+Arrays.toString(received)+"]");
	}

	public MalformedHeaderException(String msg, byte[] expected, byte[] actual) {
		super(msg+". [Expected:"+Arrays.toString(expected)+", Actual:"+Arrays.toString(actual)+"]");
	}

	public MalformedHeaderException(String msg, int bytePosition, byte[] expected, byte[] received) {
		super(msg+". At position "+bytePosition+" [Expected:"+Arrays.toString(expected)+", Actual:"+Arrays.toString(received)+"]");
	}
}
