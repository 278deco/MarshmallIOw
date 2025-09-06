package marshmalliow.core.directory;

import java.net.URI;

public class InMemoryDirectoryProvider implements DirectoryProvider {

	@Override
	public boolean supports(String scheme) {
		return "memory".equalsIgnoreCase(scheme);
	}

	@Override
	public Directory create(String id, URI uri) {
		return new InMemoryDirectory(id);
	}

}
