package marshmalliow.core.database.implementation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mariadb.r2dbc.MariadbConnectionFactory;
import org.mariadb.r2dbc.api.MariadbConnection;
import org.mariadb.r2dbc.api.MariadbResult;
import org.mariadb.r2dbc.api.MariadbStatement;

import io.r2dbc.spi.Statement;
import marshmalliow.core.database.utils.DatabaseType;
import reactor.core.publisher.Flux;

public class MariaDBImplementation extends DBImplementation {

	private final MariadbConnectionFactory factory;
	
	private MariadbConnection connection;
	
	public MariaDBImplementation(MariadbConnectionFactory factory, boolean autoClose) throws Exception {
		super(autoClose);
		
		this.factory = factory;
	}

	@Override
	public DBImplementation openConnection() throws SQLException {
		if(this.connection != null) throw new SQLException("Cannot open an already existing connection");
		this.connection = this.factory.create().onErrorComplete(SQLException.class).block();
		
		return this;
	}
	
	/**
	 * Create a new statement for building a statement-based request. </br>
	 * <strong>This method do not close the connection after return</strong>
	 * @param request the SQL of the statement
	 * @return a new {@link Statement} instance
	 * @throws SQLException
	 * @see {@link MariadbConnection#createStatement(String)}
	 */
	public MariadbStatement createStatement(String request) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
		
		return this.connection.createStatement(request);
	}
	
	public Flux<MariadbResult> createStatement(String request, List<String> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
		
		final MariadbStatement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			statement.bind(i, arguments.get(i));
		}
		
		try {
			return statement.execute();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	public Flux<MariadbResult> createStatement(String request, Map<String, Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
		
		final MariadbStatement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> statement.bind(key, value));
		
		try {
			return statement.execute();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	@Override
	public <E> List<List<E>> select(String request, List<String> arguments, Class<E> returnType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
		
		final MariadbStatement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			statement.bind(i, arguments.get(i));
		}
		
		try {
			return statement.execute().flatMap(result -> result.map((row, metadata) -> {
				final List<E> returnList = new ArrayList<E>();
				for(int i = 0; i < arguments.size(); i++) {
					returnList.add(row.get(i, returnType));
				}
				
				return returnList;
			})).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public <E> List<List<E>> select(String request, Map<String, Object> arguments, Class<E> returnType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
		
		final MariadbStatement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> statement.bind(key, value));
		
		try {
			return statement.execute().flatMap(result -> result.map((row, metadata) -> {
				final List<E> returnList = new ArrayList<E>();
				for(int i = 0; i < arguments.size(); i++) {
					returnList.add(row.get(i, returnType));
				}
				
				return returnList;
			})).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public void insert(String request, List<String> arguments) throws SQLException {
		executeRequest(request, arguments);
	}
	
	@Override
	public <E> List<List<E>> insert(String request, List<String> arguments, List<String> returningArguments, Class<E> returnType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			statement.bind(i, arguments.get(i));
		}
		
		statement.execute().subscribe();
		
		try {
			return statement.execute().flatMap(result -> result.map((row, metadata) -> {
				final List<E> returnList = new ArrayList<E>();
				for(int i = 0; i < arguments.size(); i++) {
					returnList.add(row.get(i, returnType));
				}
				
				return returnList;
			})).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public void insert(String request, Map<String, Object> arguments) throws SQLException {
		executeRequest(request, arguments);
	}
	
	@Override
	public <E> List<List<E>> insert(String request, Map<String, Object> arguments, Map<String, Object> returningArguments, Class<E> returnType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
		
		final MariadbStatement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> statement.bind(key, value));
		
		statement.execute().subscribe();
		
		try {
			return statement.execute().flatMap(result -> result.map((row, metadata) -> {
				final List<E> returnList = new ArrayList<E>();
				for(int i = 0; i < arguments.size(); i++) {
					returnList.add(row.get(i, returnType));
				}
				
				return returnList;
			})).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public void update(String request, List<String> arguments) throws SQLException {
		executeRequest(request, arguments);
	}

	@Override
	public void update(String request, Map<String, Object> arguments) throws SQLException {
		executeRequest(request, arguments);
	}

	@Override
	public void delete(String request, List<String> arguments) throws SQLException {
		executeRequest(request, arguments);
	}

	@Override
	public void delete(String request, Map<String, Object> arguments) throws SQLException {
		executeRequest(request, arguments);
	}
	
	private void executeRequest(String request, List<String> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
		
		final MariadbStatement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			statement.bind(i, arguments.get(i));
		}
		
		statement.execute().subscribe();
		
		if(this.autoClose) closeConnection();
	}
	
	private void executeRequest(String request, Map<String, Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
		
		final MariadbStatement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> statement.bind(key, value));
	
		statement.execute().subscribe();
		
		if(this.autoClose) closeConnection();
	}

	@Override
	public int count(String request, List<String> arguments) throws SQLException {
		final MariadbStatement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			statement.bind(i, arguments.get(i));
		}
		
		try {
			return statement.execute().flatMap(result -> result.map(row -> {
				return row.get(0, Integer.class);
			})).blockFirst();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public int count(String request, Map<String, Object> arguments) throws SQLException {
		final MariadbStatement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> statement.bind(key, value));
		
		try {
			return statement.execute().flatMap(result -> result.map(row -> {
				return row.get(0, Integer.class);
			})).blockFirst();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public void closeConnection() throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot close a non-existing connection");
		
		this.connection.close().subscribe();
	}

	@Override
	public DatabaseType getDatabaseType() {
		return DatabaseType.MARIADB;
	}

}
