package marshmalliow.core.file.csv;

public class CSVProperties {

	public static final char DEFAULT_LINE_SEPARATOR = ',';
	
	private final char fieldSeparator;
	private final boolean hasHeader;
	
	private CSVProperties(final CSVPropertiesBuilder builder) {
		this.fieldSeparator = builder.lineSeparator;
		this.hasHeader = builder.hasHeader;
	}
	
	public char getFieldSeparator() {
		return fieldSeparator;
	}
	
	public boolean hasHeader() {
		return hasHeader;
	}
	
	public static CSVPropertiesBuilder builder() {
		return new CSVPropertiesBuilder();
	}
	
	public static CSVProperties getDefault() {
		return new CSVPropertiesBuilder().lineSeparator(DEFAULT_LINE_SEPARATOR).hasHeader(true).build();
	}
	
	public static final class CSVPropertiesBuilder {
		
		private char lineSeparator;
		private boolean hasHeader;
		
		private CSVPropertiesBuilder() { }
		
		public CSVPropertiesBuilder lineSeparator(final char lineSeparator) {
			this.lineSeparator = lineSeparator;
			return this;
		}
		
		public CSVPropertiesBuilder hasHeader(final boolean hasHeader) {
			this.hasHeader = hasHeader;
			return this;
		}
		
		public CSVProperties build() {
			return new CSVProperties(this);
		}
		
	}
}
