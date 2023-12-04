package marshmalliow.core.database.implementation;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import marshmalliow.core.database.utils.DatabaseType;
import reactor.core.publisher.Flux;

/**
 * Create an abstract class implementing basic and useful method to fetch and gather data present in a database.
 * @author 278deco
 * @version 1.0.1
 */
public abstract class DBImplementation {

	protected final boolean autoClose;

	/**
	 * Create a new {@link DBImplementation} instance.
	 * @param autoClose If the connection is automatically closed after each requests.
	 * @throws Exception
	 */
	public DBImplementation(boolean autoClose) throws Exception {
		this.autoClose = autoClose;
	}

	/**
	 * Set the scope of all requests executed after this method for a specific database.
	 * @param dbName The name of the database to be used
	 * @throws SQLException
	 */
	public abstract void setDatabase(String dbName) throws SQLException;
	
	/**
	 * Create a basic statement, also called SQL Request, to be performed onto the database.<br/>
	 * As this method returns a {@link Statement}, the statement must be read entirely for the request to be correctly executed.<br/>
	 * <strong>The method {@link #closeConnection()} must be invoked after fetching the response.</strong>
	 * @param <E>
	 * @param request The SQL Request to be executed
	 * @return a response from the database
	 * @throws SQLException
	 */
	public abstract <E extends Statement> E createStatement(String request) throws SQLException;
	
	/**
	 * Create a basic statement, also called SQL Request, to be performed onto the database.<br/>
	 * As this method returns a {@link Statement}, the statement must be read entirely for the request to be correctly executed.<br/>
	 * <strong>The method {@link #closeConnection()} must be invoked after fetching the response.</strong><br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement in their order of appearance in the list.
	 * @param request The SQL Request to be executed
	 * @param arguments a list of the argument needed by the request
	 * @return a {@link Flux} of responses from the database 
	 * @throws SQLException
	 */
	public abstract Flux<? extends Result> createStatement(String request, List<Object> arguments) throws SQLException;
	
	/**
	 * Create a basic statement, also called SQL Request, to be performed onto the database.<br/>
	 * As this method returns a {@link Statement}, the statement must be read entirely for the request to be correctly executed.<br/>
	 * <strong>The method {@link #closeConnection()} must be invoked after fetching the response.</strong><br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement using their respective key.
	 * @param request The SQL Request to be executed
	 * @param arguments a map of the argument needed by the request
	 * @return a {@link Flux} of responses from the database 
	 * @throws SQLException
	 */
	public abstract Flux<? extends Result> createStatement(String request, Map<String, Object> arguments) throws SQLException;
	
	/**
	 * Create a {@code SELECT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/>
	 * The response of the database will be parsed in a list of list. The first list representing all rows' response and the second on
	 * all the requested columns of the table. The resulting objects will be casted to {@code returnType}.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement in their order of appearance in the list.
	 * @param <E>
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @param returnType The type of the resulting objects
	 * @return The response of the database
	 * @throws SQLException
	 */
	public abstract <E extends Object> List<List<E>> select(String request, List<Object> arguments, Class<E> returnType) throws SQLException;

	/**
	 * Create a {@code SELECT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/>
	 * The response of the database will be parsed in a list of list. The first list representing all rows' response and the second on
	 * all the requested columns of the table. The resulting objects will be casted to {@code returnType}.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement using their respective key.
	 * @param <E>
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @param returnType The type of the resulting objects
	 * @return The response of the database
	 * @throws SQLException
	 */
	public abstract <E extends Object> List<List<E>> select(String request, Map<String, Object> arguments, Class<E> returnType) throws SQLException;
	
	/**
	 * Create a {@code SELECT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/>
	 * The response of the database will be parsed in a list of list. The first list representing all rows' response and the second on
	 * all the requested columns of the table. 
	 * The resulting objects are returned as basic {@link Object} and must be casted depending on their real type.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement in their order of appearance in the list.
	 * @param <E>
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @return The response of the database
	 * @throws SQLException
	 */
	public abstract List<List<Object>> select(String request, List<Object> arguments) throws SQLException;
	
	/**
	 * Create a {@code SELECT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/>
	 * The response of the database will be parsed in a list of list. The first list representing all rows' response and the second on
	 * all the requested columns of the table. 
	 * The resulting objects are returned as basic {@link Object} and must be casted depending on their real type.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement using their respective key.
	 * @param <E>
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @return The response of the database
	 * @throws SQLException
	 */
	public abstract List<List<Object>> select(String request, Map<String, Object> arguments) throws SQLException;

	/**
	 * Create a {@code INSERT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement in their order of appearance in the list.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @throws SQLException
	 */
	public abstract void insert(String request, List<Object> arguments) throws SQLException;

	/**
	 * Create a {@code INSERT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement using their respective key.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @throws SQLException
	 */
	public abstract void insert(String request, Map<String, Object> arguments) throws SQLException;
	
	/**
	 * Create a {@code INSERT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/>
	 * The response of the database will be parsed in a list of list.  The first list representing all rows' response and the second on
	 * all the requested columns of the table. The resulting objects will be casted to {@code returnType}.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement in their order of appearance in the list.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @return the response from the database
	 * @throws SQLException
	 */
	public abstract <E> List<List<E>> insertWithResult(String request, List<String> arguments, Class<E> returnType) throws SQLException;
	
	/**
	 * Create a {@code INSERT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/>
	 * The response of the database will be parsed in a list of list.  The first list representing all rows' response and the second on
	 * all the requested columns of the table. The resulting objects will be casted to {@code returnType}.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement in their order of appearance in the list.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @return the response from the database
	 * @throws SQLException
	 */
	public abstract <E> List<List<E>> insertWithResult(String request, Map<String, Object> arguments, Class<E> returnType) throws SQLException;

	/**
	 * Create a {@code INSERT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/>
	 * The response of the database will be parsed in a list of list. The first list representing all rows' response and the second on
	 * all the requested columns of the table.
	 * The resulting objects are returned as basic {@link Object} and must be casted depending on their real type.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement using their respective key.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @return the response from the database
	 * @throws SQLException
	 */
	public abstract List<List<Object>> insertWithResult(String request, List<Object> arguments) throws SQLException;
	
	/**
	 * Create a {@code INSERT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/>
	 * The response of the database will be parsed in a list of list. The first list representing all rows' response and the second on
	 * all the requested columns of the table.
	 * The resulting objects are returned as basic {@link Object} and must be casted depending on their real type.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement using their respective key.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @return the response from the database
	 * @throws SQLException
	 */
	public abstract List<List<Object>> insertWithResult(String request, Map<String, Object> arguments) throws SQLException;

	/**
	 * Create a {@code UPDATE} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement in their order of appearance in the list.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @throws SQLException
	 */
	public abstract void update(String request, List<Object> arguments) throws SQLException;

	/**
	 * Create a {@code DELETE} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement using their respective key.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @throws SQLException
	 */
	public abstract void update(String request, Map<String, Object> arguments) throws SQLException;
	
	/**
	 * Create a {@code DELETE} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement in their order of appearance in the list.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @throws SQLException
	 */
	public abstract void delete(String request, List<Object> arguments) throws SQLException;
	
	/**
	 * Create a {@code DELETE} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement using their respective key.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @throws SQLException
	 */
	public abstract void delete(String request, Map<String, Object> arguments) throws SQLException;
	
	/**
	 * Create a {@code COUNT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement in their order of appearance in the list.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @return The result of the count method 
	 * @throws SQLException
	 */
	public abstract int count(String request, List<Object> arguments) throws SQLException;

	/**
	 * Create a {@code COUNT} SQL Request to be performed onto the database.<br/>
	 * This method will close itself if the {@link #autoClose} parameter is true, else {@link #closeConnection()} must be invoked.<br/><br/>
	 * The SQL Request will be prepared and the arguments will be bound to the statement using their respective key.
	 * @param request The SQL Request to be executed
	 * @param arguments The arguments used in the statement
	 * @return The result of the count method 
	 * @throws SQLException
	 */
	public abstract int count(String request, Map<String, Object> arguments) throws SQLException;

	/**
	 * Close the current connection established with database.<br/>
	 * This method doesn't needs to be called from method handling the closing of the database when {@link #autoClose} is on.<br/>
	 * After the connection has been closed, multiple calls of this method will not do anything.
	 * @throws SQLException
	 */
	public abstract void closeConnection() throws SQLException;

	public abstract DatabaseType getDatabaseType();
	
	/**
	 * If the connection is automatically closed after each requests.
	 * @return autoclose's parameter
	 */
	public boolean isAutoClosing() {
		return autoClose;
	}
}
