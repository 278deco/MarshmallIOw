package marshmalliow.core.database.implementation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mariadb.r2dbc.api.MariadbConnection;
import org.mariadb.r2dbc.api.MariadbResult;
import org.mariadb.r2dbc.api.MariadbStatement;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import marshmalliow.core.database.utils.DatabaseType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MariaDBImplementation extends DBImplementation {

	private MariadbConnection connection;

	public MariaDBImplementation(Mono<MariadbConnection> dbConnection, boolean autoClose) throws Exception {
		super(autoClose);

		this.connection = dbConnection.block();
	}
	
	@Override
	public void setDatabase(String dbName) throws SQLException {
		this.connection.setDatabase(dbName).block();
	}
	
	/**
	 * Create a new statement for building a statement-based request. <br/>
	 * <strong>This method do not close the connection after return</strong>
	 * @param request the SQL of the statement
	 * @return a new {@link Statement} instance
	 * @throws SQLException
	 * @see MariadbConnection#createStatement(String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MariadbStatement createStatement(String request) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		return this.connection.createStatement(request);
	}

	@Override
	public Flux<MariadbResult> createStatement(String request, List<Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			statement.bind(i, arguments.get(i));
		}


		return statement.execute();
	}

	@Override
	public Flux<MariadbResult> createStatement(String request, Map<String, Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> statement.bind(key, value));

		return statement.execute();
	}

	@Override
	public <E> List<List<E>> select(String request, List<Object> arguments, Class<E> returnType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			statement.bind(i, arguments.get(i));
		}
		
		try {
			return statement.execute().flatMap(result -> {
				return result.map((row, metadata) -> rowIterator(row, returnType));
			}).collectList().block();
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
			return statement.execute().flatMap(result -> {
				return result.map((row, metadata) -> rowIterator(row, returnType));
			}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	@Override
	public List<List<Object>> select(String request, List<Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			statement.bind(i, arguments.get(i));
		}
		
		try {
			return statement.execute().flatMap(result -> {
				return result.map((row, metadata) -> rowIterator(row, Object.class));
			}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public List<List<Object>> select(String request, Map<String, Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> statement.bind(key, value));

		try {
			return statement.execute().flatMap(result -> {
				return result.map((row, metadata) -> rowIterator(row, Object.class));
			}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}


	@Override
	public void insert(String request, List<Object> arguments) throws SQLException {
		executeRequest(request, arguments);
	}

	@Override
	public <E> List<List<E>> insertWithResult(String request, List<String> arguments, Class<E> returnType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			statement.bind(i, arguments.get(i));
		}

		try {
			return statement.execute().flatMap(result -> {
				return result.map((row, metadata) -> rowIterator(row, returnType));
			}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	@Override
	public List<List<Object>> insertWithResult(String request, List<Object> arguments, int expectedReturnNumber) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			statement.bind(i, arguments.get(i));
		}

		try {
			return statement.execute().flatMap(result -> {
				return result.map((row, metadata) -> {
					final List<Object> list = new ArrayList<>();
					for(int i = 0; i < expectedReturnNumber; i++) {
						try {
							list.add(row.get(i, Object.class));
						}catch(IndexOutOfBoundsException e) {
							i = expectedReturnNumber; //Fast break;
						}
					}
					
					return list;
				});
			}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public void insert(String request, Map<String, Object> arguments) throws SQLException {
		executeRequest(request, arguments);
	}

	@Override
	public <E> List<List<E>> insertWithResult(String request, Map<String, Object> arguments, Class<E> returnType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> statement.bind(key, value));

		try {
			return statement.execute().flatMap(result -> {
				return result.map((row, metadata) -> rowIterator(row, returnType));
			}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	@Override
	public List<List<Object>> insertWithResult(String request, Map<String, Object> arguments, int expectedReturnNumber) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> statement.bind(key, value));

		try {
			return statement.execute().flatMap(result -> result.map((row, metadata) -> {
				final List<Object> returnList = new ArrayList<>();
				for(int i = 0; i < expectedReturnNumber; i++) {
					try {
						returnList.add(row.get(i, Object.class));
					}catch(IndexOutOfBoundsException e) {
						i = expectedReturnNumber; //Fast break;
					}
				}

				return returnList;
			})).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public void update(String request, List<Object> arguments) throws SQLException {
		executeRequest(request, arguments);
	}

	@Override
	public void update(String request, Map<String, Object> arguments) throws SQLException {
		executeRequest(request, arguments);
	}

	@Override
	public void delete(String request, List<Object> arguments) throws SQLException {
		executeRequest(request, arguments);
	}

	@Override
	public void delete(String request, Map<String, Object> arguments) throws SQLException {
		executeRequest(request, arguments);
	}

	@Override
	public int count(String request, List<Object> arguments) throws SQLException {
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
	
	private <E> List<E> rowIterator(Row row, Class<E> castingClass) {
		final List<E> rowResult = new ArrayList<>();
		
		int index = 0;
		while(index != -1) {
			try {
				rowResult.add(row.get(index, castingClass));
				
				index+=1;
			}catch(IndexOutOfBoundsException e) {
				index = -1;
			}
		}
		
		return rowResult;
	}
	
	private void executeRequest(String request, List<Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
		try {
			final MariadbStatement statement = this.connection.createStatement(request);
			for(int i = 0; i < arguments.size(); i++) {
				statement.bind(i, arguments.get(i));
			}
	
			statement.execute().then().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	private void executeRequest(String request, Map<String, Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final MariadbStatement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> statement.bind(key, value));

		statement.execute().then().block();

		if(this.autoClose) closeConnection();
	}

	@Override
	public void closeConnection() throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot close a non-existing connection");

		this.connection.close().then().block();
	}

	@Override
	public DatabaseType getDatabaseType() {
		return DatabaseType.MARIADB;
	}
}
