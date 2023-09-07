package marshmalliow.core.json.utils;

public enum JSONTokenEnum {
	
	LEFT_BRACKET("["),
	RIGHT_BRACKET("]"),
	LEFT_BRACE("{"),
	RIGHT_BRACE("}"),
	KEY_VALUE_SEPARATOR(":"),
	COMMA_SEPARATOR(","),
	EOF(null),
	
	VALUE_STRING(null),
	VALUE_INTEGER(null),
	VALUE_LONG(null),
	VALUE_DOUBLE(null),
	VALUE_FLOAT(null),
	VALUE_TRUE("true"),
	VALUE_FALSE("false"),
	VALUE_NULL("null");
	
	private String token;
	private JSONTokenEnum(String token) {
		this.token = token;
	}
	
	public String getStringToken() {
		return token;
	}
}
