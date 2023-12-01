package marshmalliow.core.database.implementation;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import marshmalliow.core.database.utils.DatabaseType;
import reactor.core.publisher.Flux;

public abstract class DBImplementation {

	protected final boolean autoClose;

	public DBImplementation(boolean autoClose) throws Exception {
		this.autoClose = autoClose;
	}

	public abstract void setDatabase(String dbName) throws SQLException;
	
	public abstract <E extends Statement> E createStatement(String request) throws SQLException;
	
	public abstract Flux<? extends Result> createStatement(String request, List<Object> arguments) throws SQLException;
	
	public abstract Flux<? extends Result> createStatement(String request, Map<String, Object> arguments) throws SQLException;
	
	public abstract <E extends Object> List<List<E>> select(String request, List<Object> arguments, Class<E> returnType) throws SQLException;

	public abstract <E extends Object> List<List<E>> select(String request, Map<String, Object> arguments, Class<E> returnType) throws SQLException;
	
	public abstract List<List<Object>> select(String request, List<Object> arguments) throws SQLException;

	public abstract List<List<Object>> select(String request, Map<String, Object> arguments) throws SQLException;

	public abstract void insert(String request, List<Object> arguments) throws SQLException;

	public abstract void insert(String request, Map<String, Object> arguments) throws SQLException;
	
	public abstract <E> List<List<E>> insertWithResult(String request, List<String> arguments, Class<E> returnType) throws SQLException;
	
	public abstract <E> List<List<E>> insertWithResult(String request, Map<String, Object> arguments, Class<E> returnType) throws SQLException;

	public abstract List<List<Object>> insertWithResult(String request, List<Object> arguments, int expectedReturnNumber) throws SQLException;
	
	public abstract List<List<Object>> insertWithResult(String request, Map<String, Object> arguments, int expectedReturnNumber) throws SQLException;

	public abstract void update(String request, List<Object> arguments) throws SQLException;

	public abstract void update(String request, Map<String, Object> arguments) throws SQLException;

	public abstract void delete(String request, List<Object> arguments) throws SQLException;

	public abstract void delete(String request, Map<String, Object> arguments) throws SQLException;

	public abstract int count(String request, List<Object> arguments) throws SQLException;

	public abstract int count(String request, Map<String, Object> arguments) throws SQLException;

	public abstract void closeConnection() throws SQLException;

	public abstract DatabaseType getDatabaseType();
	
	public boolean isAutoClosing() {
		return autoClose;
	}
}
