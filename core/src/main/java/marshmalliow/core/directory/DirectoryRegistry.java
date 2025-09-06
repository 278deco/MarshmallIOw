package marshmalliow.core.directory;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DirectoryRegistry {

	private final Map<String, Directory> directories = new HashMap<>();
	private final List<DirectoryProvider> providers = new ArrayList<>();
	
	public DirectoryRegistry() {
		providers.add(new LocalDirectoryProvider());
		providers.add(new InMemoryDirectoryProvider());
	}
	
	public void register(String id, URI uri) {
		final String scheme = uri.getScheme();
		
		for(final DirectoryProvider provider : providers) {
			if(provider.supports(scheme)) {
				final Directory dir = provider.create(id, uri);
				directories.put(id, dir);
				return;
			}
		}
		
		throw new IllegalArgumentException("No provider found for scheme: " + scheme);
	}
	
	public Optional<Directory> get(String id) {
		return Optional.ofNullable(directories.get(id));
	}
	
	public void addProvider(DirectoryProvider provider) {
		this.providers.add(provider);
	}
	
	public int directoriesSize() {
		return directories.size();
	}
	
	public int providersSize() {
		return providers.size();
	}
	
}
