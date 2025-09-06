package marshmalliow.core.directory;

import java.net.URI;
import java.nio.file.Path;

public class LocalDirectoryProvider implements DirectoryProvider {

	@Override
	public boolean supports(String scheme) {
		return "file".equalsIgnoreCase(scheme);
	}

	@Override
	public Directory create(String id, URI uri) {
		return new LocalDirectory(id, Path.of(uri));
	}

}
