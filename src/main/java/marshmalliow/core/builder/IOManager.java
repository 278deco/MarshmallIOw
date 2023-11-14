package marshmalliow.core.builder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marshmalliow.core.binary.MOBFFile;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.exceptions.FileNotLoadedException;
import marshmalliow.core.file.TextFile;
import marshmalliow.core.json.JSONFile;
import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.objects.IOClass;

/**
 * Manage all files and cache them for easy use across a program
 * @author 278deco
 * @version 1.0
 * @deprecated
 */
public class IOManager {
	
	private static final Logger LOGGER = LogManager.getLogger(IOManager.class);
	
	private static IOManager instance;
	
	private final Map<Integer, IOClass> files = new HashMap<>();
	private final DirectoryManager directoryManager;
	
	private final Object mutex = new Object();
	
	private IOManager(DirectoryManager directoryManager) {
		LOGGER.info("Starting Input Output manager...");
		
		this.directoryManager = directoryManager;
	}
	
	/**
	 * Create a new instance of {@link IOManager} as a singleton<br>
	 * If the instance already exist return the existing instance
	 * @param directoryManager An instance of {@link DirectoryManager}
	 * @return the instance of IOManager
	 */
	public static IOManager createInstance(DirectoryManager directoryManager) {
		if(instance == null) {
			synchronized (IOManager.class) {
				if(instance == null) instance = new IOManager(directoryManager);
			}
		}
		return instance;
	}

	/**
	 * Get the instance of IOManager as a singleton
	 * @return the instance of IOManager
	 */
	public static IOManager get() {
		return instance;
	}
	
	public <E extends IOClass> E createNewJSONFile(Directory directory, String fileName, Class<E> element) {
		synchronized (mutex) {	
			if(!element.isInstance(JSONFile.class)) {
				LOGGER.error("The IO class provided is not an instance of JSONFile class.");
				return null;
			}
			if(directory != null && fileName != null) {
				try {
					final Constructor<E> constructor = element.getConstructor(Directory.class, String.class);
					
					final E obj = constructor.newInstance(directory, fileName);
					
					if(obj != null) {
						this.files.putIfAbsent(obj.hashCode(), obj);
						return obj;
					}
				} catch (NoSuchMethodException | IllegalArgumentException | SecurityException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
					LOGGER.error("Unexpected error while loading "+element.getSimpleName()+".",e);
				}
			}
			
			return null;
		}
	}
	
	public <E extends IOClass> E createNewJSONFile(String directoryID, String fileName, Class<E> element) {
		return createNewJSONFile(this.directoryManager.getLoadedDirectory(directoryID), fileName, element);
	}
	
	public JSONFile createNewJSONObjectFile(Directory directory, String fileName) {
		synchronized (mutex) {
			if(directory != null && fileName != null) {
				final JSONFile result = new JSONFile(directory, fileName, new JSONObject());
				this.files.putIfAbsent(result.hashCode(), result);
				
				return result;
			}
			return null;
		}
	}
	
	public JSONFile createNewJSONObjectFile(String directoryID, String fileName) {
		return createNewJSONObjectFile(this.directoryManager.getLoadedDirectory(directoryID), fileName);
	}
	
	public JSONFile createNewJSONArrayFile(Directory directory, String fileName) {
		synchronized (mutex) {
			if(directory != null && fileName != null) {
				final JSONFile result = new JSONFile(directory, fileName, new JSONArray());
				this.files.putIfAbsent(result.hashCode(), result);
				
				return result;
			}
			return null;
		}
	}
	
	public JSONFile createNewJSONArrayFile(String directoryID, String fileName) {
		return createNewJSONArrayFile(this.directoryManager.getLoadedDirectory(directoryID), fileName);
	}
	
	public <E extends IOClass> E createNewMOBFFile(Directory directory, String fileName, DataTypeRegistry registry, Class<E> element) {
		synchronized (mutex) {	
			if(!element.isInstance(JSONFile.class)) {
				LOGGER.error("The IO class provided is not an instance of MOBFFile class.");
				return null;
			}
			if(directory != null && fileName != null) {
				try {
					final Constructor<E> constructor = element.getConstructor(Directory.class, String.class, DataTypeRegistry.class);
					
					final E obj = constructor.newInstance(directory, fileName, registry);
					
					if(obj != null) {
						this.files.putIfAbsent(obj.hashCode(), obj);
						return obj;
					}
				} catch (NoSuchMethodException | IllegalArgumentException | SecurityException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
					LOGGER.error("Unexpected error while loading "+element.getSimpleName()+".",e);
				}
			}
			
			return null;
		}
	}
	
	public <E extends IOClass> E createNewMOBFFile(String directoryID, String fileName, DataTypeRegistry registry, Class<E> element) {
		return createNewMOBFFile(this.directoryManager.getLoadedDirectory(directoryID), fileName, registry, element);
	}
	
	public MOBFFile createNewMOBFFile(Directory directory, String fileName, DataTypeRegistry registry) {
		synchronized (mutex) {
			if(directory != null && fileName != null) {
				final MOBFFile result = new MOBFFile(directory, fileName, registry);
				this.files.putIfAbsent(result.hashCode(), result);
				
				return result;
			}
			return null;
		}
	}
	
	public MOBFFile createNewMOBFFile(String directoryID, String fileName, DataTypeRegistry registry) {
		return createNewMOBFFile(this.directoryManager.getLoadedDirectory(directoryID), fileName, registry);
	}
	
	public <E extends IOClass> E createNewTextFile(Directory directory, String fileName, Class<E> element) {
		synchronized (mutex) {	
			if(!element.isInstance(TextFile.class)) {
				LOGGER.error("The IO class provided is not an instance of TextFile class.");
				return null;
			}
			if(directory != null && fileName != null) {
				try {
					final Constructor<E> constructor = element.getConstructor(Directory.class, String.class);
					
					final E obj = constructor.newInstance(directory, fileName);
					
					if(obj != null) {
						this.files.putIfAbsent(obj.hashCode(), obj);
						return obj;
					}
				} catch (NoSuchMethodException | IllegalArgumentException | SecurityException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
					LOGGER.error("Unexpected error while loading "+element.getSimpleName()+".",e);
				}
			}
			
			return null;
		}
	}
	
	public <E extends IOClass> E createNewTextFile(String directoryID, String fileName,  Class<E> element) {
		return createNewTextFile(this.directoryManager.getLoadedDirectory(directoryID), fileName, element);
	}
	
	public TextFile createNewTextFile(Directory directory, String fileName) {
		synchronized (mutex) {
			if(directory != null && fileName != null) {
				final TextFile result = new TextFile(directory, fileName);
				this.files.putIfAbsent(result.hashCode(), result);
				
				return result;
			}
			return null;
		}
	}
	
	public TextFile createNewTextFile(String directoryID, String fileName) {
		return createNewTextFile(this.directoryManager.getLoadedDirectory(directoryID), fileName);
	}
	
	/**
	 * Save all files in this IOManager instance
	 * @throws Exception
	 */
	public void saveFiles() throws Exception {
		this.saveFiles(false);
	}
	
	/**
	 * Save all files in this IOManager instance
	 * @param forceSave Force the file to save itself even if no modification were made to its content
	 * @throws Exception
	 */
	public void saveFiles(boolean forceSave) throws Exception {
		synchronized (mutex) {	
			LOGGER.info("Saving {} files...",files.size());
			int error = 0;
			
			for(IOClass element : files.values()) {
				try {
					element.saveFile(forceSave);
				}catch(IOException e) {
					error+=1;
				}
			}
			
			LOGGER.info("Successfully saved {} files! [errors occured:{}]", files.size(), error);
		}
	}

	/**
	 * Remove an element from the list of accessible files. <br>
	 * Safe remove means that any change made to the original file that are not saved will be saved
	 * before removing it from the list<br>
	 * The file to be removed must be a file represented by its own class in the program
	 * @param <E> a file instance who extends IOElement
	 * @return this instance of IOManager
	 * @throws Exception
	 */
	public <E extends IOClass> IOManager safeRemove(Class<E> element) throws Exception {
		synchronized (mutex) {	
			final int id = element.hashCode();
			if(this.files.containsKey(id)) {
				this.files.get(id).saveFile(true);
				
				this.files.remove(id);
			}
			
			return this;
		}
	}
	
	/**
	 * Remove an element from the list of accessible files. <br>
	 * Hard remove means that any change made to the original file that are not saved will be erased<br>
	 * The file to be removed must be a file represented by its own class in the program
	 * @param <E> a file instance who extends IOElement
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOClass> IOManager hardRemove(Class<E> element) {
		synchronized (mutex) {	
			this.files.remove(element.hashCode());
			
			return this;
		}
	}
	
	/**
	 * Get the instance of a file
	 * @param <E> a file instance who extends IOElement
	 * @return the instance of the file saved in the list
	 */
	public <E extends IOClass> E get(Class<E> element) {
		final int id = element.hashCode();
		
		if(this.files.containsKey(id)) {
			return element.cast(this.files.get(id));
		}else {
			throw new FileNotLoadedException("Couldn't find file in IOManager instance [class: "+element.getSimpleName()+"]");
		}
	}

	public DirectoryManager getDirectoryManager() {
		return directoryManager;
	}
	
	/**
	 * Return the total of files saved in this instance of {@link IOManager}
	 * @return the number of files
	 */
	public int getFilesNumber() {
		return this.files.size();
	}
	
}


