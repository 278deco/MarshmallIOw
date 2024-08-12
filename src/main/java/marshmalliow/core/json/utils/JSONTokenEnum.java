package marshmalliow.core.json.utils;

public enum JSONTokenEnum {
	
	/**
	 * Left square bracket.
	 * Represents the beginning of an array.
	 */
	LEFT_BRACKET("["),
	
	/**
	 * Right square bracket.
	 * Represents the end of an array.
	 */
	RIGHT_BRACKET("]"),
	
	/**
	 * Left curly brace.
	 * Represents the beginning of an object.
	 */
	LEFT_BRACE("{"),
	
	/**
	 * Right curly brace. 
	 * Represents the end of an object.
	 */
	RIGHT_BRACE("}"),
	
	/**
	 * Colon separator.
	 * Separates a key from its value.
	 */
	KEY_VALUE_SEPARATOR(":"),
	
	/**
	 * Comma separator. 
	 * Separates values in an array or object.
	 */
	COMMA_SEPARATOR(","),
	
	/**
	 * End of file.
	 */
	EOF(null),
	
	/**
	 * String value.
	 */
	VALUE_STRING(null),
	
	/**
	 * Integer value.
	 */
	VALUE_INTEGER(null),
	
	/**
	 * Long value.
	 */
	VALUE_LONG(null),
	
	/**
	 * Double value.
	 */
	VALUE_DOUBLE(null),
	
	/**
	 * Float value.
	 */
	VALUE_FLOAT(null),
	
	/**
	 * True boolean value.
	 */
	VALUE_TRUE("true"),
	
	/**
	 * False boolean value.
	 */
	VALUE_FALSE("false"),
	
	/**
	 * JSON null value.
     */
	VALUE_NULL("null");
	
	private String token;
	private JSONTokenEnum(String token) {
		this.token = token;
	}
	
	public String getStringToken() {
		return token;
	}
}
