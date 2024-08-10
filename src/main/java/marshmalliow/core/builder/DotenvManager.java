package marshmalliow.core.builder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import marshmalliow.core.file.dotenv.DotenvContainer;
import marshmalliow.core.io.DotenvLexer;
import marshmalliow.core.io.DotenvParser;
import marshmalliow.core.objects.Directory;

/**
 * The {@link DotenvManager} is a singleton that allows to manage the environment variables.<br/>
 * This class allows to add environment variables from the system or from a .env file.<br/>
 * The environment variables are stored in {@link DotenvContainer}
 * 
 * @see DotenvContainer
 * @version 1.0.0
 * @since 0.3.0
 * @author 278deco
 */
public class DotenvManager {

	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	
	private static volatile DotenvManager instance;
	
	private final DotenvContainer envMapping = new DotenvContainer();
	private boolean systemEnvironment = false;
	
	/**
	 * Get the instance of {@link DotenvManager} as a singleton
	 * IF no instance is found, create it
	 * @return the instance of DotenvManager
	 */
	public static DotenvManager get() {
		if(instance == null) {
			synchronized (DotenvManager.class) {
				if(instance == null) instance = new DotenvManager();
			}
		}
		return instance;
	}
	
	/**
	 * Add all the environment variable present on the system to the mapping of the {@link DotenvManager}.<br/>
	 * Multiple calls to this method will not add the system environment again.
	 * 
	 * @see System#getenv()
	 */
	public void addSystemEnvironment() {
		try {
			LOCK.writeLock().lock();
			if(systemEnvironment) return;
			
			systemEnvironment = true;
			envMapping.addAll(System.getenv());
		} finally {
			LOCK.writeLock().unlock();
		}		
	}
	
	/**
	 * Add the content of a .env file to the mapping of the {@link DotenvManager}.<br/>
	 * The file is located at the provided path and has the provided name.<br/>
	 * If the file is not found, a {@link FileNotFoundException} is thrown.
	 * 
	 * @param path The path of the directory containing the .env file
	 * @param fileName The name of the .env file
	 * @throws IOException
	 */
	public void addEnvFile(Path path, String fileName) throws IOException {
		try {
			LOCK.writeLock().lock();
			
			final String fullName = fileName.endsWith(".env") ? fileName : fileName+".env";
			final Path fullPath = path.resolve(fullName);
			
			if(!Files.exists(fullPath)) throw new FileNotFoundException();
			
			BufferedReader reader = null;
			try {
				reader = Files.newBufferedReader(fullPath);
				
				final DotenvParser parser = new DotenvParser(new DotenvLexer(reader));
				envMapping.addAll(parser.parse());
			}finally {
				if(reader != null) reader.close();
			}
			
		}finally {
			LOCK.writeLock().unlock();
		}
	}
	
	/**
	 * Add the content of a .env file to the mapping of the
	 * {@link DotenvManager}.<br/>
	 * The file is located at the provided directory and has the provided name.<br/>
	 * If the file is not found, a {@link FileNotFoundException} is thrown.
	 * 
	 * @param dir      The directory containing the .env file
	 * @param fileName The name of the .env file
	 * @throws IOException 
	 * @see #addEnvFile(Path, String)
	 */
	public void addEnvFile(Directory dir, String fileName) throws IOException {
		addEnvFile(dir.getPath(), fileName);
	}
	
	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * If the key is not found, return null
	 * 
	 * @param key The key of the environment variable
	 * @return The value of the environment variable
	 */
	public String getEnv(String key) {
		try {
			LOCK.readLock().lock();
			return envMapping.getEnv(key);
		} finally {
			LOCK.readLock().unlock();
		}
	}

	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * If the key is not found, return the <code>defaultValue</code>
	 * 
	 * @param key The key of the environment variable
	 * @param defaultValue The default value if the key is not found
	 * @return The value of the environment variable
	 */
	public String getEnvOrDefault(String key, String defaultValue) {
		try {
			LOCK.readLock().lock();
			return envMapping.getEnvOrDefault(key, defaultValue);
		} finally {
			LOCK.readLock().unlock();
		}
	}

	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * If the key is not found, return the value of the <code>otherKey</code>
	 * 
	 * @param key      The key of the environment variable
	 * @param otherKey The key to get the value from if the key is not found
	 * @return The value of the environment variable
	 */
	public String getEnvOrElse(String key, String otherKey) {
        try {
            LOCK.readLock().lock();
            return envMapping.getEnvOrElse(key, otherKey);
        } finally {
            LOCK.readLock().unlock();
        }
	}
	
	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * The key is then casted to an integer. If the key doesn't exist or cannot be 
	 * parsed, returns {@link Optional#empty()}.
	 * 
	 * @param key The key of the environment variable
	 * @return An {@link Optional} containing the value of the environment variable 
	 * 		   as an integer
	 * @see Integer#parseInt(String)
	 */
	public Optional<Integer> getEnvAsInt(String key) {
		try {
			LOCK.readLock().lock();
			return envMapping.getEnvAsInt(key);
		} finally {
			LOCK.readLock().unlock();
		}
	}

	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * The key is then casted to a double. If the key doesn't exist or cannot be
	 * parsed, returns {@link Optional#empty()}.
	 * 
	 * @param key The key of the environment variable
	 * @return An {@link Optional} containing the value of the environment variable
	 *         as a double
	 * @see Double#parseDouble(String)
	 */
	public Optional<Double> getEnvAsDouble(String key) {
		try {
			LOCK.readLock().lock();
			return envMapping.getEnvAsDouble(key);
		} finally {
			LOCK.readLock().unlock();
		}
	}

	/**
	 * Get the value of the environment variable associated with the key.<br/>
	 * The key is then casted to a boolean. If the key doesn't exist or cannot be
	 * parsed, returns {@link Optional#empty()}.<br/>
	 * <p>
	 * As described by {@link Boolean#parseBoolean(String)}, the value is considered true 
	 * if it is not case-sensitive "true". Otherwise, it is false.
	 * 
	 * @param key The key of the environment variable
	 * @return An {@link Optional} containing the value of the environment variable
	 *         as a boolean
	 * @see Boolean#parseBoolean(String)
	 */
	public Optional<Boolean> getEnvAsBoolean(String key) {
		try {
			LOCK.readLock().lock();
			return envMapping.getEnvAsBoolean(key);
		} finally {
			LOCK.readLock().unlock();
		}
	}

	/**
	 * Get the mapping of the environment variables.
	 * 
	 * @return The mapping of the environment variables
	 */
	public Map<String, String> getEnvMapping() {
		try {
            LOCK.readLock().lock();
            return envMapping.getEnvMapping();
        } finally {
            LOCK.readLock().unlock();
        }
	} 
	
}
