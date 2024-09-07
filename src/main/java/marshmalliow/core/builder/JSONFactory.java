package marshmalliow.core.builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;

import marshmalliow.core.io.JSONLexer;
import marshmalliow.core.io.JSONParser;
import marshmalliow.core.json.JSONFile;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.security.FileCredentials;
import reactor.util.annotation.Nullable;

public class JSONFactory {

	public static final String AUTO_DIRECTORY_NAME = "auto:";
	public static final Integer DEFAULT_HTTP_TIMEOUT = 300000;

	private static final ReentrantLock MUTEX = new ReentrantLock();
	private static volatile JSONFactory instance;
	
	private DirectoryManager directoryManager;
	
	private JSONFactory() { }
	
	/**
	 * Attach a {@link DirectoryManager} and its loaded directories.<br/>
	 * This is useful for the {@link JSONFactory} if you are reusing some paths
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
	 * Get the instance of {@link JSONFactory} as a singleton.
	 * 
	 * @return the instance of JSONFactory or <code>null</code>
	 */
	public static JSONFactory get() {
		if(instance == null) {
			try {
				MUTEX.lock();
				if(instance == null) instance = new JSONFactory();
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

	/*
	 * Parse to JSON Container methods 
	 */
	
	/**
	 * Parse a Reader to a JSON Container.
	 * <p>
	 * This method closes the reader after the parsing is done.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param reader The reader to read
	 * @param outputContainer The JSON structure to be read (Object or Array)
	 * @return The container with the data of the JSON
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E parseJSON(Reader reader, Class<E> outputContainer) throws IOException {
		JSONLexer lexer = null;
		try {
			lexer = new JSONLexer(reader);
			return outputContainer.cast(new JSONParser(lexer).parse());
		}finally {
			if (lexer != null) lexer.close();
			if (reader != null) reader.close(); // Just in case
		}
	}
	
	/**
	 * Parse an Input Stream to a JSON Container.
	 * <p>
	 * This method uses the UTF-8 charset as a default charset to read the byte array.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param stream The input stream to read
	 * @param outputContainer The JSON structure to be read (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 * @see #parseJSON(InputStream, Charset, Class)
	 */
	public <E extends JSONContainer> E parseJSON(InputStream stream, Class<E> outputContainer) throws IOException {
		return parseJSON(stream, StandardCharsets.UTF_8, outputContainer);
	}
	
	/**
	 * Parse an Input Stream to a JSON Container.
	 * <p>
	 * The charset is used by the {@link InputStreamReader} to read the byte array.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param stream The input stream to read
	 * @param charset The charset to use to read the byte array
	 * @param outputContainer The JSON structure to be read (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E parseJSON(InputStream stream, Charset charset, Class<E> outputContainer) throws IOException {
		InputStreamReader reader = null;
		JSONLexer lexer = null;
		try {
			reader = new InputStreamReader(stream, charset);
			lexer = new JSONLexer(reader);
			
			return outputContainer.cast(new JSONParser(lexer).parse());
		}finally {
			if(lexer != null) lexer.close();
			if(reader != null) reader.close(); //Just in case
		}
	}
	
	/**
	 * Parse a byte array to a JSON Container.
	 * <p>
	 * This method uses the UTF-8 charset as a default charset to read the byte array.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param byteArray The byte array
	 * @param outputContainer The JSON structure to be read (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 * @see #parseJSON(byte[], Charset, Class)
	 */
	public <E extends JSONContainer> E parseJSON(byte[] byteArray, Class<E> outputContainer) throws IOException {
		return parseJSON(byteArray, StandardCharsets.UTF_8, outputContainer);
	}
	
	/**
	 * Parse a byte array to a JSON Container.
	 * <p>
	 * The charset is used by the {@link InputStreamReader} to read the byte array.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param byteArray The byte array
	 * @param charset The charset to use to read the byte array
	 * @param outputContainer The JSON structure to be read (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E parseJSON(byte[] byteArray, Charset charset, Class<E> outputContainer) throws IOException {
		InputStreamReader reader = null;
		JSONLexer lexer = null;
		try {
			final ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);
			reader = new InputStreamReader(stream, charset);
			lexer = new JSONLexer(reader);
			
			return outputContainer.cast(new JSONParser(lexer).parse());
		}finally {
			if(lexer != null) lexer.close();
			if(reader != null) reader.close(); //Just in case
		}
	}
	
	/*
	 * Get JSON File content methods
	 */
	
	/**
	 * Open a JSON File present on the disk and get gather its content.
	 *  
	 * @param <E> An object extending {@link JSONContainer}
	 * @param directoryID The id of the directory where the file is stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param classContainer The root of the JSON file to be opened (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E getJSONFileContent(String directoryID, String jsonName, Class<E> classContainer) throws IOException {
		return getJSONFileContent(getDirectory(directoryID), jsonName, classContainer);
	}
	
	/**
	 * Open a JSON File present on the disk and get gather its content.
	 * 
	 * @implNote This method does not used the internal {@link DirectoryManager} to get the directory
	 * but instead uses the provided path to create a new instance of {@link Directory}
	 * that will not be registered in the manager.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param path The path of the directory where the file is stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param classContainer The root of the JSON file to be opened (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E getJSONFileContent(Path path, String jsonName, Class<E> classContainer) throws IOException {		
		//Create a new directory with the path, doesn't register it
		//The name of the directory is the last part of the path
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return getJSONFileContent(directory, jsonName, classContainer);
	}
	
	
	
	/**
	 * Open a JSON File present on the disk and get gather its content.
	 *
	 * @implNote If the directory is not registered in the {@link DirectoryManager}, it will be registered automatically.
	 *
	 * @param <E> An object extending {@link JSONContainer}
	 * @param directory The {@link Directory} where the file is stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param classContainer The root of the JSON file to be opened (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E getJSONFileContent(Directory directory, String jsonName, Class<E> classContainer) throws IOException {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = jsonName.replace(".json", "");

		final JSONFile jsonFile = new JSONFile(directory, finalName);
		jsonFile.readFile();
		
		return classContainer.cast(jsonFile.getContent());
	}
	
	/*
	 * Get secured JSON File content methods
	 */
	
	/**
	 * Open a secured JSON File present on the disk and get gather its content.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param directoryID The id of the directory where the file is stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param classContainer The root of the JSON file to be opened (Object or Array)
	 * @param credentials the credentials if the file is encrypted
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E getSecuredJSONFileContent(String directoryID, String jsonName,
			Class<E> classContainer, FileCredentials credentials) throws IOException {
		return getSecuredJSONFileContent(getDirectory(directoryID), jsonName, classContainer, credentials);
	}
	
	/**
	 * Open a secured JSON File present on the disk and get gather its content.
	 *
	 * @implNote This method does not used the internal {@link DirectoryManager} to get the directory
	 * but instead uses the provided path to create a new instance of {@link Directory}
	 * that will not be registered in the manager.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param path The path of the directory where the file is stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param classContainer The root of the JSON file to be opened (Object or Array)
	 * @param credentials the credentials if the file is encrypted
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E getSecuredJSONFileContent(Path path, String jsonName,
			Class<E> classContainer, FileCredentials credentials) throws IOException {		
		//Create a new directory with the path, doesn't register it
		//The name of the directory is the last part of the path
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return getSecuredJSONFileContent(directory, jsonName, classContainer, credentials);
	}
	
	/**
	 * Open a secured JSON File present on the disk and get gather its content.
	 * 
	 * @implNote If the directory is not registered in the {@link DirectoryManager}, it will be registered automatically.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param directory The {@link Directory} where the file is stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param classContainer The root of the JSON file to be opened (Object or Array)
	 * @param credentials the credentials if the file is encrypted
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E getSecuredJSONFileContent(Directory directory, String jsonName,
			Class<E> classContainer, FileCredentials credentials) throws IOException {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = jsonName.replace(".json", "");

		final JSONFile jsonFile = new JSONFile(directory, finalName, credentials);
		jsonFile.readFile();
		
		return classContainer.cast(jsonFile.getContent());
	}
	
	/*
	 * Create JSON File methods
	 */
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it.<br/>
	 * No other action is performed except the creation of the instance.
	 *
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * 
	 * @param directoryID The id of the directory where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @return a new instance of JSONFile
	 */
	public JSONFile createJSONFile(String directoryID, String jsonName, @Nullable JSONContainer rootContainer) {
		return createJSONFile(getDirectory(directoryID), jsonName, rootContainer);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it<br/>
	 * No other action is performed except the creation of the instance
	 * 
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * <p>
	 * This method does not used the internal {@link DirectoryManager} to get the directory
	 * but instead uses the provided path to create a new instance of {@link Directory}
	 * that will not be registered in the manager.
	 * 
	 * @param path The path of the directory where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @return a new instance of JSONFile
	 */
	public JSONFile createJSONFile(Path path, String jsonName, @Nullable JSONContainer rootContainer) {
		//Create a new directory with the path, doesn't register it
		//The name of the directory is the last part of the path
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return createJSONFile(directory, jsonName, rootContainer);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it.<br/>
	 * No other action is performed except the creation of the instance.
	 *
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * <p>
	 * If the directory is not registered in the {@link DirectoryManager}, it will be registered automatically.
	 * 
	 * @param directory The {@link Directory} where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @return a new instance of JSONFile
	 */
	public JSONFile createJSONFile(Directory directory, String jsonName, @Nullable JSONContainer rootContainer) {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = jsonName.replace(".json", "");
		
		if(rootContainer == null) {
			return new JSONFile(directory, finalName);
		}else {
			return new JSONFile(directory, finalName, rootContainer);
		}
	}
	
	/**
	 * Create secured JSON File methods
	 */
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it.<br/>
	 * No other action is performed except the creation of the instance.
	 *
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * 
	 * @param directoryID The id of the directory where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 */
	public JSONFile createSecuredJSONFile(String directoryID, String jsonName,
			@Nullable JSONContainer rootContainer, FileCredentials credentials) {
		return createSecuredJSONFile(getDirectory(directoryID), jsonName, rootContainer, credentials);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it<br/>
	 * No other action is performed except the creation of the instance.
	 *
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * <p>
	 * This method does not used the internal {@link DirectoryManager} to get the directory
	 * but instead uses the provided path to create a new instance of {@link Directory}
	 * that will not be registered in the manager.
	 * 
	 * @param path The path of the directory where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 */
	public JSONFile createSecuredJSONFile(Path path, String jsonName,
			@Nullable JSONContainer rootContainer, FileCredentials credentials) {
		//Create a new directory with the path, doesn't register it
		//The name of the directory is the last part of the path
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return createSecuredJSONFile(directory, jsonName, rootContainer, credentials);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it.<br/>
	 * No other action is performed except the creation of the instance.
	 * 
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * <p>
	 * If the directory is not registered in the {@link DirectoryManager}, it will be registered automatically.
	 * 
	 * @param directory The {@link Directory} where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 */
	public JSONFile createSecuredJSONFile(Directory directory, String jsonName,
			@Nullable JSONContainer rootContainer, FileCredentials credentials) {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		final String finalName = jsonName.replace(".json", "");
		
		if(rootContainer == null) {
			return new JSONFile(directory, finalName, credentials);
		}else {
			return new JSONFile(directory, finalName, rootContainer, credentials);
		}
	}
	
	/**
	 * Create JSON File from base methods
	 */
	
	/**
	 * Create a new instance of a {@link JSONFile}'s child class and returns it.<br/>
	 * No other action is performed except the creation of the instance.
	 * 
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * 
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param directoryID The id of the directory where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @return a new instance of JSONFile
	 * @throws IllegalArgumentException If the base class is not a children of {@link JSONFile}
	 * @throws IOException 
	 */
	public <E extends JSONFile> E createJSONFileFromBase(Class<E> baseClass, String directoryID,
			String jsonName, @Nullable JSONContainer rootContainer) throws IllegalArgumentException, IOException {
		return createJSONFileFromBase(baseClass, getDirectory(directoryID), jsonName, rootContainer);
	}
	
	/**
	 * Create a new instance of {@link JSONFile} and returns it.<br/>
	 * No other action is performed except the creation of the instance.
	 * 
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * <p>
	 * This method does not used the internal {@link DirectoryManager} to get the directory
	 * but instead uses the provided path to create a new instance of {@link Directory}
	 * that will not be registered in the manager.
	 * 
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param path The path of the directory where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @return a new instance of JSONFile
	 * @throws IllegalArgumentException If the base class is not a children of {@link JSONFile}
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONFile> E createJSONFileFromBase(Class<E> baseClass, Path path,
			String jsonName, @Nullable JSONContainer rootContainer) throws IllegalArgumentException, IOException {
		//Create a new directory with the path, doesn't register it
		//The name of the directory is the last part of the path
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return createJSONFileFromBase(baseClass, directory, jsonName, rootContainer);
	}
	
	/**
	 * Create a new instance of a {@link JSONFile}'s child class and returns it.<br/>
	 * No other action is performed except the creation of the instance.
	 * 
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * <p>
	 * If the directory is not registered in the {@link DirectoryManager}, it will be registered automatically.
	 * 
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param directory The {@link Directory} where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @return a new instance of JSONFile
	 * @throws IllegalArgumentException If the base class is not a children of {@link JSONFile}
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONFile> E createJSONFileFromBase(Class<E> baseClass, Directory directory,
			String jsonName, @Nullable JSONContainer rootContainer) throws IllegalArgumentException, IOException {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		if (baseClass == null || !(baseClass.isInstance(JSONFile.class))) {
			throw new IllegalArgumentException("The base class is not a child of JSONFile.");
		}
		
		try {
			final String finalName = jsonName.replace(".json", "");
			
			if(rootContainer == null ) {
				final Constructor<E> constructor = baseClass.getConstructor(Directory.class, String.class);
				return constructor.newInstance(directory, finalName); 
			}else {
				final Constructor<E> constructor = baseClass.getConstructor(Directory.class, String.class, JSONContainer.class);
				return constructor.newInstance(directory, finalName, rootContainer); 
			}

		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			final IOException thrownedE = new IOException("Cannot create instance of "+jsonName);
			thrownedE.addSuppressed(e);
			throw thrownedE;
		}
	}
	
	/**
	 * Create secured JSON File from base methods
	 */

	/**
	 * Create a secured instance of a {@link JSONFile}'s child class and returns it.<br/>
	 * No other action is performed except the creation of the instance.
	 * 
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * 
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param directoryID The id of the directory where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws IllegalArgumentException If the base class is not a children of {@link JSONFile}
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONFile> E createSecuredJSONFileFromBase(Class<E> baseClass, String directoryID,
			String jsonName, @Nullable JSONContainer rootContainer, FileCredentials credentials) throws IllegalArgumentException, IOException {
		return createSecuredJSONFileFromBase(baseClass, getDirectory(directoryID), jsonName, rootContainer, credentials);
	}
	
	/**
	 * Create a secured instance of {@link JSONFile} and returns it.<br/>
	 * No other action is performed except the creation of the instance.
	 * 
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * <p>
	 * This method does not used the internal {@link DirectoryManager} to get the directory
	 * but instead uses the provided path to create a new instance of {@link Directory}
	 * that will not be registered in the manager.
	 * 
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param path The path of the directory where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws IllegalArgumentException If the base class is not a children of {@link JSONFile}
	 * @throws IOException if an IO error occurs
	 */
	public <E extends JSONFile> E createSecuredJSONFileFromBase(Class<E> baseClass, Path path,
			String jsonName, @Nullable JSONContainer rootContainer, FileCredentials credentials) throws IllegalArgumentException, IOException {
		//Create a new directory with the path, doesn't register it
		//The name of the directory is the last part of the path
		final Directory directory = new Directory(AUTO_DIRECTORY_NAME+path.getFileName().toString(), path);
		
		return createSecuredJSONFileFromBase(baseClass, directory, jsonName, rootContainer, credentials);
	}
	
	/**
	 * Create a secured instance of a {@link JSONFile}'s child class and returns it.<br/>
	 * No other action is performed except the creation of the instance.
	 * 
	 * @implNote If the rootContainer is null, the file will be created empty (no root).
	 * <p>
	 * If the directory is not registered in the {@link DirectoryManager}, it will be registered automatically.
	 * 
	 * @param baseClass A child class from {@link JSONFile} which will be returned
	 * @param directory The {@link Directory} where the file will be stored
	 * @param jsonName The name of the file (preferably without the file extension)
	 * @param rootContainer The root of the JSON file to be created (Object or Array)
	 * @param credentials the credentials if the file will be encrypted
	 * @return a new instance of JSONFile
	 * @throws IllegalArgumentException If the base class is not a children of {@link JSONFile}
	 * @throws IOException if an IO error occurs
	 */
	public <E extends JSONFile> E createSecuredJSONFileFromBase(Class<E> baseClass, Directory directory,
			String jsonName, @Nullable JSONContainer rootContainer, FileCredentials credentials) throws IllegalArgumentException, IOException {
		if(this.directoryManager != null) this.directoryManager.registerNewDirectoryIfAbsent(directory);
		if (baseClass == null || !(baseClass.isInstance(JSONFile.class))) {
			throw new IllegalArgumentException("The base class is not a child of JSONFile.");
		}
		
		try {
			final String finalName = jsonName.replace(".json", "");
			
			if(rootContainer == null) {
				final Constructor<E> constructor = 
						baseClass.getConstructor(Directory.class, String.class, FileCredentials.class);
				
				return constructor.newInstance(directory, finalName, credentials);
			}else {
				final Constructor<E> constructor = 
						baseClass.getConstructor(Directory.class, String.class, JSONContainer.class, FileCredentials.class);
				
				return constructor.newInstance(directory, finalName, rootContainer, credentials);
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			final IOException thrownedE = new IOException("Cannot create instance of "+jsonName);
			thrownedE.addSuppressed(e);
			throw thrownedE;
		}
	}

	/*
	 * HTTP GET Request
	 */

	/**
	 * Open a {@link HttpURLConnection} and attempt to gather the content of the page as JSON.
	 * 
	 * @param stringURL The URL as a string where the date is stored
	 * @return The container with the data of the JSON File
	 * @throws IOException
	 */
	public JSONContainer getHttpContentAsJSON(String stringURL) throws IOException {
		return getHttpContentAsJSON(URI.create(stringURL).toURL());
	}
	
	/**
	 * Open a {@link HttpURLConnection} and attempt to gather the content of the page as JSON.
	 * 
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
