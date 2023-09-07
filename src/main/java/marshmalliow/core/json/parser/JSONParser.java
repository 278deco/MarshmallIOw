package marshmalliow.core.json.parser;

import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.json.objects.JSONToken;
import marshmalliow.core.json.utils.JSONTokenEnum;

public class JSONParser {

	/**
	 * State machine
	 */
	private static final int INIT = 0;
	private static final int BEGIN_OBJ = 1;
	private static final int END_OBJ = 2;
	private static final int BEGIN_ARR = 3;
	private static final int END_ARR = 4;
	private static final int VALUE_NAME = 5;
//	private static final int VALUE_NAME_SEPARATOR = 6; // colon separation
	private static final int VALUE = 7;
	private static final int VALUE_SEPARATOR = 8; // comma separation
	private static final int EOF = 10;
	
	private JSONLexer lexer;
	private volatile int state = INIT;
	
	public JSONParser(JSONLexer lexer) {
		this.lexer = lexer;
	}

	public synchronized JSONContainer parse() throws JSONParseException {
		final JSONToken firstToken = lexer.nextToken(); //determine if the json is an object or an array 
		
		switch (firstToken.getType() ) {
		case LEFT_BRACE:
			state = BEGIN_OBJ;
			
			try {
				return parseObject(0);
			}finally {
				state = EOF;
			}
		case LEFT_BRACKET:
			state = BEGIN_ARR;
			
			try {
				return parseArray(0);
			}finally {
				state = EOF;
			}
		default:
			throw new JSONParseException();
		}
	}
	
	private JSONObject parseObject(int depth) throws JSONParseException {
		final JSONObject obj = new JSONObject();
		
		JSONToken token;
		String lastTokenName = null;
		do {
			token = lexer.nextToken();
			switch (token.getType()) {
				case LEFT_BRACE:
					if(state == VALUE_NAME) {
						state = BEGIN_OBJ;
						if(lastTokenName != null) obj.put(lastTokenName, parseObject(depth+1));
						lastTokenName = null;
					}
					break;
				case LEFT_BRACKET:
					if(state == VALUE_NAME) {
						state = BEGIN_ARR;
						if(lastTokenName != null) obj.put(lastTokenName, parseArray(depth+1));
						lastTokenName = null;
					}
					break;
				case RIGHT_BRACE: //Handled by the while loop condition
				case KEY_VALUE_SEPARATOR: //Don't care about the separation
					break;
				case COMMA_SEPARATOR:
					if(state == VALUE || state == END_OBJ || state == END_ARR) state = VALUE_SEPARATOR;
					else throw new JSONParseException();
					
					break;
				case VALUE_STRING:
					if(state == VALUE_SEPARATOR || state == BEGIN_OBJ) {
						state = VALUE_NAME;
						lastTokenName = token.getDataAsString();
						break;
					}
				case VALUE_LONG:
				case VALUE_INTEGER:
				case VALUE_DOUBLE:
				case VALUE_FLOAT:
				case VALUE_TRUE:
				case VALUE_FALSE:
				case VALUE_NULL:
					if(state == VALUE_NAME) {
						state = VALUE;
						if(lastTokenName != null) obj.put(lastTokenName, token.getDataParsed());
						lastTokenName = null;
					}
					break;
				default:
					throw new JSONParseException();
			}
			
		}while(token.getType() != JSONTokenEnum.RIGHT_BRACE);
		
		state = END_OBJ;
		
		return obj;
	}
	
	private JSONArray parseArray(int depth) throws JSONParseException {
		final JSONArray arr = new JSONArray();
		
		JSONTokenEnum arrayType = null;
		JSONToken token;
		do {
			token = lexer.nextToken();
			switch (token.getType()) {
				case LEFT_BRACE:
					if(state == VALUE_SEPARATOR || state == BEGIN_ARR) {
						state = BEGIN_OBJ;
						arr.add(parseObject(depth+1));
					}
					break;
				case LEFT_BRACKET:
					if(state == VALUE_SEPARATOR || state == BEGIN_ARR) {
						state = BEGIN_ARR;
						arr.add(parseArray(depth+1));
					}
					break;
				case RIGHT_BRACKET: //Handled by the while loop condition
				case KEY_VALUE_SEPARATOR: //Don't care about the separation
					break;
				case COMMA_SEPARATOR:
					if(state == VALUE || state == END_OBJ || state == END_ARR) state = VALUE_SEPARATOR;
					else throw new JSONParseException();
					break;
				case VALUE_STRING:
				case VALUE_LONG:
				case VALUE_INTEGER:
				case VALUE_DOUBLE:
				case VALUE_FLOAT:
				case VALUE_TRUE:
				case VALUE_FALSE:
				case VALUE_NULL:
					if(state == BEGIN_ARR || state == VALUE_SEPARATOR) {
						if(arrayType == null || arrayType == token.getType()) {
							state = VALUE;
							arrayType = token.getType();
							arr.add(token.getDataParsed());
						}else throw new JSONParseException();
					}
					break;
				default:
					throw new JSONParseException();
			}
			
		}while(token.getType() != JSONTokenEnum.RIGHT_BRACKET);
		
		state = END_ARR;
		
		return arr;
	}
	
	public synchronized void setLexer(JSONLexer lexer) {
		this.lexer = lexer;
	}
	
}
