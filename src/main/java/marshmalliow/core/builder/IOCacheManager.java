package marshmalliow.core.builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import marshmalliow.core.exceptions.FileNotLoadedException;
import marshmalliow.core.exceptions.IdentifierAlreadyUsedException;
import marshmalliow.core.objects.IOClass;

/**
 * Cache every Input/Output Object extending {@link IOClass} with a simple identifier (id).<br/>
 * Uses a {@link ReadWriteLock} locking strategy.
 * @author 278deco
 * @version 1.0
 */
public class IOCacheManager {

//	private static final Logger LOGGER = LogManager.getLogger(IOCacheManager.class);
	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	
	private static volatile IOCacheManager instance;
	
	private final Map<String, IOClass> files = new HashMap<>();

	/**
	 * Get the instance of {@link IOCacheManager} as a singleton
	 * IF no instance is found, create it
	 * @return the instance of DBFactory or null
	 */
	public static IOCacheManager get() {
		if(instance == null) {
			synchronized (IOCacheManager.class) {
				if(instance == null) instance = new IOCacheManager();
			}
		}
		return instance;
	}
	
	/**
	 * Add a new Input/Output object extending {@link IOClass} into the cache.<br/>
	 * This method uses the object name (not the path) as the identifier. If you wish to use a specified id see {@link IOCacheManager#add(String, IOClass)}.
	 * @param obj The object to be added
	 * @throws IdentifierAlreadyUsedException if a file is registered with the same identifier
	 */
	public void add(IOClass obj) {
		try {
			LOCK.writeLock().lock();
			
			if (this.files.putIfAbsent(obj.getFileName(), obj) != null) {
				throw new IdentifierAlreadyUsedException("Identifier "+obj.getFileName()+" is already used by a registred file");
			}
		}finally {
			LOCK.writeLock().unlock();;
		}
		
	}
	
	/**
	 * Add a new Input/Output object extending {@link IOClass} into the cache.<br/>
	 * If you wish to use the file's name as the identifier, see {@link IOCacheManager#add(String, IOClass)}.
	 * @param identifier The id used to register the file into the cache
	 * @param obj The object to be added
	 * @throws IdentifierAlreadyUsedException if a file is registered with the same identifier
	 */
	public void add(String identifier, IOClass obj) {
		try {
			LOCK.writeLock().lock();
			if (this.files.putIfAbsent(identifier, obj) != null) {
				throw new IdentifierAlreadyUsedException("Identifier "+obj.getFileName()+" is already used by a registred file");
			}
		}finally {
			LOCK.writeLock().unlock();;
		}
	}
	
	/**
	 * Add a new Input/Output object extending {@link IOClass} into the cache.<br/>
	 * If an object is already using the same identifier, it will be erased.<br/>
	 * This method uses the object name (not the path) as the identifier. If you wish to use a specified id see {@link IOCacheManager#replace(String, IOClass)}.
	 * @param obj The object to be added
	 */
	public void replace(IOClass obj) {
		try {
			LOCK.writeLock().lock();
			this.files.put(obj.getFileName(), obj);
		}finally {
			LOCK.writeLock().unlock();;
		}
	}
	
	/**
	 * Add a new Input/Output object extending {@link IOClass} into the cache.<br/>
	 * If an object is already using the same identifier, it will be erased.<br/>
	 * If you wish to use the file's name as the identifier, see {@link IOCacheManager#replace(IOClass)}.
	 * @param identifier The id used to register the file into the cache
	 * @param obj The object to be added
	 * @throws IdentifierAlreadyUsedException if a file is registered with the same identifier
	 */
	public void replace(String identifier, IOClass obj) {
		try {
			LOCK.writeLock().lock();
			this.files.put(identifier, obj);
		}finally {
			LOCK.writeLock().unlock();;
		}
	}
	
	/**
	 * Remove Input/Output object from the cache.<br/>
	 * No check is performed before the removal nor saving.
	 * @param identifier The id of the {@link IOClass} object
	 * @return If the object has been successfully removed
	 */
	public boolean remove(String identifier) {
		try {
			LOCK.writeLock().lock();
			return this.files.remove(identifier) != null;
		}finally {
			LOCK.writeLock().unlock();;
		}
	}
	
	/**
	 * Remove Input/Output object from the cache.<br/>
	 * The file's name (not the path) is used as the identifier.<br/>
	 * The method checks if the provided object is equals to the saved one. No saving is done.
	 * @param obj The {@link IOClass} object
	 * @return If the object has been successfully removed
	 */
	public boolean remove(IOClass obj) {
		try {
			LOCK.writeLock().lock();
			return this.files.remove(obj.getFileName(), obj);
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	/**
	 * Remove Input/Output object from the cache.<br/>
	 * The method checks if the provided object is equals to the saved one. No saving is done.
	 * @param identifier The id of the {@link IOClass} object
	 * @param obj The {@link IOClass} object
	 * @return If the object has been successfully removed
	 */
	public boolean remove(String identifier, IOClass obj) {
		try {
			LOCK.writeLock().lock();
			return this.files.remove(identifier, obj);
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	/**
	 * Save all the files present in the cache then attempt to flush it.<br/>
	 * If a file fails to save correctly, no error is thrown and all non-saved content is lost.
	 * @param forceSave see {@link IOClass#saveFile(boolean)} for more information
	 */
	public void saveAndflushCache(boolean forceSave) {
		try {
			LOCK.writeLock().lock();
			for(IOClass obj : this.files.values()) {
				try {
					obj.saveFile(forceSave);
				}catch(IOException e) { }
			}
			
			this.files.clear();
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	/**
	 * Save all the files present in the cache then attempt to flush it.<br/>
	 * If a file fails to save correctly, no error is thrown and all non-saved content is lost.
	 */
	public void saveAndflushCache() {
		saveAndflushCache(false);
	}
	
	/**
	 * Flush all files present in the cache. If some of them had non-saved content, everything is lost.
	 */
	public void flushCache() {
		try {
			LOCK.writeLock().lock();
			this.files.clear();
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	/**
	 * Attempt to save an Input/Output object then remove it from the cache.<br/>
	 * If a file fails to save correctly, a {@link IOException} is thrown and the file isn't removed from the cache.<br/>
	 * No check is performed before the removal.
	 * @param identifier The id of the {@link IOClass} object
	 * @param forceSave see {@link IOClass#saveFile(boolean)} for more information
	 * @return If the object has been successfully removed
	 * @throws IOException
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 */
	public boolean saveAndRemove(String identifier, boolean forceSave) throws IOException {
		try {
			LOCK.writeLock().lock();
			syncSave(identifier, forceSave);
			
			return this.files.remove(identifier) != null;
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	/**
	 * Attempt to save an Input/Output object then remove it from the cache.<br/>
	 * If a file fails to save correctly, a {@link IOException} is thrown and the file isn't removed from the cache.<br/>
	 * No check is performed before the removal.
	 * @param identifier The id of the {@link IOClass} object
	 * @return If the object has been successfully removed
	 * @throws IOException
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 */
	public boolean saveAndRemove(String identifier) throws IOException {
		return saveAndRemove(identifier, false);
	}
	
	/**
	 * Attempt to save an Input/Output object then remove it from the cache.<br/>
	 * If a file fails to save correctly, a {@link IOException} is thrown and the file isn't removed from the cache.<br/>
	 * The file's name (not the path) is used as the identifier.<br/>
	 * The method checks if the provided object is equals to the saved one.
	 * @param obj The {@link IOClass} object to be saved and removed
	 * @param forceSave see {@link IOClass#saveFile(boolean)} for more information
	 * @return If the object has been successfully removed
	 * @throws IOException
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 */
	public boolean saveAndRemove(IOClass obj, boolean forceSave) throws IOException {
		try {
			LOCK.writeLock().lock();
			syncSave(obj, forceSave);
			
			return this.files.remove(obj.getFileName(), obj);
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	/**
	 * Attempt to save an Input/Output object then remove it from the cache.<br/>
	 * If a file fails to save correctly, a {@link IOException} is thrown and the file isn't removed from the cache.<br/>
	 * The file's name (not the path) is used as the identifier.<br/>
	 * The method checks if the provided object is equals to the saved one.
	 * @param obj The {@link IOClass} object to be saved and removed
	 * @return If the object has been successfully removed
	 * @throws IOException
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 */
	public boolean saveAndRemove(IOClass obj) throws IOException {
		return saveAndRemove(obj, false);
	}
	
	/**
	 * Attempt to save an Input/Output object then remove it from the cache.<br/>
	 * If a file fails to save correctly, a {@link IOException} is thrown and the file isn't removed from the cache.<br/>
	 * The method checks if the provided object is equals to the saved one.
	 * @param identifier The id of the {@link IOClass} object
	 * @param obj The {@link IOClass} object to be saved and removed
	 * @param forceSave see {@link IOClass#saveFile(boolean)} for more information
	 * @return If the object has been successfully removed
	 * @throws IOException
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 */
	public boolean saveAndRemove(String identifier, IOClass obj, boolean forceSave) throws IOException {
		try {
			LOCK.writeLock().lock();
			final IOClass storedObj = this.files.get(identifier);
			if(storedObj != null && storedObj.equals(obj)) storedObj.saveFile(forceSave);
			else throw new FileNotLoadedException("File with identifier "+identifier+" hasn't been loaded in this instance");
			
			return this.files.remove(identifier, obj);
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	/**
	 * Attempt to save an Input/Output object then remove it from the cache.<br/>
	 * If a file fails to save correctly, a {@link IOException} is thrown and the file isn't removed from the cache.<br/>
	 * The method checks if the provided object is equals to the saved one.
	 * @param identifier The id of the {@link IOClass} object
	 * @param obj The {@link IOClass} object to be saved and removed
	 * @return If the object has been successfully removed
	 * @throws IOException
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 */
	public boolean saveAndRemove(String identifier, IOClass obj) throws IOException {
		return saveAndRemove(identifier, obj, false);
	}
	
	private void syncSave(String identifier, boolean forceSave) throws IOException {
		final IOClass obj = this.files.get(identifier);
		if(obj != null) obj.saveFile(forceSave);
		else throw new FileNotLoadedException("File with identifier "+identifier+" hasn't been loaded in this instance");
	}
	
	/**
	 * Attempt to save an Input/Output object
	 * @param identifier The id of the {@link IOClass} object
	 * @param forceSave see {@link IOClass#saveFile(boolean)} for more information
	 * @throws IOException
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 */
	public void save(String identifier, boolean forceSave) throws IOException {
		try {
			LOCK.readLock().lock();
			syncSave(identifier, forceSave);
		}finally {
			LOCK.readLock().unlock();
		}
		
	}
	
	/**
	 * Attempt to save an Input/Output object
	 * @param identifier The id of the {@link IOClass} object
	 * @throws IOException
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 */
	public void save(String identifier) throws IOException {
		save(identifier, false);
	}
	
	private void syncSave(IOClass obj, boolean forceSave) throws IOException {
		final IOClass storedObj = this.files.get(obj.getFileName());
		if(storedObj != null && storedObj.equals(obj)) storedObj.saveFile(forceSave);
		else throw new FileNotLoadedException("File with identifier "+obj.getFileName()+" hasn't been loaded in this instance");
		
	}
	
	/**
	 * Attempt to save an Input/Output object<br/>
	 * The file's name (not the path) is used as the identifier.
	 * @param obj The {@link IOClass} object to be saved and removed
	 * @param forceSave see {@link IOClass#saveFile(boolean)} for more information
	 * @throws IOException
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 */
	public void save(IOClass obj, boolean forceSave) throws IOException {
		try {
			LOCK.readLock().lock();
			syncSave(obj, forceSave);
		}finally {
			LOCK.readLock().unlock();
		}
	}
	
	/**
	 * Attempt to save an Input/Output object<br/>
	 * The file's name (not the path) is used as the identifier.
	 * @param obj The {@link IOClass} object to be saved and removed
	 * @throws IOException
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 */
	public void save(IOClass obj) throws IOException {
		save(obj, false);
	}
	
	/**
	 * Attempt to save all files present in the cache.<br/>
	 * If a file fails to save correctly, is name and path is add to the {@code errorBuffer}. If the errorBuffer contains values, throws {@link IOException}.
	 * @param forceSave see {@link IOClass#saveFile(boolean)} for more information
	 * @throws IOException
	 */
	public void saveAll(boolean forceSave) throws IOException {
		try {
			LOCK.readLock().lock();
			final StringBuilder errorBuffer = new StringBuilder();
			
			for(IOClass obj : this.files.values()) {
				try {
					obj.saveFile(forceSave);
				}catch(IOException e) {
					errorBuffer.append(obj.getFullName()+", ");
				}
			}
			
			if(errorBuffer.length() != 0) {
				errorBuffer.setLength(errorBuffer.length()-2);
				throw new IOException("Save method encountered errors with files "+errorBuffer.toString());
			}
				
		}finally {
			LOCK.readLock().unlock();
		}
	}
	
	/**
	 * Attempt to save all files present in the cache.<br/>
	 * If a file fails to save correctly, is name and path is add to the {@code errorBuffer}. If the errorBuffer contains values, throws {@link IOException}.
	 * @throws IOException
	 */
	public void saveAll() throws IOException {
		saveAll(false);
	}
	
	/**
	 * Get an Input/Output object stored in cache.
	 * @param identifier The id requested for the {@link IOClass} object
	 * @return the object corresponding to the id
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 * @see IOCacheManager#getAsOptional(String)
	 */
	public IOClass get(String identifier) {
		try {
			LOCK.readLock().lock();
			final IOClass result = this.files.get(identifier);
			if(result != null) return result;
			else throw new FileNotLoadedException("File with identifier "+identifier+" hasn't been loaded in this instance");
		}finally {
			LOCK.readLock().unlock();
		}
	}
	
	/**
	 * Get an Input/Output object stored in cache.<br/>
	 * @param identifier The id requested for the {@link IOClass} object
	 * @return the object corresponding to the id
	 * @see IOCacheManager#get(String)
	 */
	public Optional<IOClass> getAsOptional(String identifier) {
		try {
			LOCK.readLock().lock();
			return Optional.ofNullable(this.files.get(identifier));
		}finally {
			LOCK.readLock().unlock();
		}
	}
	
	/**
	 * Get an Input/Output object stored in cache.<br/>
	 * The method will try to perform a cast to the provided {@code castingClass}.
	 * @param <E> A class extending {@link IOClass}
	 * @param identifier The id requested for the IO object
	 * @param castingClass The class which will cast the object
	 * @return The object casted as {@code <E>}
	 * @throws FileNotLoadedException if no file is found with the provided identifier
	 * @see IOCacheManager#getAsOptional(String, Class)
	 */
	public <E extends IOClass> E get(String identifier, Class<E> castingClass) {
		try {
			LOCK.readLock().lock();
			return castingClass.cast(get(identifier));
		}finally {
			LOCK.readLock().unlock();
		}
	}
	
	/**
	 * Get an Input/Output object stored in cache.<br/>
	 * The method will try to perform a cast to the provided {@code castingClass}.
	 * @param <E> A class extending {@link IOClass}
	 * @param identifier The id requested for the IO object
	 * @param castingClass The class which will cast the object
	 * @return The object casted as {@code <E>}
	 * @see IOCacheManager#get(String, Class)
	 */
	public <E extends IOClass> Optional<E> getAsOptional(String identifier, Class<E> castingClass) {
		try {
			LOCK.readLock().lock();
		
			final IOClass result = this.files.get(identifier);
			if(result == null) return Optional.empty();
			
			try {
				return Optional.ofNullable(castingClass.cast(get(identifier)));
			}catch(ClassCastException e) {
				return Optional.empty();
			}
		}finally {
			LOCK.readLock().unlock();
		}
	}
	
	/**
	 * Return the total of files saved in this instance of {@link IOCacheManager}
	 * @return the number of files
	 */
	public int getFilesNumber() {
		try {
			LOCK.readLock().lock();
			return this.files.size();
		}finally {
			LOCK.readLock().unlock();
		}
	}
	
}
