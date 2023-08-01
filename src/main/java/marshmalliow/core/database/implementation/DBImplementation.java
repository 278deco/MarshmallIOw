package marshmalliow.core.database.implementation;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import marshmalliow.core.database.utils.DatabaseType;

public abstract class DBImplementation { 
	
	protected final boolean autoClose;
	
	public DBImplementation(boolean autoClose) throws Exception {	
		this.autoClose = autoClose;
	}
	
	public abstract DBImplementation openConnection() throws SQLException;
	
	public abstract <E extends Object> List<List<E>> select(String request, List<String> arguments, Class<E> returnType) throws SQLException;
	
	public abstract <E extends Object> List<List<E>> select(String request, Map<String, Object> arguments, Class<E> returnType) throws SQLException;
	
	public abstract void insert(String request, List<String> arguments) throws SQLException;
	
	public abstract <E> List<List<E>> insert(String request, List<String> arguments, List<String> returningArguments, Class<E> returnType) throws SQLException;
	
	public abstract void insert(String request, Map<String, Object> arguments) throws SQLException;
	
	public abstract <E> List<List<E>> insert(String request, Map<String, Object> arguments, Map<String, Object> returningArguments, Class<E> returnType) throws SQLException;
	
	public abstract void update(String request, List<String> arguments) throws SQLException;
	
	public abstract void update(String request, Map<String, Object> arguments) throws SQLException;
	
	public abstract void delete(String request, List<String> arguments) throws SQLException;
	
	public abstract void delete(String request, Map<String, Object> arguments) throws SQLException;
	
	public abstract int count(String request, List<String> arguments) throws SQLException;
	
	public abstract int count(String request, Map<String, Object> arguments) throws SQLException;
	
	public abstract void closeConnection() throws SQLException;
	
	public abstract DatabaseType getDatabaseType();
}
