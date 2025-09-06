package marshmalliow.core.file.text;

import marshmalliow.core.directory.Directory;
import marshmalliow.core.security.FileCredentials;

public abstract class AbstractTextFileBuilder<T extends AbstractTextFile> {
	
	protected Directory directory;
	protected String directoryId;
	protected String name;
	protected FileCredentials credentials;

	public abstract T build();
}
