package marshmalliow.core.io;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;

import marshmalliow.core.exceptions.JSONParseException;
import marshmalliow.core.json.objects.JSONToken;
import marshmalliow.core.json.utils.JSONTokenEnum;

/**
 * JSONLexer purpose is to read a JSON file and produce {@link JSONToken}, checking for syntaxes errors.<br/>
 * <em>See RFC 4627 and RFC 8259.</em>
 * @see JSONParser
 * @author 278deco
 * @version 1.0.1
 */
public class JSONLexer {

	private static final char[] TRUE_PATTERN = {'t','r','u','e'};
	private static final char[] FALSE_PATTERN = {'f','a','l','s','e'};
	private static final char[] NULL_PATTERN = {'n','u','l','l'};
	
	private static final int BUFFER_SIZE = 1024;
	
	private char[] buffer = new char[BUFFER_SIZE];
	private int bufferIndex = 0;
	
	private int counter = 0;
	
	private Reader inputSource;
	
	public JSONLexer(Reader source) {
		this.inputSource = Objects.requireNonNull(source);
	}
	
	/**
	 * Increase the buffer reading position (index).<br/>
	 * If the reading position is greater than the buffer size, refilled the buffer with new data
	 * @param number The number to be added to the buffer reading position
	 * @return If the buffer has been refilled when the reading position increased
	 * @throws IOException
	 */
	private boolean incBuffer(int number) throws JSONParseException {
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
	 * @throws JSONParseException
	 */
	private int fillBuffer() throws JSONParseException {
		char[] nbuffer = new char[BUFFER_SIZE];
		this.bufferIndex = 0;
		
		int n;
		try {
			n = inputSource.read(nbuffer, 0, nbuffer.length);
		} catch (IOException e) {
			throw new JSONParseException("Unexpected exception while filling the buffer",e);
		}

		counter = this.bufferIndex;
		if(n >0) this.counter+=n;

		this.buffer = nbuffer;
		
		return n;
	}
	
	/**
	 * Get the next {@link JSONToken} depending on the character encountered in the {@link #buffer}.<br/>
	 * If the buffer is empty, a {@link JSONTokenEnum#EOF} is returned to acknowledge the end of the reading. 
	 * This method always return EOF when the end of the file has been reached, no taking into matter the number of calls.<br/><br/>
	 * Internally, this method loop until a proper {@link JSONToken} is found, discarding all unwanted characters like {@code \n, \r, \t}...
	 * @return A JSONToken read from the {@link #inputSource}
	 * @throws JSONParseException
	 */
	public synchronized JSONToken nextToken() throws JSONParseException {
		if(counter == 0) fillBuffer();

		JSONToken result = null;
		
		while (result == null) {
			if(bufferIndex >= counter) {
				result = new JSONToken(JSONTokenEnum.EOF);
			}else {
				final char readChar = buffer[this.bufferIndex];
				
				switch (readChar) {
					case ' ': break;
					case '\n': break;
					case '\r': break;
					case '\t': break;
					case ':':
						result = new JSONToken(JSONTokenEnum.KEY_VALUE_SEPARATOR);
						break;
					case '{':
						result = new JSONToken(JSONTokenEnum.LEFT_BRACE);
						break;
					case '}':
						result = new JSONToken(JSONTokenEnum.RIGHT_BRACE);
						break;
					case '[':
						result = new JSONToken(JSONTokenEnum.LEFT_BRACKET);
						break;
					case ']':
						result = new JSONToken(JSONTokenEnum.RIGHT_BRACKET);
						break;
					case ',':
						result = new JSONToken(JSONTokenEnum.COMMA_SEPARATOR);
						break;
					case '\"':
						result = tokenizeString();
						break;
					case 't':
					case 'f':
					case 'n':
						result = tokenizeSpecialKeywords(readChar);
						break;
					default:
						result = tokenizeNumbers(readChar);
						if(result == null) throw new IllegalArgumentException("Unexpected value: " + buffer[this.bufferIndex]);
						break;
				}
				
				incBuffer(1);
			}
		}
		
		return result;
	}
	
	/**
	 * This method is used to insert the string data of a {@link JSONTokenEnum#VALUE_STRING} into its {@link JSONToken} instance.<br/>
	 * It read the {@link #buffer} and take all characters between two quotations marks as the string data.
	 * @return A {@link JSONToken} containing string data
	 * @throws JSONParseException
	 */
	private JSONToken tokenizeString() throws JSONParseException {
		if(inputSource == null) throw new JSONParseException("Input source closed");
		
		char[] strbuff = new char[this.buffer.length];
		int strPos = 0;
		
		incBuffer(1); //Skip the starting quotation mark
		
		//If the string is empty return immediately
		if(this.buffer[this.bufferIndex] == '\"') {
			return new JSONToken(JSONTokenEnum.VALUE_STRING, "");
		}
		
		boolean strEnd = false;
		while(!strEnd) {
			strbuff[strPos++] = this.buffer[this.bufferIndex];
			
			if(incBuffer(1)) {
				char[] nstrbuff = new char[strbuff.length+this.buffer.length];
				System.arraycopy(strbuff, 0, nstrbuff, 0, strbuff.length);
				strbuff = nstrbuff;
			}
			
			strEnd = this.buffer[this.bufferIndex] == '\"' && this.buffer.length-1 == 0;
			if(!strEnd) strEnd = this.buffer[this.bufferIndex] == '\"' && this.buffer[this.bufferIndex-1] != '\\';
		}
		
		return new JSONToken(JSONTokenEnum.VALUE_STRING, new String(strbuff, 0, strPos));
	}
	
	/**
	 * This method is used to create special {@link JSONToken} like {@link JSONTokenEnum#VALUE_TRUE}, {@link JSONTokenEnum#VALUE_FALSE} and {@link JSONTokenEnum#VALUE_NULL}.
	 * @param startingChar The starting character, meaning the character read from the buffer how caused the invocation this method.
	 * @return A {@link JSONToken} containing {@code true, false, null} data
	 * @throws JSONParseException
	 */
	private JSONToken tokenizeSpecialKeywords(char startingChar) throws JSONParseException {
		if(inputSource == null) throw new JSONParseException("Input source closed");

		final char[] temp = new char[startingChar == 'f' ? 5 : 4];
		
		for(int i = 0; i < temp.length; i++) {
			temp[i] = this.buffer[bufferIndex];
			incBuffer(1);
		}
		
		if(startingChar == 't' && Arrays.equals(TRUE_PATTERN, 0, TRUE_PATTERN.length, temp, 0, TRUE_PATTERN.length)) {
			return new JSONToken(JSONTokenEnum.VALUE_TRUE);
		}else if(startingChar == 'f' && Arrays.equals(FALSE_PATTERN, 0, FALSE_PATTERN.length, temp, 0, FALSE_PATTERN.length)) {
			return new JSONToken(JSONTokenEnum.VALUE_FALSE);
		}else if(startingChar == 'n' && Arrays.equals(NULL_PATTERN, 0, NULL_PATTERN.length, temp, 0, NULL_PATTERN.length)) {
			return new JSONToken(JSONTokenEnum.VALUE_NULL);
		}else {
			throw new JSONParseException();
		}
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private JSONToken tokenizeNumbersOld(char readChar) throws JSONParseException {
		if(inputSource == null) throw new JSONParseException("Input source closed");
		
		boolean negative = readChar == '-'; 
		if(negative) incBuffer(1); //If negative we skip the sign

		boolean exponent = false;
		boolean negativeExp = false;
		
		boolean floatingPoint = false;
		
		long value = 0;
		long expValue = 0;
		
		char c;
		while(isValidNumber((c = buffer[bufferIndex]))) {
			switch (c) {
			case 'e':
			case 'E':
				exponent = true;
				break;
			case '.':
				floatingPoint = true;
				break;
			case '+':
			case '-':
				if(!exponent) throw new JSONParseException();
				negativeExp = c == '-'; 
				break;
			default:
				if(exponent || floatingPoint) {
					if(floatingPoint) value = value + ((c - '0') / 10);
					else expValue = expValue * 10 + (c - '0');
				}else {
					value = value * 10 + (c - '0');
				}
				break;
			}
			
			incBuffer(1);
		}
		
		incBuffer(-1); //Hack
		
		final Number finalValue = (value*(negative ? -1 : 1))*(Math.pow(10, expValue*(negativeExp ? -1 : 1)));
		final long finalLongValue = finalValue.longValue();
		final double finalDoubleValue = finalValue.doubleValue();
		
		if(floatingPoint) {
			return finalDoubleValue > Float.MAX_VALUE ?
					new JSONToken(JSONTokenEnum.VALUE_DOUBLE, finalDoubleValue) :
					new JSONToken(JSONTokenEnum.VALUE_FLOAT, finalValue.floatValue());
		}else {
			return finalLongValue > Integer.MAX_VALUE ? 
					new JSONToken(JSONTokenEnum.VALUE_LONG, finalLongValue) : 
					new JSONToken(JSONTokenEnum.VALUE_INTEGER, finalValue.intValue());
		}
	}
	
	/**
	 * This method is used to create number {@link JSONToken} and deciding if it's either a {@link JSONTokenEnum#VALUE_DOUBLE}, {@link JSONTokenEnum#VALUE_FLOAT}, {@link JSONTokenEnum#VALUE_INTEGER} or {@link JSONTokenEnum#VALUE_LONG}.
	 * @param readChar The read character, meaning the character read from the buffer how caused the invocation this method.
	 * @return A {@link JSONToken} containing a number data
	 * @throws JSONParseException
	 */
	private JSONToken tokenizeNumbers(char readChar) throws JSONParseException {
		if(inputSource == null) throw new JSONParseException("Input source closed");
		
		char[] strbuff = new char[this.buffer.length];
		int strPos = 0;
	
		boolean doubleCast = false;
		
		char c;
		while(isValidNumber((c = buffer[bufferIndex]))) {
			strbuff[strPos++] = c;
			
			if(c == '.' || c == 'e' || c == 'E') doubleCast = true;
			
			if(incBuffer(1)) { //If the global buffer has been refilled, when need to expand the size of the strbuff containing the number data.
				char[] nstrbuff = new char[strbuff.length+this.buffer.length];
				System.arraycopy(strbuff, 0, nstrbuff, 0, strbuff.length);
				strbuff = nstrbuff;
			}
		}
		
		incBuffer(-1); //Remove the last character read so the switch can read it again
		
		if(doubleCast) {
			return  convertFloatingNumberToToken(new String(strbuff, 0, strPos));
		}else {
			return convertNumberToToken(new String(strbuff, 0, strPos));
		}
	}
	
	/**
	 * Convert a number data into a {@link JSONToken} containing a {@link JSONTokenEnum#VALUE_DOUBLE} or {@link JSONTokenEnum#VALUE_FLOAT}.
	 * @param strRepr The string representation of the number
	 * @return A {@link JSONToken} containing the parsed number data
	 * @throws JSONParseException
	 */
	private JSONToken convertFloatingNumberToToken(String strRepr) throws JSONParseException {
		final Double value = Double.parseDouble(strRepr);
		if(value.isNaN() || value.isInfinite()) throw new JSONParseException("NaN or Infinity value founded");
		
		return (value > Float.MAX_VALUE) ? new JSONToken(JSONTokenEnum.VALUE_DOUBLE, value) : new JSONToken(JSONTokenEnum.VALUE_FLOAT, value.floatValue());
	}
	
	/**
	 * Convert a number data into a {@link JSONToken} containing a {@link JSONTokenEnum#VALUE_LONG} or {@link JSONTokenEnum#VALUE_INTEGER}.
	 * @param strRepr The string representation of the number
	 * @return A {@link JSONToken} containing the parsed number data
	 * @throws JSONParseException
	 */
	private JSONToken convertNumberToToken(String strRepr) {
		try {
			final Long value = Long.parseLong(strRepr);
			
			return (value > Integer.MAX_VALUE) ? new JSONToken(JSONTokenEnum.VALUE_LONG, value) : new JSONToken(JSONTokenEnum.VALUE_INTEGER, value.intValue());
		}catch(NumberFormatException e) {
			return null;
		}
	}
	
	private boolean isValidNumber(char c) {
		switch (c) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'e':
			case 'E':
			case '.':
			case '+':
			case '-':
				return true;
			default:
				return false;
		}
	}

	public void close() throws IOException {
		inputSource.close();
		inputSource = null;
	}
	
	
	
}
