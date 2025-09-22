package marshmalliow.core.file.csv;

public enum CSVTokenEnum {

	/**
	 * Field Separator. 
	 * Separates each file for each other.<br/>
	 * Cannot be defined here because the user has the choice of its own separator
	 * 
	 */
	FIELD_SEPARATOR(null),
	
	/**
	 * Field escaping character.
	 * Optional but must be use if special character is used inside the field.
	 */
	FIELD_ESCAPING("\""),
	
	/**
	 * Line break.
	 * Represent a new line inside the CSV. If enclosed inside double quotes it is ignored.
	 */
	LINE_BREAK(null),
		
	/**
	 * End of file.
	 */
	EOF(null),
	
	/**
	 * Value coming from a file.
	 */
	FIELD_VALUE(null);
	
	private String token;
	private CSVTokenEnum(String token) {
		this.token = token;
	}
	
	public String getStringToken() {
		return token;
	}
}
