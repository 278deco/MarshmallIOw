package marshmalliow.core.io;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

import marshmalliow.core.exceptions.CSVParseException;
import marshmalliow.core.file.csv.CSVProperties;
import marshmalliow.core.file.csv.CSVToken;
import marshmalliow.core.file.csv.CSVTokenEnum;

/**
 * CSVLexer purpose is to read a CSV file and produce {@link CSVToken}, checking for syntaxes errors.<br/>
 * <em>See RFC 4180.</em>
 * @see CSVParser
 * @author 278deco
 * @version 1.0.0
 */
public class CSVLexer {

	private static final int BUFFER_SIZE = 1024;
	private static final int REFLOW_BUFFER_SIZE = 10;
	
	private char[] buffer = new char[BUFFER_SIZE+REFLOW_BUFFER_SIZE];
	private int bufferIndex = 0;
	
	private int counter = 0;
	
	private Reader inputSource;
	private final CSVProperties props;
	
	public CSVLexer(Reader source, CSVProperties props) {
		this.inputSource = Objects.requireNonNull(source);
		this.props = Objects.requireNonNull(props);
	}
	
	/**
	 * Increase the buffer reading position (index).<br/>
	 * If the reading position is greater than the buffer size, refilled the buffer with new data<br/>
	 * If the reading position is lower than 0 (buffer lower bound), clamp it to 0.
	 * @param number The number to be added to the buffer reading position
	 * @return If the buffer has been refilled when the reading position increased
	 * @throws CSVParseException
	 */
	private boolean incBuffer(int number) throws CSVParseException {
		if(this.bufferIndex + number < 0) return false;
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
	 * @throws CSVParseException
	 */
	private int fillBuffer() throws CSVParseException {
		char[] nbuffer = new char[BUFFER_SIZE + REFLOW_BUFFER_SIZE];
		this.bufferIndex = REFLOW_BUFFER_SIZE;
		
		// The reflow properties leave a window at the start of the buffer for the last buffer's ending char.
		// This allow backward reading if needed.
		// Here we populate the starting of the new buffer with the last buffer values if present
		if(counter != 0) {
			for(int i = 0; i < REFLOW_BUFFER_SIZE; i++) {
				final int oldBufIdx = this.buffer.length - (REFLOW_BUFFER_SIZE + i);
				if(oldBufIdx < 0) break;
				nbuffer[i] = this.buffer[oldBufIdx];
			}
		}
		
		int n;
		try {
			n = inputSource.read(nbuffer, REFLOW_BUFFER_SIZE, BUFFER_SIZE);
		} catch (IOException e) {
			throw new CSVParseException("Unexpected exception while filling the buffer",e);
		}

		counter = this.bufferIndex;
		if(n >0) this.counter+=n;

		this.buffer = nbuffer;
		
		return n;
	}
	
	/**
	 * Get the next {@link CSVToken} depending on the character encountered in the {@link #buffer}.<br/>
	 * If the buffer is empty, a {@link CSVTokenEnum#EOF} is returned to acknowledge the end of the reading. 
	 * This method always return EOF when the end of the file has been reached, no taking into matter the number of calls.<br/><br/>
	 * Internally, this method loop until a proper {@link CSVToken} is found, discarding all unwanted characters like {@code \n, \r, \t}...
	 * @return A {@link CSVToken} read from the {@link #inputSource}
	 * @throws CSVParseException
	 */
	public synchronized CSVToken nextToken() throws CSVParseException {
		if(counter == 0) fillBuffer();

		CSVToken result = null;
		
		while (result == null) {
			if(bufferIndex >= counter) {
				result = new CSVToken(CSVTokenEnum.EOF);
			}else {
				final char readChar = buffer[this.bufferIndex];
								
				switch (readChar) {
				case ' ': break;
				case '\n': 
				case '\r':
					result = tokenizeLineBreak(readChar);
					break;
				case '\"':
					result = new CSVToken(CSVTokenEnum.FIELD_ESCAPING);
					break;
				default:
					if (readChar == props.getFieldSeparator()) {
						result = new CSVToken(CSVTokenEnum.FIELD_SEPARATOR);
					}else {
						result = tokenizeString();							
					}
					
					if(result == null) throw new IllegalArgumentException("Unexpected value: " + buffer[this.bufferIndex]);
					break;
				}
				
				incBuffer(1);
			}
		}

		return result;
	}
	
	/**
	 * This method is used to insert the string data of a {@link CSVTokenEnum#FIELD_VALUE} into its {@link CSVToken} instance.<br/>
	 * It read the {@link #buffer} and take all characters between two quotations marks as the string data.
	 * @return A {@link CSVToken} containing string data
	 * @throws CSVParseException
	 */
	private CSVToken tokenizeString() throws CSVParseException {
		if(inputSource == null) throw new CSVParseException("Input source closed");
		
		char[] strbuff = new char[this.buffer.length];
		int strPos = 0;
				
		boolean strEnd = false;
		boolean escapeQuotes = false;
		while(!strEnd) {
			char c = this.buffer[this.bufferIndex];
			
			if(c == '\"' && escapeQuotes) {
				strbuff[strPos++] = c;
				escapeQuotes = false;
			} else if (c == '\"' && !escapeQuotes) {
				escapeQuotes = true;
			} else if (c != '\"' && escapeQuotes) {
				strEnd = true;
				incBuffer(-1); // Put back the last read character because it's not part of the string
			}else {
				strEnd = (c == '\"' && !escapeQuotes) || (c != '\"' && escapeQuotes) || c == '\r' || c == '\n' || c == props.getFieldSeparator() || bufferIndex >= counter;
				
				if(!strEnd) strbuff[strPos++] = c;
			}
			
			if(!strEnd && incBuffer(1)) {
				char[] nstrbuff = new char[strbuff.length+this.buffer.length];
				System.arraycopy(strbuff, 0, nstrbuff, 0, strbuff.length);
				strbuff = nstrbuff;
			}
		}
		
		incBuffer(-1);
		
		return new CSVToken(CSVTokenEnum.FIELD_VALUE, new String(strbuff, 0, strPos));
	}
	
	private CSVToken tokenizeLineBreak(char startingChar) throws CSVParseException {
		if(inputSource == null) throw new CSVParseException("Input source closed");
		
		incBuffer(1);
		final char nextChar = buffer[this.bufferIndex];
		
		if(nextChar == '\n') {
			return new CSVToken(CSVTokenEnum.LINE_BREAK);
		}
		
		incBuffer(-1); // Remove last read character because it's not part of the line break
		
		return new CSVToken(CSVTokenEnum.LINE_BREAK);
	}
	
	public void close() throws IOException {
		inputSource.close();
		inputSource = null;
	}
	
	public final CSVProperties getProperties() {
		return props;
	}
	
}
