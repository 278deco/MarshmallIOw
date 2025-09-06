package marshmalliow.core.directory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public interface Directory {

	String id();
	
	/**
	 * Resolve the given name to a URI inside this directory.
	 * @param name The name of the file to resolve
	 * @return The resolved URI
	 */
	URI resolve(String name);
	
	InputStream openInputStream(String name) throws IOException;
	OutputStream openOutputStream(String name) throws IOException;
	
	boolean exists(String name) throws IOException;
	boolean isReadable(String name) throws IOException;
	long size(String name) throws IOException;
}
