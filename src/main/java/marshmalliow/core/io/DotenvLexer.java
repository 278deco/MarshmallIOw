package marshmalliow.core.io;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

import marshmalliow.core.exceptions.DotenvParseException;
import marshmalliow.core.file.dotenv.DotenvToken;
import marshmalliow.core.file.dotenv.DotenvTokenEnum;

/**
 * DotenvLexer purpose is to read a .env file and produce {@link DotenvToken}, checking for syntaxes errors.<br/>
 * <em>See dotenv format specifications</em>
 * @see DotenvParser
 * @author 278deco
 * @version 1.0.0
 */
public class DotenvLexer {

private static final int BUFFER_SIZE = 1024;
	
	private char[] buffer = new char[BUFFER_SIZE];
	private int bufferIndex = 0;
	
	private int counter = 0;
	
	private Reader inputSource;
	
	public DotenvLexer(Reader source) {
		this.inputSource = Objects.requireNonNull(source);
	}
	
	/**
	 * Increase the buffer reading position (index).<br/>
	 * If the reading position is greater than the buffer size, refilled the buffer with new data
	 * @param number The number to be added to the buffer reading position
	 * @return If the buffer has been refilled when the reading position increased
	 * @throws DotenvParseException
	 */
	private boolean incBuffer(int number) throws DotenvParseException {
		this.bufferIndex += number;
		
		if(this.bufferIndex >= buffer.length) {
			fillBuffer();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Fill the reading buffer with new data from the reader.<br/>
	 * This method updated the {@link #buffer} and {@link #bufferIndex} variables.
	 * @return The number of character refilled in the buffer
	 * @throws DotenvParseException
	 */
	private int fillBuffer() throws DotenvParseException {
		char[] nbuffer = new char[BUFFER_SIZE];
		this.bufferIndex = 0;
		
		int n;
		try {
			n = inputSource.read(nbuffer, 0, nbuffer.length);
		} catch (IOException e) {
			throw new DotenvParseException("Unexpected exception while filling the buffer",e);
		}

		counter = this.bufferIndex;
		if(n >0) this.counter+=n;

		this.buffer = nbuffer;
		
		return n;
	}
	
	/**
	 * Get the next {@link DotenvToken} depending on the character encountered in the {@link #buffer}.<br/>
	 * If the buffer is empty, a {@link DotenvTokenEnum#EOF} is returned to acknowledge the end of the reading. 
	 * This method always return EOF when the end of the file has been reached, no taking into matter the number of calls.<br/><br/>
	 * Internally, this method loop until a proper {@link DotenvToken} is found, discarding all unwanted characters like {@code \n, \r, \t}...
	 * @return A DotenvToken read from the {@link #inputSource}
	 * @throws DotenvParseException
	 */
	public synchronized DotenvToken nextToken() throws DotenvParseException {
		if(counter == 0) fillBuffer();

		DotenvToken result = null;
		
		while (result == null) {
			if(bufferIndex >= counter) {
				result = new DotenvToken(DotenvTokenEnum.EOF);
			}else {
				final char readChar = buffer[this.bufferIndex];
				
				switch (readChar) {
					case ' ': break;
					case '\n': break;
					case '\r': break;
					case '\t': break;
					case '=':
						result = new DotenvToken(DotenvTokenEnum.KEY_VALUE_SEPARATOR);
						break;
					case '#':
						result = new DotenvToken(DotenvTokenEnum.COMMENT);
						break;
					default:
						result = tokenizeValue();
						if(result == null) throw new IllegalArgumentException("Unexpected value: " + buffer[this.bufferIndex]);
						break;
				}
				
				incBuffer(1);
			}
		}
		
		return result;
	}
	
	/**
	 * This method is used to insert the string data of a {@link DotenvTokenEnum#VALUE} into its {@link DotenvToken} instance.<br/>
	 * It read the {@link #buffer} and take all characters up to {@link DotenvTokenEnum#KEY_VALUE_SEPARATOR} or EOL as the data.
	 * @return A {@link DotenvToken} containing string data
	 * @throws DotenvParseException
	 */
	private DotenvToken tokenizeValue() throws DotenvParseException {
		if(inputSource == null) throw new DotenvParseException("Input source closed");
		
		char[] strbuff = new char[this.buffer.length];
		int strPos = 0;
		
		boolean strEnd = false;
		while(!strEnd && bufferIndex < counter) {
			
			//Check if we are at the end of the str value
			//With key value separator, we end the string directly, and rewing the buffer index to catch the token
			// With comment token, we do the same
			if (this.buffer[this.bufferIndex] == '#' || this.buffer[this.bufferIndex] == '=') {
				strEnd = this.buffer.length-1 > 0 && this.buffer[this.bufferIndex - 1] != '\\';
				if(strEnd) incBuffer(-1);
			}
			
			//With LF or CRLF, we check if the previous character is not a backslash (escaping char)
			else if (this.buffer[this.bufferIndex] == '\n' || this.buffer[this.bufferIndex] == '\r') {
				strEnd = this.buffer.length-1 > 0 && this.buffer[this.bufferIndex - 1] != '\\';
				
				//If the we are not a the end of the str, we increment the buffer to skip the LF or CRLF and remove the escaping char
				if(!strEnd && strPos > 2) {
					incBuffer(this.buffer[this.bufferIndex] == '\r' ? 2 : 1);
					strPos--;
				}
			}
			
			if(!strEnd) {				
				strbuff[strPos++] = this.buffer[this.bufferIndex];
				
				if(incBuffer(1)) {
					char[] nstrbuff = new char[strbuff.length+this.buffer.length];
					System.arraycopy(strbuff, 0, nstrbuff, 0, strbuff.length);
					strbuff = nstrbuff;
				}
			}		
		}
		
		return new DotenvToken(DotenvTokenEnum.VALUE, new String(strbuff, 0, strPos));
	}
	
}
