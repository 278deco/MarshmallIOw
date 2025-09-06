package marshmalliow.core.database;

import java.lang.reflect.Constructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marshmalliow.core.database.implementation.DBImplementation;
import marshmalliow.core.database.implementation.MariaDBImplementation;
import marshmalliow.core.database.objects.DBTable;
import marshmalliow.core.database.security.DBCredentials;
import marshmalliow.core.database.security.DBCredentialsHolder;
import marshmalliow.core.database.utils.DatabaseType;

/**
 * Factory storing database credentials and dispatching them to the Java table representation.<br/>
 * The factory can handle every type of databases present in {@link DatabaseType}.
 * @author 278deco
 * @version 1.0.0
 */
public class DBFactory {

	private static final Logger LOGGER = LogManager.getLogger(DBFactory.class);

	
	private static volatile DBFactory instance;
	private volatile DBCredentialsHolder credentialsHolder;

	private DBFactory(DBCredentials credentials) {
		this.credentialsHolder = new DBCredentialsHolder(credentials);
//		if(credentials.isWithPool()) {
//			this.credentialsHolder.initializeConnectionPool();
//		}
	}

	/**
	 * Create the instance of {@link DBFactory} as a singleton
	 * IF no instance is found, a new one is created<br>
	 * @param credentials A instance of {@link DBCredentials}
	 * @return the instance of DBFactory
	 */
	public static DBFactory newInstance(DBCredentials credentials) {
		if(instance == null) {
			synchronized (DBFactory.class) {
				if(instance == null) instance = new DBFactory(credentials);
			}
		}
	
		return instance;
	}

	/**
	 * Get the instance of {@link DBFactory} as a singleton
	 * IF no instance is found, {@code null} is returned
	 * @return the instance of DBFactory or null
	 */
	public static DBFactory get() {
		return instance;
	}
	
	/**
	 * Build and returns a {@link DBTable} class using the provided database type.
	 * @param <E>
	 * @param cls The class extends {@link DBTable}
	 * @param type The type of database we want to communicate with
	 * @param autoClose If the connection to the database is closed after every SQL requests
	 * @return The instance of table or null if the table cannot be built
	 */
	public <E extends DBTable> E getTable(Class<E> cls, DatabaseType type, boolean autoClose) {
		E result;

		switch (type) {
			case MARIADB -> {
				try {
					final Constructor<E> constructor = cls.getConstructor(DBImplementation.class);
					final MariaDBImplementation implementation = new MariaDBImplementation(credentialsHolder.getMariaDBConnection(), autoClose);

					result = constructor.newInstance(implementation);
				}catch(Exception e) {
					LOGGER.warn("Unexpected error while building table class with error", e);
					result = null;
				}
			}
			default -> {
				result = null;
			}
		}

		return result;
	}
	
	/**
	 * Build and returns a {@link DBTable} class using the provided database type.<br/>
	 * This method provide the auto-close philosophy where the connection to the database is closed after every SQL requests.
	 * @param <E>
	 * @param cls The class extends {@link DBTable}
	 * @param type The type of database we want to communicate with
	 * @return The instance of table or null if the table cannot be built
	 */
	public <E extends DBTable> E getTable(Class<E> cls, DatabaseType type) {
		return this.getTable(cls, type, true);
	}
	
	/**
	 * Close all the connections to the database.
	 * @throws RuntimeException
	 */
	public void closeAllConnections() throws RuntimeException {
		this.credentialsHolder.closeAllConnections().block();
	}
}
