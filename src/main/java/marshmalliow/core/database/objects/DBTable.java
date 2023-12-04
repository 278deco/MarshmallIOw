package marshmalliow.core.database.objects;

import marshmalliow.core.database.implementation.DBImplementation;

public abstract class DBTable {

	protected final DBImplementation implementation;

	public DBTable(DBImplementation implementation) {
		this.implementation = implementation;
	}

	/**
	 * The name of the SQL Table. Must be exactly the same as the one present in the database.
	 * @return the SQL table's name. 
	 */
	public abstract String getSQLTableName();

}
