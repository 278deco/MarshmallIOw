package marshmalliow.core.json.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;

import marshmalliow.core.json.exceptions.JSONParseException;
import marshmalliow.core.json.objects.JSONToken;
import marshmalliow.core.json.utils.JSONTokenEnum;

public class JSONLexer {
	
	/**
	 * State machine
	 */
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
	 * Increase the buffer reading position (index).</br>
	 * If the reading position is greater than the buffer size, refilled the buffer with new data
	 * @param number the number to be added to the buffer reading position
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
	
	private int fillBuffer() throws JSONParseException {
		char[] nbuffer = new char[BUFFER_SIZE];
		this.bufferIndex = 0;
		
		int n;
		try {
			n = inputSource.read(nbuffer, 0, nbuffer.length);
		} catch (IOException e) {
			throw new JSONParseException();
		}

		counter = this.bufferIndex;
		if(n >0) this.counter+=n;

		this.buffer = nbuffer;
		
		return n;
	}
	
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
						if(result == null) throw new IllegalArgumentException("Unexpected value: " + buffer[bufferIndex]);
						break;
				}
				
				incBuffer(1);
			}
		}
		
		return result;
	}
	
	private JSONToken tokenizeString() throws JSONParseException {
		if(inputSource == null) throw new JSONParseException("Input source closed");
		
		char[] strbuff = new char[this.buffer.length];
		int strPos = 0;
		
		incBuffer(1); //Skip the starting quotation mark
		
		while(this.buffer[this.bufferIndex] != '\"') {
			strbuff[strPos++] = this.buffer[this.bufferIndex];
			
			if(incBuffer(1)) {
				char[] nstrbuff = new char[strbuff.length+this.buffer.length];
				System.arraycopy(strbuff, 0, nstrbuff, 0, strbuff.length);
				strbuff = nstrbuff;
			}
		}
		
		return new JSONToken(JSONTokenEnum.VALUE_STRING, new String(strbuff, 0, strPos));
	}

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
	
	private JSONToken tokenizeNumbers(char readChar) throws JSONParseException {
		if(inputSource == null) throw new JSONParseException("Input source closed");
		
		char[] strbuff = new char[this.buffer.length];
		int strPos = 0;
	
		boolean doubleCast = false;
		
		char c;
		while(isValidNumber((c = buffer[bufferIndex]))) {
			strbuff[strPos++] = c;
			
			if(c == '.' || c == 'e' || c == 'E') doubleCast = true;
			
			if(incBuffer(1)) {
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
	
	private JSONToken convertFloatingNumberToToken(String strRepr) throws JSONParseException {
		final Double value = Double.parseDouble(strRepr);
		if(value.isNaN() || value.isInfinite()) throw new JSONParseException("NaN or Infinity value founded");
		
		return (value > Float.MAX_VALUE) ? new JSONToken(JSONTokenEnum.VALUE_DOUBLE, value) : new JSONToken(JSONTokenEnum.VALUE_FLOAT, value.floatValue());
	}
	
	private JSONToken convertNumberToToken(String strRepr) {
		final Long value = Long.parseLong(strRepr);
		
		return (value > Integer.MAX_VALUE) ? new JSONToken(JSONTokenEnum.VALUE_LONG, value) : new JSONToken(JSONTokenEnum.VALUE_INTEGER, value.intValue());
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
