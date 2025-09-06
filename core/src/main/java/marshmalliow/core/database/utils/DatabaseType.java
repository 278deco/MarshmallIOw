package marshmalliow.core.database.utils;

/**
 * Database type enumeration
 * <p>
 * This enumeration is used to determine which database type to use in the application.
 * @author 278deco
 * @version 1.0.0
 * @since 0.1.0
 */
public enum DatabaseType {
	/**
	 * Mariadb database type
	 * @see org.mariadb.r2dbc.MariadbConnection
	 * @see <a href="https://mariadb.com/docs/server/connect/programming-languages/java/">MariaDB Java Connector</a>
	 * 
	 */
	MARIADB;
}
