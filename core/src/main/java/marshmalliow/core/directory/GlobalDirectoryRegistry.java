package marshmalliow.core.directory;

public class GlobalDirectoryRegistry {
	
	private static final DirectoryRegistry INSTANCE = new DirectoryRegistry();
	
	public static DirectoryRegistry get() {
		synchronized (INSTANCE) {
			return INSTANCE;
		}
	}
	
	private GlobalDirectoryRegistry() {}

}
