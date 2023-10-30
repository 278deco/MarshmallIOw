package marshmalliow.core.json.assertions;

public enum AssertionType {

	TRUE("True"),
	FALSE("False"),
	NOT_EQUAL("Not equal"),
	EQUAL("Equal"),
	NOT_NULL("Not null"), 
	INSTANCE_OF("Instance of"),
	JSON_CONTENT_TYPE("Content Type of");

	final String name;

	AssertionType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}