package marshmalliow.core.file.dotenv;

public enum DotenvTokenEnum {
	
	/**
	 * The comment token.<br/>
	 * This token is used to represent the comment in the .env file
	 */
	COMMENT("#"),
	
	/**
	 * The key-value separator token.<br/>
	 * This token is used to separate the key from the value in the .env file
	 */
	KEY_VALUE_SEPARATOR("="),
	
	/**
	 * The escape token.<br/>
	 * This token is used to escape special characters in the .env file 
	 * but also to escape the new line character
	 */
	NEW_LINE_ESCAPE("\\"),
	
	/**
	 * The end of file token.
	 */
	EOF(null),
	
	/**
	 * The value token. <br/>
	 * This token is used to represent the value of the both the key and the value pair in the .env file
	 */
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
