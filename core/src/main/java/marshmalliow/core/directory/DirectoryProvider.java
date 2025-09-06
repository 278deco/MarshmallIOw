package marshmalliow.core.directory;

import java.net.URI;

public interface DirectoryProvider {

	/**
	 * Check if this provider supports the given scheme.
	 * @param scheme example: "file", "memory"
	 * @return true if supported
	 */
	boolean supports(String scheme);
	
	Directory create(String id, URI uri);
}
