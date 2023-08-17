package marshmalliow.core.database;

import java.lang.reflect.Constructor;

import marshmalliow.core.database.implementation.DBImplementation;
import marshmalliow.core.database.implementation.MariaDBImplementation;
import marshmalliow.core.database.objects.DBTable;
import marshmalliow.core.database.security.DBCredentials;
import marshmalliow.core.database.security.DBCredentialsHolder;
import marshmalliow.core.database.utils.DatabaseType;

public class DBFactory {

	private static volatile DBFactory instance;
	private volatile DBCredentialsHolder credentialsHolder;

	private DBFactory(DBCredentials credentials) {
		this.credentialsHolder = new DBCredentialsHolder(credentials);
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

	public <E extends DBTable> E getTable(Class<E> cls, DatabaseType type, boolean autoClose) {
		E result;

		switch (type) {
			case MARIADB -> {
				try {
					final Constructor<E> constructor = cls.getConstructor(DBImplementation.class);
					final MariaDBImplementation implementation = new MariaDBImplementation(credentialsHolder.getMariaDBFactory(), autoClose);

					result = constructor.newInstance(implementation);
				}catch(Exception e) {
					result = null;
				}
			}
			default -> {
				result = null;
			}
		}

		return result;
	}

	public <E extends DBTable> E getTable(Class<E> cls, DatabaseType type) {
		return this.getTable(cls, type, true);
	}
}
