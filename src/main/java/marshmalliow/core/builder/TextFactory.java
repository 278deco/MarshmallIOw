package marshmalliow.core.builder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import marshmalliow.core.exceptions.TextFileInitializationException;
import marshmalliow.core.file.TextFile;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.security.FileCredentials;

public class TextFactory {

	public static final String AUTO_DIRECTORY_NAME = "auto:";

	private static final ReentrantLock MUTEX = new ReentrantLock();
	private static volatile TextFactory instance;
	
	private DirectoryManager directoryManager;
	
	private TextFactory() { }
	
	/**
	 * Attach a {@link DirectoryManager} and its loaded directories.<br/>
	 * This is useful for the {@link TextFactory} if you are reusing some paths
	 * 
	 * @param dirManager An instance of DirectoryManager
	 */
	public static void withDirectoryManager(DirectoryManager dirManager) {
		try {
			MUTEX.lock();
			if(instance == null) get();
		
			instance.directoryManager = dirManager;
		}finally {
			MUTEX.unlock();
		}
	}
	
	/**
	 * Get the instance of {@link TextFactory} as a singleton.
	 * 
	 * @return the instance of TextFactory or <code>null</code>
	 */
	public static TextFactory get() {
		if(instance == null) {
			try {
				MUTEX.lock();
				if(instance == null) instance = new TextFactory();
			}finally {
				MUTEX.unlock();
			}
		}

		return instance;
	}
	
	/**
	 * Return a {@link Directory} if the provided id is found in the {@link DirectoryManager}<br/>
	 * If no directory is found, throws {@link NullPointerException}
	 * 
	 * @param id The provided directory's id
	 * @return a directory object corresponding to the id
	 */
	private final Directory getDirectory(String id) {
		if(this.directoryManager == null) throw new NullPointerException("No DirectoryManager were attached to the factory");
		
		final Directory dir = this.directoryManager.getLoadedDirectory(id);
		if(dir == null) throw new NullPointerException("Directory with id "+id+" doesn't exist or is not loaded for the factory");
		return dir;
	}
	
	/**
	 * Open an existing text file on the disk and return its content as a list of strings.<br/>
	 * Each string represents a line in the text file.<br/><br/>
	 * 
	 * The directory must be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, a {@link NullPointerException} will be thrown.<br/>
	 * 
	 * @param directoryId The id of the directory where the file is located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A list of strings representing the content of the text file
	 * @throws TextFileInitializationException If there is an error reading the file
	 */
	public List<String> getFileContent(String directoryId, String name) throws TextFileInitializationException {
		return getFileContent(getDirectory(directoryId), name);
	}
	
	/**
	 * Open an existing text file on the disk and return its content as a list of strings.<br/>
	 * Each string represents a line in the text file.<br/><br/>
	 * 
	 * A directory with the name "auto:{folder name}" will be created and registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param path The path of the directory where the file is located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A list of strings representing the content of the text file
	 * @throws TextFileInitializationException If there is an error reading the file
	 */
	public List<String> getFileContent(Path path, String name) throws TextFileInitializationException {
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return getFileContent(directory, name);
	}
	
	/**
	 * Open an existing text file on the disk and return its content as a list of strings.<br/>
	 * Each string represents a line in the text file.<br/><br/>
	 * 
	 * The directory is not required to be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param directory The directory where the file is located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A list of strings representing the content of the text file
	 * @throws TextFileInitializationException If there is an error reading the file
	 */
	public List<String> getFileContent(Directory directory, String name) throws TextFileInitializationException {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = name.replace(".txt", "");
		
		try {
			final TextFile textFile = TextFile.builder()
					.directory(directory)
					.name(finalName)
					.build();
			
			
			textFile.readFile();
			
			return textFile.getContent();
		}catch(Exception e) {
			throw new TextFileInitializationException("Cannot read the Text file.", e);
		}
	}
	
	/**
	 * Open an existing secured text file on the disk and return its content as a list of strings.<br/>
	 * Each string represents a line in the text file.<br/><br/>
	 * 
	 * The directory must be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, a {@link NullPointerException} will be thrown.<br/>
	 * 
	 * @param directoryId The id of the directory where the file is located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @param credentials The credentials used to decrypt the file
	 * @return A list of strings representing the content of the text file
	 * @throws TextFileInitializationException If there is an error reading the file
	 */
	public List<String> getSecuredFileContent(String directoryId, String name, FileCredentials credentials) throws TextFileInitializationException {
		return getSecuredFileContent(getDirectory(directoryId), name, credentials);
	}
	
	/**
	 * Open an existing secured text file on the disk and return its content as a list of strings.<br/>
	 * Each string represents a line in the text file.<br/><br/>
	 * 
	 * A directory with the name "auto:{folder name}" will be created and registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param path The path of the directory where the file is located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @param credentials The credentials used to decrypt the file
	 * @return A list of strings representing the content of the text file
	 * @throws TextFileInitializationException If there is an error reading the file
	 */
	public List<String> getSecuredFileContent(Path path, String name, FileCredentials credentials) throws TextFileInitializationException {
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return getSecuredFileContent(directory, name, credentials);
	}
	
	/**
	 * Open an existing secured text file on the disk and return its content as a list of strings.<br/>
	 * Each string represents a line in the text file.<br/><br/>
	 * 
	 * The directory is not required to be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param directory The directory where the file is located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @param credentials The credentials used to decrypt the file
	 * @return A list of strings representing the content of the text file
	 * @throws TextFileInitializationException If there is an error reading the file
	 */
	public List<String> getSecuredFileContent(Directory directory, String name, FileCredentials credentials) throws TextFileInitializationException {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = name.replace(".txt", "");
		
		try {
			final TextFile textFile = TextFile.builder()
					.directory(directory)
					.name(finalName)
					.credentials(credentials)
					.build();
			
			textFile.readFile();
			return textFile.getContent();
		}catch(IOException e) {
			throw new TextFileInitializationException("Cannot read the Text file.", e);
		}
	}
	
	/**
	 * Open an existing text file on the disk and return its content as a byte array.<br/><br/>
	 * 
	 * The directory must be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, a {@link NullPointerException} will be thrown.<br/>
	 * 
	 * @param directoryId The id of the directory where the file is located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A byte array representing the content of the text file
	 * @throws TextFileInitializationException If there is an error reading the file
	 */
	public byte[] getFileContentAsBytes(String directoryId, String name) throws TextFileInitializationException {
		return getFileContentAsBytes(getDirectory(directoryId), name);
	}
	
	/**
	 * Open an existing text file on the disk and return its content as a byte array.<br/><br/>
	 * 
	 * A directory with the name "auto:{folder name}" will be created and registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param path The path of the directory where the file is located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A byte array representing the content of the text file
	 * @throws TextFileInitializationException If there is an error reading the file
	 */
	public byte[] getFileContentAsBytes(Path path, String name) throws TextFileInitializationException {
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return getFileContentAsBytes(directory, name);
	}
	
	/**
	 * Open an existing text file on the disk and return its content as a byte array.<br/><br/>
	 * 
	 * The directory is not required to be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param directory The directory where the file is located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A byte array representing the content of the text file
	 * @throws TextFileInitializationException If there is an error reading the file
	 */
	public byte[] getFileContentAsBytes(Directory directory, String name) throws TextFileInitializationException {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = name.replace(".txt", "");
		
		byte[] data = new byte[0];
		
		try (final InputStream is = Files.newInputStream(directory.getPath().resolve(finalName))) {
			data = is.readAllBytes();
			
		} catch (IOException e) {
			throw new TextFileInitializationException("Cannot read the Text file.", e);
		}
		
		return data;
	}
	
	/**
	 * Create a new {@link TextFile} instance without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * The directory must be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, a {@link NullPointerException} will be thrown.<br/>
	 * 
	 * @param directoryId The id of the directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A new instance of TextFile
	 */
	public TextFile createFile(String directoryId, String name) {
		return createFile(getDirectory(directoryId), name);
	}
	
	/**
	 * Create a new {@link TextFile} instance without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * A directory with the name "auto:{folder name}" will be created and registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param path The path of the directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A new instance of TextFile
	 */
	public TextFile createFile(Path path, String name) {
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return createFile(directory, name);
	}
	
	/**
	 * Create a new {@link TextFile} instance without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * The directory is not required to be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param directory The directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A new instance of TextFile
	 */
	public TextFile createFile(Directory directory, String name) {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = name.replace(".txt", "");
		
		return TextFile.builder()
				.directory(directory)
				.name(finalName)
				.build();
	}
	
	/**
	 * Create a new secured {@link TextFile} instance without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * The directory must be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, a {@link NullPointerException} will be thrown.<br/>
	 * 
	 * @param directoryId The id of the directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @param credentials The credentials used to encrypt/decrypt the file
	 * @return A new instance of secured TextFile
	 */
	public TextFile createSecuredFile(String directoryId, String name, FileCredentials credentials) {
		return createSecuredFile(getDirectory(directoryId), name, credentials);
	}
	
	/**
	 * Create a new secured {@link TextFile} instance without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * A directory with the name "auto:{folder name}" will be created and registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param path The path of the directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @param credentials The credentials used to encrypt/decrypt the file
	 * @return A new instance of secured TextFile
	 */
	public TextFile createSecuredFile(Path path, String name, FileCredentials credentials) {
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return createSecuredFile(directory, name, credentials);
	}
	
	/**
	 * Create a new secured {@link TextFile} instance without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * The directory is not required to be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param directory The directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @param credentials The credentials used to encrypt/decrypt the file
	 * @return A new instance of secured TextFile
	 */
	public TextFile createSecuredFile(Directory directory, String name, FileCredentials credentials) {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = name.replace(".txt", "");
		
		return TextFile.builder()
				.directory(directory)
				.name(finalName)
				.credentials(credentials)
				.build();
	}
	
	/**
	 * Create a new instance of a class that extends {@link TextFile} without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * The directory must be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, a {@link NullPointerException} will be thrown.<br/>
	 * 
	 * @param clazz The class that extends TextFile
	 * @param directoryId The id of the directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A new instance of the specified class that extends TextFile
	 */
	public <E extends TextFile> E createFileAs(Class<E> clazz, String directoryId, String name) {
		return createFileAs(clazz, getDirectory(directoryId), name);
	}
	
	/**
	 * Create a new instance of a class that extends {@link TextFile} without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * A directory with the name "auto:{folder name}" will be created and registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param clazz The class that extends TextFile
	 * @param path The path of the directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A new instance of the specified class that extends TextFile
	 */
	public <E extends TextFile> E createFileAs(Class<E> clazz, Path path, String name) {
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return createFileAs(clazz, directory, name);
	}
	
	/**
	 * Create a new instance of a class that extends {@link TextFile} without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * The directory is not required to be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param clazz The class that extends TextFile
	 * @param directory The directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @return A new instance of the specified class that extends TextFile
	 */
	public <E extends TextFile> E createFileAs(Class<E> clazz, Directory directory, String name) {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = name.replace(".txt", "");
		
		return TextFile.builder(clazz)
				.directory(directory)
				.name(finalName)
				.build();
	}
	
	/**
	 * Create a new secured instance of a class that extends {@link TextFile} without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * The directory must be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, a {@link NullPointerException} will be thrown.<br/>
	 * 
	 * @param clazz The class that extends TextFile
	 * @param directoryId The id of the directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @param credentials The credentials used to encrypt/decrypt the file
	 * @return A new instance of the specified class that extends TextFile
	 */
	public <E extends TextFile> E createSecuredFileAs(Class<E> clazz, String directoryId, String name, FileCredentials credentials) {
		return createSecuredFileAs(clazz, getDirectory(directoryId), name, credentials);
	}
	
	/**
	 * Create a new secured instance of a class that extends {@link TextFile} without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * A directory with the name "auto:{folder name}" will be created and registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param clazz The class that extends TextFile
	 * @param path The path of the directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @param credentials The credentials used to encrypt/decrypt the file
	 * @return A new instance of the specified class that extends TextFile
	 */
	public <E extends TextFile> E createSecuredFileAs(Class<E> clazz, Path path, String name, FileCredentials credentials) {
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return createSecuredFileAs(clazz, directory, name, credentials);
	}
	
	/**
	 * Create a new secured instance of a class that extends {@link TextFile} without doing any IO to the disk (no writes or reads).<br/>
	 * 
	 * The directory is not required to be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param clazz The class that extends TextFile
	 * @param directory The directory where the file will be located
	 * @param name The name of the text file (preferably without the .txt extension)
	 * @param credentials The credentials used to encrypt/decrypt the file
	 * @return A new instance of the specified class that extends TextFile
	 */
	public <E extends TextFile> E createSecuredFileAs(Class<E> clazz, Directory directory, String name, FileCredentials credentials) {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = name.replace(".txt", "");
		
		return TextFile.builder(clazz)
				.directory(directory)
				.name(finalName)
				.credentials(credentials)
				.build();
	}
	
}
