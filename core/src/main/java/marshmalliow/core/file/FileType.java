package marshmalliow.core.file;

public enum FileType {
	JSON(".json"),
	MOBF(".mobf"),
	PLAIN_TEXT(".txt"),
	CSV(".csv"),
	UNKNOWN("");
	
	private String extension;
	private FileType(String extension) {
		this.extension = extension.toLowerCase();
	}
	
	public String getExtension() {
		return this.extension;
	}
}
