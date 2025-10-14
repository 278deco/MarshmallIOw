package marshmalliow.core.directory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalDirectory implements Directory {

	private final String id;
	private final Path path;
	
	public LocalDirectory(String id, Path path) {
		this.id = id;
		this.path = path;
	}
	
	@Override
	public String id() {
		return this.id;
	}
	
	@Override
	public String scheme() {
		return "file";
	}

	@Override
	public URI resolve(String name) {
		return this.path.resolve(name).toUri();
	}
	
	public Path resolvePath(String name) {
		return this.path.resolve(name);
	}
	
	public Path path() {
		return this.path;
	}
	
	@Override
	public InputStream openInputStream(String name) throws IOException {
		return Files.newInputStream(this.path.resolve(name));
	}
	
	@Override
	public OutputStream openOutputStream(String name) throws IOException {
		return Files.newOutputStream(this.path.resolve(name));
	}
	
	@Override
	public boolean exists(String name) throws IOException {
		return Files.exists(this.path.resolve(name));
	}
	
	@Override
	public boolean isReadable(String name) throws IOException {
		return Files.isReadable(this.path.resolve(name));
	}
	
	@Override
	public long size(String name) throws IOException {
		return Files.size(this.path.resolve(name));
	}

}
