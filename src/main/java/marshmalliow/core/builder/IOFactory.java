package marshmalliow.core.builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.InstanceNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marshmalliow.core.binary.MOBFFile;
import marshmalliow.core.binary.data.MOBFFileHeader;
import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.data.types.container.ObjectDataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.CompressionType;
import marshmalliow.core.exceptions.UnsupportedJSONContainerException;
import marshmalliow.core.file.TextFile;
import marshmalliow.core.io.JSONLexer;
import marshmalliow.core.io.JSONParser;
import marshmalliow.core.json.JSONFile;
import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.security.FileCredentials;

/**
 * 
 * @author 278deco
 * @version 1.0
 */
public class IOFactory {
	
	public static final Logger LOGGER = LogManager.getLogger(IOFactory.class);
	public static final DataTypeRegistry DEFAULT_MOBF_REGISTRY = DataTypeEnum.createSafeNewRegistry(LOGGER).build();
	public static final Integer DEFAULT_HTTP_TIMEOUT = 300000;
	
	private static final ReentrantLock  mutex = new ReentrantLock();
	private DirectoryManager directoryManager;
	
	private static volatile IOFactory instance;
	
	private IOFactory() { }
	
	/**
	 * Bind a specific {@link DirectoryManager} listing all loaded directories
	 * @param dirManager An instance of DirectoryManager
	 * @throws InstanceNotFoundException 
	 */
	public static void bindDirectoryManager(DirectoryManager dirManager) throws InstanceNotFoundException {
		if(instance != null) {
			try {
				mutex.lock();
				instance.directoryManager = dirManager;
			}finally {
				mutex.unlock();
			}
		}else throw new InstanceNotFoundException("Cannot bind a directory manager before calling get method");
	}

	/**
	 * Get the instance of {@link IOFactory} as a singleton<br/>
	 * IF no instance is found, {@code null} is returned
	 * @return the instance of IOFactory or <code>null</code>
	 */
	public static IOFactory get() {
		if(instance == null) {
			synchronized (IOFactory.class) {
				if(instance == null) instance = new IOFactory();
			}
		}

		return instance;
	}
	
	/**
	 * Return a {@link Directory} if the provided id is found in the {@link DirectoryManager}<br/>
	 * If no directory is found, throws {@link NullPointerException}
	 * @param id The provided directory's id
	 * @return a directory object corresponding to the id
	 */
	private final Directory getDirectory(String id) {
		final Directory dir = this.directoryManager.getLoadedDirectory(id);
		if(dir == null) throw new NullPointerException("Directory with id "+id+" doesn't exist or is not loaded for the factory");
		return dir;
	}
	
	/**
	 * Return a {@link Directory} if the provided path is found in the {@link DirectoryManager}<br/>
	 * If no directory is found, throws {@link NullPointerException}
	 * @param path The provided directory's path
	 * @return a directory object corresponding to the path
	 */
	private final Directory getDirectory(Path path) {
		final Directory dir = this.directoryManager.getLoadedDirectory(path);
		if (dir == null)
			throw new NullPointerException("Directory with path " + path + " doesn't exist or is not loaded for the factory");
		return dir;
	}
	
	/**
	 * Return the instance of {@link DirectoryManager} used by this factory
	 * @return the instance of DirectoryManager
	 */
	public DirectoryManager getDirectoryManager() {
		return this.directoryManager;
	}
	
	/*
	 * Get JSON File content methods
	 */
	
	/**
	 * Open a JSON File present on the disk and get gather its content
	 * @param <E> An object extending {@link JSONContainer}
	 * @param directoryID The id of the directory where the file is stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param classContainer The root of the JSON file to be opened (Object or Array)
	 * @param credentials (Optional) the credentials if the file is encrypted
	 * @return The container with the data of the JSON File
	 * @throws IOException
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public <E extends JSONContainer> E getJSONFileContent(String directoryID, String jsonName, Class<E> classContainer, Optional<FileCredentials> credentials) throws IOException {
		return getJSONFileContent(getDirectory(directoryID), jsonName, classContainer, credentials);
	}
	
	/**
	 * Open a JSON File present on the disk and get gather its content
	 * @param <E> An object extending {@link JSONContainer}
	 * @param path The path of the directory where the file is stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param classContainer The root of the JSON file to be opened (Object or Array)
	 * @param credentials (Optional) the credentials if the file is encrypted
	 * @return The container with the data of the JSON File
	 * @throws IOException
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public <E extends JSONContainer> E getJSONFileContent(Path path, String jsonName, Class<E> classContainer, Optional<FileCredentials> credentials) throws IOException {
		return getJSONFileContent(getDirectory(path), jsonName, classContainer, credentials);
	}
	
	/**
	 * Open a JSON File present on the disk and get gather its content
	 * @param <E> An object extending {@link JSONContainer}
	 * @param directory The {@link Directory} where the file is stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param classContainer The root of the JSON file to be opened (Object or Array)
	 * @param credentials (Optional) the credentials if the file is encrypted
	 * @return The container with the data of the JSON File
	 * @throws IOException
	 */
	public <E extends JSONContainer> E getJSONFileContent(Directory directory, String jsonName, Class<E> classContainer, Optional<FileCredentials> credentials) throws IOException {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		final JSONFile jsonFile = credentials.isPresent() ? new JSONFile(directory, jsonName, credentials.get()) : new JSONFile(directory, jsonName);
		jsonFile.readFile();
		
		return classContainer.cast(jsonFile.getContent());
	}
	
	/*
	 * Create new JSON File methods
	 */
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param directoryID The id of the directory where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param classContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public JSONFile createNewJSONFile(String directoryID, String jsonName, Class<? extends JSONContainer> classContainer, Optional<FileCredentials> credentials) throws UnsupportedJSONContainerException {
		return createNewJSONFile(getDirectory(directoryID), jsonName, classContainer, credentials);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param path The path of the directory where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param classContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public JSONFile createNewJSONFile(Path path, String jsonName, Class<? extends JSONContainer> classContainer, Optional<FileCredentials> credentials) throws UnsupportedJSONContainerException {
		return createNewJSONFile(getDirectory(path), jsonName, classContainer, credentials);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param directory The {@link Directory} where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param classContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 */
	public JSONFile createNewJSONFile(Directory directory, String jsonName, Class<? extends JSONContainer> classContainer, Optional<FileCredentials> credentials) throws UnsupportedJSONContainerException {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		if(classContainer == JSONObject.class) {
			return credentials.isPresent() ? new JSONFile(directory, jsonName, new JSONObject(), credentials.get()) : new JSONFile(directory, jsonName, new JSONObject()); 
		}else if(classContainer == JSONArray.class) {
			return credentials.isPresent() ? new JSONFile(directory, jsonName, new JSONArray(), credentials.get()) : new JSONFile(directory, jsonName, new JSONArray()); 
		}else {
			throw new UnsupportedJSONContainerException();
		}
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it<br/>
	 * This instance is created without any content object (empty file)<br/>
	 * No other action is performed except the creation of the instance
	 * @param directoryID The id of the directory where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public JSONFile createNewEmptyJSONFile(String directoryID, String jsonName, Optional<FileCredentials> credentials) throws UnsupportedJSONContainerException {
		return createNewEmptyJSONFile(getDirectory(directoryID), jsonName, credentials);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it<br/>
	 * This instance is created without any content object (empty file)<br/>
	 * No other action is performed except the creation of the instance
	 * @param path The path of the directory where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public JSONFile createNewEmptyJSONFile(Path path, String jsonName, Optional<FileCredentials> credentials) throws UnsupportedJSONContainerException {
		return createNewEmptyJSONFile(getDirectory(path), jsonName, credentials);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it.<br/>
	 * This instance is created without any content object (empty file)<br/>
	 * No other action is performed except the creation of the instance
	 * @param directory The {@link Directory} where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 */
	public JSONFile createNewEmptyJSONFile(Directory directory, String jsonName, Optional<FileCredentials> credentials) throws UnsupportedJSONContainerException {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);

		return credentials.isPresent() ? new JSONFile(directory, jsonName, credentials.get()) : new JSONFile(directory, jsonName); 
	}
	
	
	
	/**
	 * Create a new instance of a {@link JSONFile}'s child class and returns it.<br/>
	 * No other action is performed except the creation of the instance
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param directoryID The id of the directory where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param classContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 * @throws IOException 
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public <E extends JSONFile> E createNewJSONFileChild(Class<E> baseClass, String directoryID, String jsonName, Class<? extends JSONContainer> classContainer, Optional<FileCredentials> credentials) throws UnsupportedJSONContainerException, IOException {
		return createNewJSONFileChild(baseClass, getDirectory(directoryID), jsonName, classContainer, credentials);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param path The path of the directory where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param classContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 * @throws IOException 
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public <E extends JSONFile> E createNewJSONFileChild(Class<E> baseClass, Path path, String jsonName, Class<? extends JSONContainer> classContainer, Optional<FileCredentials> credentials) throws UnsupportedJSONContainerException, IOException {
		return createNewJSONFileChild(baseClass, getDirectory(path), jsonName, classContainer, credentials);
	}
	
	/**
	 * Create a new instance of a {@link JSONFile}'s child class and returns it.<br/>
	 * No other action is performed except the creation of the instance
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param directory The {@link Directory} where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param classContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 * @throws IOException 
	 */
	public <E extends JSONFile> E createNewJSONFileChild(Class<E> baseClass, Directory directory, String jsonName, Class<? extends JSONContainer> classContainer, Optional<FileCredentials> credentials) throws IOException, UnsupportedJSONContainerException {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		try {
			final Constructor<E> constructor = credentials.isPresent() ? 
					baseClass.getConstructor(Directory.class, String.class, JSONContainer.class, FileCredentials.class) :
					baseClass.getConstructor(Directory.class, String.class, JSONContainer.class);
			
			if(classContainer == JSONObject.class) {
				return credentials.isPresent() ? constructor.newInstance(directory, jsonName, new JSONObject(), credentials.get()) : constructor.newInstance(directory, jsonName, new JSONObject()); 
			}else if(classContainer == JSONArray.class) {
				return credentials.isPresent() ? constructor.newInstance(directory, jsonName, new JSONArray(), credentials.get()) : constructor.newInstance(directory, jsonName, new JSONArray()); 
			}else {
				throw new UnsupportedJSONContainerException();
			}
			
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			final IOException thrownedE = new IOException("Cannot create instance of "+jsonName);
			thrownedE.addSuppressed(e);
			throw thrownedE;
		}
	}
	
	/**
	 * Create a new instance of a {@link JSONFile}'s child class and returns it.<br/>
	 * This instance is created without any content object (empty file)<br/>
	 * No other action is performed except the creation of the instance
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param directoryID The id of the directory where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 * @throws IOException 
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public <E extends JSONFile> E createNewJSONFileChild(Class<E> baseClass, String directoryID, String jsonName, Optional<FileCredentials> credentials) throws UnsupportedJSONContainerException, IOException {
		return createNewJSONFileChild(baseClass, getDirectory(directoryID), jsonName, credentials);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it<br/>
	 * This instance is created without any content object (empty file)<br/>
	 * No other action is performed except the creation of the instance
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param path The path of the directory where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 * @throws IOException 
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public <E extends JSONFile> E createNewJSONFileChild(Class<E> baseClass, Path path, String jsonName, Optional<FileCredentials> credentials) throws UnsupportedJSONContainerException, IOException {
		return createNewJSONFileChild(baseClass, getDirectory(path), jsonName, credentials);
	}
	
	/**
	 * Create a new instance of a {@link JSONFile}'s child class and returns it.<br/>
	 * This instance is created without any content object (empty file)<br/>
	 * No other action is performed except the creation of the instance
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param directory The {@link Directory} where the file will be stored
	 * @param jsonName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws UnsupportedJSONContainerException
	 * @throws IOException
	 */
	public <E extends JSONFile> E createNewJSONFileChild(Class<E> baseClass, Directory directory, String jsonName, Optional<FileCredentials> credentials) throws IOException, UnsupportedJSONContainerException {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		try {
			final Constructor<E> constructor = credentials.isPresent() ? 
					baseClass.getConstructor(Directory.class, String.class, FileCredentials.class) :
					baseClass.getConstructor(Directory.class, String.class);
			
			
			return credentials.isPresent() ? constructor.newInstance(directory, jsonName, credentials.get()) : constructor.newInstance(directory, jsonName); 
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			final IOException thrownedE = new IOException("Cannot create instance of "+jsonName);
			thrownedE.addSuppressed(e);
			throw thrownedE;
		}
	}
	
	/*
	 * Get MOBF File content methods
	 */
	
	/**
	 * Open a MOBF File present on the disk and get gather its content<br/>
	 * The {@link DataTypeRegistry} used by this function is {@link IOFactory#DEFAULT_MOBF_REGISTRY} 
	 * @param directoryID The id of the directory where the file is stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return an {@link ObjectDataType} with the data of the MOBF File
	 * @throws IOException
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public ObjectDataType getMOBFileContent(String directoryID, String mobfName, Optional<FileCredentials> credentials/*Unused for now*/) throws IOException {
		return getMOBFileContent(getDirectory(directoryID), mobfName, DEFAULT_MOBF_REGISTRY, credentials);
	}
	
	/**
	 * Open a MOBF File present on the disk and get gather its content<br/>
	 * The {@link DataTypeRegistry} used by this function is {@link IOFactory#DEFAULT_MOBF_REGISTRY} 
	 * @param path The path of the directory where the file is stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return an {@link ObjectDataType} with the data of the MOBF File
	 * @throws IOException
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public ObjectDataType getMOBFileContent(Path path, String mobfName, Optional<FileCredentials> credentials/*Unused for now*/) throws IOException {
		return getMOBFileContent(getDirectory(path), mobfName, DEFAULT_MOBF_REGISTRY, credentials);
	}
	
	/**
	 * Open a MOBF File present on the disk and get gather its content<br/>
	 * The {@link DataTypeRegistry} used by this function is {@link IOFactory#DEFAULT_MOBF_REGISTRY} 
	 * @param directory The {@link Directory} where the file is stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return an {@link ObjectDataType} with the data of the MOBF File
	 * @throws IOException
	 */
	public ObjectDataType getMOBFileContent(Directory directory, String mobfName, Optional<FileCredentials> credentials/*Unused for now*/) throws IOException {
		return getMOBFileContent(directory, mobfName, DEFAULT_MOBF_REGISTRY, credentials);
	}
	
	/**
	 * Open a MOBF File present on the disk and get gather its content
	 * @param directoryID The id of the directory where the file is stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return an {@link ObjectDataType} with the data of the MOBF File
	 * @throws IOException
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public ObjectDataType getMOBFileContent(String directoryID, String mobfName, DataTypeRegistry registry, Optional<FileCredentials> credentials/*Unused for now*/) throws IOException {
		return getMOBFileContent(getDirectory(directoryID), mobfName, registry, credentials);
	}
	
	/**
	 * Open a MOBF File present on the disk and get gather its content
	 * @param path The path of the directory where the file is stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return an {@link ObjectDataType} with the data of the MOBF File
	 * @throws IOException
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public ObjectDataType getMOBFileContent(Path path, String mobfName, DataTypeRegistry registry, Optional<FileCredentials> credentials/*Unused for now*/) throws IOException {
		return getMOBFileContent(getDirectory(path), mobfName, registry, credentials);
	}
	
	/**
	 * Open a MOBF File present on the disk and get gather its content
	 * @param directory The {@link Directory} where the file is stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return an {@link ObjectDataType} with the data of the MOBF File
	 * @throws IOException
	 */
	public ObjectDataType getMOBFileContent(Directory directory, String mobfName,  DataTypeRegistry registry, Optional<FileCredentials> credentials/*Unused for now*/) throws IOException {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		final MOBFFile mobfFile = new MOBFFile(directory, mobfName, registry);
		mobfFile.readFile();
		
		return mobfFile.getRoot();
	}
	
	/*
	 * Create new MOBF File methods
	 */
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link DataTypeRegistry} used by this function is {@link IOFactory#DEFAULT_MOBF_REGISTRY} <br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directoryID The id of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data 
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public MOBFFile createNewMOBFFile(String directoryID, String mobfName, ObjectDataType root, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewMOBFFile(getDirectory(directoryID), mobfName, root, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link DataTypeRegistry} used by this function is {@link IOFactory#DEFAULT_MOBF_REGISTRY} <br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param path The path of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data 
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public MOBFFile createNewMOBFFile(Path path, String mobfName, ObjectDataType root, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewMOBFFile(getDirectory(path), mobfName, root, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link DataTypeRegistry} used by this function is {@link IOFactory#DEFAULT_MOBF_REGISTRY} <br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directory The {@link Directory} where the file is stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data 
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createNewMOBFFile(Directory directory, String mobfName, ObjectDataType root, Optional<FileCredentials> credentials/*Unused for now*/) {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		return new MOBFFile(directory, mobfName, DEFAULT_MOBF_REGISTRY, MOBFFileHeader.DEFAULT_HEADER, root);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE}
	 * @param directoryID The id of the directory where the file is stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public MOBFFile createNewMOBFFile(String directoryID, String mobfName, ObjectDataType root, DataTypeRegistry registry, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewMOBFFile(getDirectory(directoryID), mobfName, root, registry, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param path The path of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public MOBFFile createNewMOBFFile(Path path, String mobfName, ObjectDataType root, DataTypeRegistry registry, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewMOBFFile(getDirectory(path), mobfName, root, registry, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directory The {@link Directory} where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data 
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createNewMOBFFile(Directory directory, String mobfName, ObjectDataType root, DataTypeRegistry registry, Optional<FileCredentials> credentials/*Unused for now*/) {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		return new MOBFFile(directory, mobfName, registry, MOBFFileHeader.DEFAULT_HEADER, root);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directoryID The id of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public MOBFFile createNewMOBFFile(String directoryID, String mobfName, ObjectDataType root, DataTypeRegistry registry, MOBFFileHeader fileHeader, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewMOBFFile(getDirectory(directoryID), mobfName, root, registry, fileHeader, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param path The path of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data 
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public MOBFFile createNewMOBFFile(Path path, String mobfName, ObjectDataType root, DataTypeRegistry registry, MOBFFileHeader fileHeader, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewMOBFFile(getDirectory(path), mobfName, root, registry, fileHeader, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directory The {@link Directory} where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data 
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createNewMOBFFile(Directory directory, String mobfName, ObjectDataType root, DataTypeRegistry registry, MOBFFileHeader fileHeader, Optional<FileCredentials> credentials/*Unused for now*/) {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		return new MOBFFile(directory, mobfName, registry, fileHeader, root);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param directoryID The id of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data 
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param fileCompression The {@link CompressionType} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public MOBFFile createNewMOBFFile(String directoryID, String mobfName, ObjectDataType root, DataTypeRegistry registry, MOBFFileHeader fileHeader, CompressionType fileCompression, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewMOBFFile(getDirectory(directoryID), mobfName, root, registry, fileHeader, fileCompression, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param path The path of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data 
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param fileCompression The {@link CompressionType} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public MOBFFile createNewMOBFFile(Path path, String mobfName, ObjectDataType root, DataTypeRegistry registry, MOBFFileHeader fileHeader, CompressionType fileCompression, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewMOBFFile(getDirectory(path), mobfName, root, registry, fileHeader, fileCompression, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param directory The {@link Directory} where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param root The root of the MOBF file to be created. Populate the file with initial data 
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param fileCompression The {@link CompressionType} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createNewMOBFFile(Directory directory, String mobfName, ObjectDataType root, DataTypeRegistry registry, MOBFFileHeader fileHeader, CompressionType fileCompression, Optional<FileCredentials> credentials/*Unused for now*/) {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		return new MOBFFile(directory, mobfName, registry, fileHeader, fileCompression, root);
	}
	
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link DataTypeRegistry} used by this function is {@link IOFactory#DEFAULT_MOBF_REGISTRY} <br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directoryID The id of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public MOBFFile createNewEmptyMOBFFile(String directoryID, String mobfName, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewEmptyMOBFFile(getDirectory(directoryID), mobfName, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link DataTypeRegistry} used by this function is {@link IOFactory#DEFAULT_MOBF_REGISTRY} <br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param path The path of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public MOBFFile createNewEmptyMOBFFile(Path path, String mobfName, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewEmptyMOBFFile(getDirectory(path), mobfName, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link DataTypeRegistry} used by this function is {@link IOFactory#DEFAULT_MOBF_REGISTRY} <br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directory The {@link Directory} where the file is stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createNewEmptyMOBFFile(Directory directory, String mobfName, Optional<FileCredentials> credentials/*Unused for now*/) {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		return new MOBFFile(directory, mobfName, DEFAULT_MOBF_REGISTRY, MOBFFileHeader.DEFAULT_HEADER);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directoryID The id of the directory where the file is stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public MOBFFile createNewEmptyMOBFFile(String directoryID, String mobfName, DataTypeRegistry registry, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewEmptyMOBFFile(getDirectory(directoryID), mobfName, registry, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param path The path of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public MOBFFile createNewEmptyMOBFFile(Path path, String mobfName, DataTypeRegistry registry, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewEmptyMOBFFile(getDirectory(path), mobfName, registry, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link MOBFFileHeader} used by this function is {@link MOBFFileHeader#DEFAULT_HEADER} <br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directory The {@link Directory} where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createNewEmptyMOBFFile(Directory directory, String mobfName, DataTypeRegistry registry, Optional<FileCredentials> credentials/*Unused for now*/) {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		return new MOBFFile(directory, mobfName, registry, MOBFFileHeader.DEFAULT_HEADER);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directoryID The id of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public MOBFFile createNewEmptyMOBFFile(String directoryID, String mobfName, DataTypeRegistry registry, MOBFFileHeader fileHeader, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewEmptyMOBFFile(getDirectory(directoryID), mobfName, registry, fileHeader, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param path The path of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public MOBFFile createNewEmptyMOBFFile(Path path, String mobfName, DataTypeRegistry registry, MOBFFileHeader fileHeader, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewEmptyMOBFFile(getDirectory(path), mobfName, registry, fileHeader, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance<br/>
	 * The {@link CompressionType} used by this function is {@link CompressionType#NONE} 
	 * @param directory The {@link Directory} where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createNewEmptyMOBFFile(Directory directory, String mobfName, DataTypeRegistry registry, MOBFFileHeader fileHeader, Optional<FileCredentials> credentials/*Unused for now*/) {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		return new MOBFFile(directory, mobfName, registry, fileHeader);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param directoryID The id of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param fileCompression The {@link CompressionType} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public MOBFFile createNewEmptyMOBFFile(String directoryID, String mobfName, DataTypeRegistry registry, MOBFFileHeader fileHeader, CompressionType fileCompression, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewEmptyMOBFFile(getDirectory(directoryID), mobfName, registry, fileHeader, fileCompression, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param path The path of the directory where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param fileCompression The {@link CompressionType} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public MOBFFile createNewEmptyMOBFFile(Path path, String mobfName, DataTypeRegistry registry, MOBFFileHeader fileHeader, CompressionType fileCompression, Optional<FileCredentials> credentials/*Unused for now*/) {
		return createNewEmptyMOBFFile(getDirectory(path), mobfName, registry, fileHeader, fileCompression, credentials);
	}
	
	/**
	 * Create a new instance of {@link MOBFFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param directory The {@link Directory} where the file will be stored
	 * @param mobfName The name of the file <strong>without extension</strong>
	 * @param registry The list of {@link DataType} possibly present in the file
	 * @param fileHeader The {@link MOBFFileHeader} used for the new file
	 * @param fileCompression The {@link CompressionType} used for the new file
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return a new instance of MOBFFile
	 */
	public MOBFFile createNewEmptyMOBFFile(Directory directory, String mobfName, DataTypeRegistry registry, MOBFFileHeader fileHeader, CompressionType fileCompression, Optional<FileCredentials> credentials/*Unused for now*/) {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		return new MOBFFile(directory, mobfName, registry, fileHeader, fileCompression);
	}
	
	/*
	 *  Get Text File content methods
	 */
	
	/**
	 * Open a Text File present on the disk and get gather its content
 	 * @param directoryID The id of the directory where the file is stored
	 * @param fileName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return A unmodifiable list of the lines contained in the file
	 * @throws IOException
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public List<String> getTextFileContent(String directoryID, String fileName, Optional<FileCredentials> credentials/*Unused for now*/) throws IOException {
		return getTextFileContent(getDirectory(directoryID), fileName, credentials);
	}
	
	/**
	 * Open a Text File present on the disk and get gather its content
 	 * @param path The path of the directory where the file is stored
	 * @param fileName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return A unmodifiable list of the lines contained in the file
	 * @throws IOException
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public List<String> getTextFileContent(Path path, String fileName, Optional<FileCredentials> credentials/*Unused for now*/) throws IOException {
		return getTextFileContent(getDirectory(path), fileName, credentials);
	}
	
	/**
	 * Open a Text File present on the disk and get gather its content
 	 * @param directory The {@link Directory} where the file is stored
	 * @param fileName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file is encrypted <strong>WIP</strong>
	 * @return A unmodifiable list of the lines contained in the file
	 * @throws IOException
	 */
	public List<String> getTextFileContent(Directory directory, String fileName, Optional<FileCredentials> credentials/*Unused for now*/) throws IOException {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		final TextFile textfile = credentials.isPresent() ? new TextFile(directory, fileName, credentials.get()) : new TextFile(directory, fileName);
		textfile.readFile();
		
		return textfile.getContent();
	}
	
	/*
	 * Create new Text File methods
	 */
	
	/**
	 * Create a new instance of {@link TextFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param directoryID The id of the directory where the file will be stored
	 * @param fileName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of TextFile
	 * @see DirectoryManager#getLoadedDirectory(String)
	 */
	public TextFile createNewTextFile(String directoryID, String fileName, Optional<FileCredentials> credentials) {
		return createNewTextFile(getDirectory(directoryID), fileName, credentials);
	}
	
	/**
	 * Create a new instance of {@link TextFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param path The path of the directory where the file will be stored
	 * @param fileName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of TextFile
	 * @see DirectoryManager#getLoadedDirectory(Path)
	 */
	public TextFile createNewTextFile(Path path, String fileName, Optional<FileCredentials> credentials) {
		return createNewTextFile(getDirectory(path), fileName, credentials);
	}
	
	/**
	 * Create a new instance of {@link TextFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * @param directory The {@link Directory} where the file will be stored
	 * @param fileName The name of the file <strong>without extension</strong>
	 * @param credentials (Optional) the credentials if the file will be encrypted
	 * @return a new instance of TextFile
	 */
	public TextFile createNewTextFile(Directory directory, String fileName, Optional<FileCredentials> credentials) {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		return credentials.isPresent() ? new TextFile(directory, fileName, credentials.get()) : new TextFile(directory, fileName);
	}
	
	/*
	 * Get file's content byte
	 */
	
	/**
	 * Open a File present on the disk and get gather its content as byte array
	 * @param directoryID The id of the directory where the file is stored
	 * @param fileName The name of the file <strong>without extension</strong>
	 * @return a byte array containing the file's content
	 * @throws IOException
	 */
	public byte[] getFileContentAsByte(String directoryID, String fileName) throws IOException {
		return getFileContentAsByte(getDirectory(directoryID), fileName);
	}
	
	/**
	 * Open a File present on the disk and get gather its content as byte array
	 * @param path The path of the directory where the file is stored
	 * @param fileName The name of the file <strong>without extension</strong>
	 * @return a byte array containing the file's content
	 * @throws IOException
	 */
	public byte[] getFileContentAsByte(Path path, String fileName) throws IOException {
		return getFileContentAsByte(getDirectory(path), fileName);
	}
	
	/**
	 * Open a File present on the disk and get gather its content as byte array
	 * @param directory The {@link Directory} where the file will be stored
	 * @param fileName The name of the file <strong>without extension</strong>
	 * @return a byte array containing the file's content
	 * @throws IOException
	 */
	public byte[] getFileContentAsByte(Directory directory, String fileName) throws IOException {
		this.directoryManager.registerNewDirectoryIfAbsent(directory);
		
		byte[] fileContent = new byte[0];
		InputStream stream = null;
		
		try {
			stream = Files.newInputStream(directory.getPath().resolve(fileName));
			
			fileContent = stream.readAllBytes();
		}finally {
			if(stream != null) stream.close();
		}
		
		return fileContent;
	}
	
	/*
	 * HTTP GET Request
	 */

	/**
	 * Open a {@link HttpURLConnection} and attempt to gather the content of the page as JSON
	 * @param stringURL The URL as a string where the date is stored
	 * @return The container with the data of the JSON File
	 * @throws IOException
	 */
	public JSONContainer getHttpContentAsJSON(String stringURL) throws IOException {
		return getHttpContentAsJSON(URI.create(stringURL).toURL());
	}
	
	/**
	 * Open a {@link HttpURLConnection} and attempt to gather the content of the page as JSON
	 * @param url an {@link URL} where the date is stored
	 * @return The container with the data of the JSON File
	 * @throws IOException
	 */
	public JSONContainer getHttpContentAsJSON(URL url) throws IOException {
		JSONContainer result = new JSONObject();
		HttpURLConnection connection = null;
		InputStreamReader reader = null;
		
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(DEFAULT_HTTP_TIMEOUT);
			connection.addRequestProperty("User-Agent", "Mozilla/5.0");
			connection.connect();
			
			final int responseCode = connection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK) {
				reader = new InputStreamReader(connection.getInputStream());
				
				result = new JSONParser(new JSONLexer(reader)).parse();
				
			}else {
				throw new IOException("HTTP connection ended with response code "+responseCode);
			}
			
		}finally {
			if(connection != null) connection.disconnect();
			if(reader != null) reader.close();
		}
		
		return result;
	}
	
}
