package marshmalliow.core.database.objects;

import marshmalliow.core.database.implementation.DBImplementation;

public abstract class DBTable {

	protected final DBImplementation implementation;

	public DBTable(DBImplementation implementation) {
		this.implementation = implementation;
	}

	public abstract String getSQLTableName();

}
