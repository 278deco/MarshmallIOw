package marshmalliow.core.builder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marshmalliow.core.binary.MOBFFile;
import marshmalliow.core.binary.data.MOBFFileHeader;
import marshmalliow.core.binary.data.container.ObjectDataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.CompressionType;
import marshmalliow.core.exceptions.MOBFInitializationException;
import marshmalliow.core.objects.Directory;

public class MOBFFactory {
	
	public static final Logger LOGGER = LogManager.getLogger(MOBFFactory.class);

	public static final String AUTO_DIRECTORY_NAME = "auto:";
	public static final DataTypeRegistry DEFAULT_MOBF_REGISTRY = DataTypeEnum.createSafeNewRegistry(LOGGER).build();
	
	private static final ReentrantLock MUTEX = new ReentrantLock();
	private static volatile MOBFFactory instance;
	
	private DirectoryManager directoryManager;
	private DataTypeRegistry defaultRegistry = DEFAULT_MOBF_REGISTRY;
	
	private MOBFFactory() { }
	
	/**
	 * Attach a {@link DirectoryManager} and its loaded directories.<br/>
	 * This is useful for the {@link MOBFFactory} if you are reusing some paths
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
	 * Set the default {@link DataTypeRegistry} used when none is provided in the {@link MOBFBuilder}
	 * 
	 * @param registry An instance of DataTypeRegistry
	 */
	public static void withDefaultRegistry(DataTypeRegistry registry) {
		try {
			MUTEX.lock();
			if(instance == null) get();
		
			instance.defaultRegistry = registry;
		}finally {
			MUTEX.unlock();
		}
	}
	
	/**
	 * Get the instance of {@link MOBFFactory} as a singleton.
	 * 
	 * @return the instance of MOBFFactory or <code>null</code>
	 */
	public static MOBFFactory get() {
		if(instance == null) {
			try {
				MUTEX.lock();
				if(instance == null) instance = new MOBFFactory();
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
	 * Open an existing MOBF file on the disk and return its root {@link ObjectDataType}.<br/>
	 * The directory must be already registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, a {@link NullPointerException} will be thrown.
	 * 
	 * @param directoryId The id of the directory where the MOBF file is located
	 * @param name The name of the MOBF file (preferably without the .mobf extension)
	 * @param compression The compression type used in the MOBF file
	 * @param header The MOBF file header to use
	 * @return the root ObjectDataType of the MOBF file
	 * @throws MOBFInitializationException if the MOBF file cannot be read or initialized
	 */
	public ObjectDataType getMOBFContent(String directoryId, String name, CompressionType compression, MOBFFileHeader header) throws MOBFInitializationException {
		return getMOBFContent(getDirectory(directoryId), name, compression, header);
	}
	
	/**
	 * Open an existing MOBF file on the disk and return its root {@link ObjectDataType}.<br/>
	 * A directory with the name "auto:{folder name}" will be created and registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param path The path of the directory where the MOBF file is located
	 * @param name The name of the MOBF file (preferably without the .mobf extension)
	 * @param compression The compression type used in the MOBF file
	 * @param header The MOBF file header to use
	 * @return the root ObjectDataType of the MOBF file
	 * @throws MOBFInitializationException if the MOBF file cannot be read or initialized
	 */
	public ObjectDataType getMOBFContent(Path path, String name, CompressionType compression, MOBFFileHeader header) throws MOBFInitializationException {
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return getMOBFContent(directory, name, compression, header);
	}
	
	
	/**
	 * Open an existing MOBF file on the disk and return its root {@link ObjectDataType}.<br/>
	 * The directory is not required to be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param directory The directory where the MOBF file is located
	 * @param name The name of the MOBF file (preferably without the .mobf extension)
	 * @param compression The compression type used in the MOBF file
	 * @param header The MOBF file header to use
	 * @return the root ObjectDataType of the MOBF file
	 * @throws MOBFInitializationException if the MOBF file cannot be read or initialized
	 */
	public ObjectDataType getMOBFContent(Directory directory, String name, CompressionType compression, MOBFFileHeader header) throws MOBFInitializationException {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = name.replace(".mobf", "");
		
		final MOBFFile file = MOBFFile.builder()
				.directory(directory)
				.name(finalName)
				.registry(this.defaultRegistry)
				.header(header)
				.compression(compression)
				.build();
		
		try {
			file.readFile();
		}catch(IOException e) {
			throw new MOBFInitializationException("Cannot read the MOBF File.", e);
		}
		
		return file.getRoot();
	}

	/**
	 * Create a new {@link MOBFFile} instance without doing any IO to the disk (no writes or reads).<br/>
	 * The directory must be already registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, a {@link NullPointerException} will be thrown.
	 * 
	 * @param directoryId The id of the directory where the MOBF file will be located
	 * @param name The name of the MOBF file (preferably without the .mobf extension)
	 * @param compression The compression type to use in the MOBF file
	 * @param header The MOBF file header to use
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createMOBFFile(String directoryId, String name, CompressionType compression, MOBFFileHeader header) {
		return createMOBFFile(getDirectory(directoryId), name, compression, header);
	}
	
	/**
	 * Create a new {@link MOBFFile} instance without doing any IO to the disk (no writes or reads).<br/>
	 * A directory with the name "auto:{folder name}" will be created and registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param path The path of the directory where the MOBF file will be located
	 * @param name The name of the MOBF file (preferably without the .mobf extension)
	 * @param compression The compression type to use in the MOBF file
	 * @param header The MOBF file header to use
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createMOBFFile(Path path, String name, CompressionType compression, MOBFFileHeader header) {
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return createMOBFFile(directory, name, compression, header);
	}
	
	/**
	 * Create a new {@link MOBFFile} instance without doing any IO to the disk (no writes or reads).<br/>
	 * The directory is not required to be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param directory The directory where the MOBF file will be located
	 * @param name The name of the MOBF file (preferably without the .mobf extension)
	 * @param compression The compression type to use in the MOBF file
	 * @param header The MOBF file header to use
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createMOBFFile(Directory directory, String name, CompressionType compression, MOBFFileHeader header) {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = name.replace(".mobf", "");
		
		return MOBFFile.builder()
				.directory(directory)
				.name(finalName)
				.registry(this.defaultRegistry)
				.header(header)
				.compression(compression)
				.build();
	}
	
	/**
	 * Create a new instance of a class extending {@link MOBFFile} without doing any IO to the disk (no writes or reads).<br/>
	 * The directory must be already registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, a {@link NullPointerException} will be thrown.
	 * 
	 * @param clazz The class extending MOBFFile to instantiate
	 * @param directoryId The id of the directory where the MOBF file will be located
	 * @param name The name of the MOBF file (preferably without the .mobf extension)
	 * @param compression The compression type to use in the MOBF file
	 * @param header The MOBF file header to use
	 * @return a new instance of the provided MOBFFile subclass
	 * @throws MOBFInitializationException if the class cannot be instantiated
	 */
	public <E extends MOBFFile> E createMOBFFileAs(Class<E> clazz, String directoryId, String name, CompressionType compression, MOBFFileHeader header) throws MOBFInitializationException {
		return createMOBFFileAs(clazz, getDirectory(directoryId), name, compression, header);
	}
	
	/**
	 * Create a new instance of a class extending {@link MOBFFile} without doing any IO to the disk (no writes or reads).<br/>
	 * A directory with the name "auto:{folder name}" will be created and registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param clazz The class extending MOBFFile to instantiate
	 * @param path The path of the directory where the MOBF file will be located
	 * @param name The name of the MOBF file (preferably without the .mobf extension)
	 * @param compression The compression type to use in the MOBF file
	 * @param header The MOBF file header to use
	 * @return a new instance of the provided MOBFFile subclass
	 * @throws MOBFInitializationException if the class cannot be instantiated
	 */
	public <E extends MOBFFile> E createMOBFFileAs(Class<E> clazz, Path path, String name, CompressionType compression, MOBFFileHeader header) throws MOBFInitializationException {
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return createMOBFFileAs(clazz, directory, name, compression, header);
	}
	
	/**
	 * Create a new instance of a class extending {@link MOBFFile} without doing any IO to the disk (no writes or reads).<br/>
	 * The directory is not required to be registered in the {@link DirectoryManager} attached to the factory.<br/>
	 * If no {@link DirectoryManager} is attached, the directory will not be registered.
	 * 
	 * @param clazz The class extending MOBFFile to instantiate
	 * @param directory The directory where the MOBF file will be located
	 * @param name The name of the MOBF file (preferably without the .mobf extension)
	 * @param compression The compression type to use in the MOBF file
	 * @param header The MOBF file header to use
	 * @return a new instance of the provided MOBFFile subclass
	 * @throws MOBFInitializationException if the class cannot be instantiated
	 */
	public <E extends MOBFFile> E createMOBFFileAs(Class<E> clazz, Directory directory, String name, CompressionType compression, MOBFFileHeader header) throws MOBFInitializationException {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		if(clazz == null || !MOBFFile.class.isAssignableFrom(clazz)) throw new IllegalArgumentException("The provided class is not a valid MOBFFile subclass.");
		
		
		try {
			final String finalName = name.replace(".mobf", "");

			return MOBFFile.builder(clazz)
					.directory(directory)
					.name(finalName)
					.registry(this.defaultRegistry)
					.header(header)
					.compression(compression)
					.build();
		}catch(IllegalArgumentException e) {
			throw new MOBFInitializationException("Cannot instantiate the MOBF File.", e);
		}
	}
}
