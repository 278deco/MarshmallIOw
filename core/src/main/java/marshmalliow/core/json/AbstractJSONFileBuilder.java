package marshmalliow.core.json;

import marshmalliow.core.directory.Directory;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.security.FileCredentials;

public abstract class AbstractJSONFileBuilder<T extends AbstractJSONFile> {
	
	protected Directory directory;
	protected String directoryId;
	protected String name;
	protected JSONContainer base;
	protected FileCredentials credentials;

	public abstract T build();
	
}
