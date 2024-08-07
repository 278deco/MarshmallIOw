package marshmalliow.core.file.dotenv;

public enum DotenvTokenEnum {
	
	COMMENT("#"),
	KEY_VALUE_SEPARATOR("="),
	NEW_LINE_ESCAPE("\\"),
	EOF(null),
	
	VALUE(null);
	
	private String token;
	private DotenvTokenEnum(String token) {
		this.token = token;
	}
	
	public String getStringToken() {
		return token;
	}
	
	public char getCharToken() {
		return token.charAt(0);
	}
}
