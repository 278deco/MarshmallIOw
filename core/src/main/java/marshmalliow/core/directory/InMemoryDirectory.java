package marshmalliow.core.directory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDirectory implements Directory {

	private final ConcurrentHashMap<String, byte[]> storage = new ConcurrentHashMap<>();
	
	private final String id;
	
	public InMemoryDirectory(String id) {
		this.id = id;
	}
	
	@Override
	public String id() {
		return this.id;
	}
	
	@Override
	public String scheme() {
		return "memory";
	}

	@Override
	public URI resolve(String name) {
		return URI.create("memory://" + this.id + "/" + name);
	}
	
	@Override
	public InputStream openInputStream(String name) throws IOException {
		return new ByteArrayInputStream(storage.getOrDefault(name, new byte[0]));
	}
	
	@Override
	public OutputStream openOutputStream(String name) throws IOException {
		return new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				storage.put(name, this.toByteArray());
			}
		};
	}
	
	@Override
	public boolean exists(String name) throws IOException {
		return storage.containsKey(name);
	}
	
	@Override
	public boolean isReadable(String name) throws IOException {
		return storage.containsKey(name);
	}
	
	@Override
	public long size(String name) throws IOException {
		return storage.getOrDefault(name, new byte[0]).length;
	}

}
