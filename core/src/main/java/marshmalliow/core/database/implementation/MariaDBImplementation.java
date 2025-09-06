	package marshmalliow.core.database.implementation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import marshmalliow.core.database.objects.NullValue;
import marshmalliow.core.database.utils.DatabaseType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MariaDBImplementation extends DBImplementation {

	private Connection connection;

	public MariaDBImplementation(Mono<Connection> dbConnection, boolean autoClose) throws Exception {
		super(autoClose);

		this.connection = dbConnection.block();
	}
	
	@Override
	public void setDatabase(String dbName) throws SQLException {
		Mono.from(
			this.connection.createStatement("USE "+dbName+";").execute()
		).subscribe();
	
	}
	
	/**
	 * Create a new statement for building a statement-based request. <br/>
	 * <strong>This method do not close the connection after return</strong>
	 * @param request the SQL of the statement
	 * @return a new {@link Statement} instance
	 * @throws SQLException
	 * @see Connection#createStatement(String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Statement createStatement(String request) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		return this.connection.createStatement(request);
	}

	@Override
	public Flux<Result> createStatement(String request, List<Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			if (arguments.get(i) instanceof NullValue) statement.bindNull(i, ((NullValue) arguments.get(i)).getType());
			else statement.bind(i, arguments.get(i));
		}

		return Flux.from(statement.execute());
	}

	@Override
	public Flux<Result> createStatement(String request, Map<String, Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> {
			if (arguments.get(key) instanceof NullValue) statement.bindNull(key, ((NullValue) arguments.get(key)).getType());
            else statement.bind(key, value);
		});

		return Flux.from(statement.execute());
	}

	@Override
	public <E> List<List<E>> select(String request, List<Object> arguments, Class<E> returnType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			if (arguments.get(i) instanceof NullValue) statement.bindNull(i, ((NullValue) arguments.get(i)).getType());
			else statement.bind(i, arguments.get(i));
		}
		
		try {
			return Flux.from(statement.execute())
					.flatMap(result -> {
						return result.map((row, metadata) -> rowIterator(row, returnType));
					}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public <E> List<List<E>> select(String request, Map<String, Object> arguments, Class<E> returnType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> {
			if (arguments.get(key) instanceof NullValue) statement.bindNull(key, ((NullValue) arguments.get(key)).getType());
            else statement.bind(key, value);
		});
		
		try {
			return Flux.from(statement.execute())
					.flatMap(result -> {
						return result.map((row, metadata) -> rowIterator(row, returnType));
					}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	@Override
	public List<List<Object>> select(String request, List<Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			if (arguments.get(i) instanceof NullValue) statement.bindNull(i, ((NullValue) arguments.get(i)).getType());
			else statement.bind(i, arguments.get(i));
		}
		
		try {
			return Flux.from(statement.execute())
					.flatMap(result -> {
						return result.map((row, metadata) -> rowIterator(row, Object.class));
					}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public List<List<Object>> select(String request, Map<String, Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> {
			if (arguments.get(key) instanceof NullValue) statement.bindNull(key, ((NullValue) arguments.get(key)).getType());
            else statement.bind(key, value);
		});

		try {
			return Flux.from(statement.execute())
			.flatMap(result -> {
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
	public <E> List<List<E>> insertWithResult(String request, List<Object> arguments, Class<E> returnType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			if (arguments.get(i) instanceof NullValue) statement.bindNull(i, ((NullValue) arguments.get(i)).getType());
			else statement.bind(i, arguments.get(i));
		}

		try {
			return Flux.from(statement.execute())
					.flatMap(result -> {
						return result.map((row, metadata) -> rowIterator(row, returnType));
					}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	@Override
	public List<List<Object>> insertWithResult(String request, List<Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			if (arguments.get(i) instanceof NullValue) statement.bindNull(i, ((NullValue) arguments.get(i)).getType());
			else statement.bind(i, arguments.get(i));
		}

		try {
			return Flux.from(statement.execute())
					.flatMap(result -> {
						return result.map((row, metadata) -> rowIterator(row, Object.class));
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

		final Statement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> {
			if (arguments.get(key) instanceof NullValue) statement.bindNull(key, ((NullValue) arguments.get(key)).getType());
            else statement.bind(key, value);
		});

		try {
			return Flux.from(statement.execute())
					.flatMap(result -> {
						return result.map((row, metadata) -> rowIterator(row, returnType));
					}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	@Override
	public List<List<Object>> insertWithResult(String request, Map<String, Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> {
			if (arguments.get(key) instanceof NullValue) statement.bindNull(key, ((NullValue) arguments.get(key)).getType());
            else statement.bind(key, value);
		});

		try {
			return Flux.from(statement.execute())
					.flatMap(result -> {
						return result.map((row, metadata) -> rowIterator(row, Object.class));
					}).collectList().block();
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
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		for(int i = 0; i < arguments.size(); i++) {
			if (arguments.get(i) instanceof NullValue) statement.bindNull(i, ((NullValue) arguments.get(i)).getType());
			else statement.bind(i, arguments.get(i));
		}

		try {
			return Flux.from(statement.execute()).flatMap(result -> result.map(row -> {
				return row.get(0, Integer.class);
			})).blockFirst();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	@Override
	public int count(String request, Map<String, Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> {
			if (arguments.get(key) instanceof NullValue) statement.bindNull(key, ((NullValue) arguments.get(key)).getType());
            else statement.bind(key, value);
		});

		try {
			return Flux.from(statement.execute()).flatMap(result -> result.map(row -> {
				return row.get(0, Integer.class);
			})).blockFirst();
		}finally {	
			if(this.autoClose) closeConnection();
		}
	}
	
	public void batch(List<String> requests) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		try { 
			final Batch batch = this.connection.createBatch();
			
			for (String request : requests) {
				batch.add(request);
			}
			
			Mono.from(batch.execute()).subscribe();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	public List<List<Object>> batchWithResult(List<String> requests) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
		try {
			final Batch batch = this.connection.createBatch();
			
			for(String request : requests) {
				batch.add(request);
			}
		
			return Flux.from(batch.execute())
					.flatMap(result -> {
						return result.map((row, metadata) -> rowIterator(row, Object.class));
					}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	public <E> List<List<E>> batchWithResult(List<String> requests, Class<E> castingType) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Batch batch = this.connection.createBatch();
		
		for (String request : requests) {
			batch.add(request);
		}
		
		try {
			return Flux.from(batch.execute())
					.flatMap(result -> {
						return result.map((row, metadata) -> rowIterator(row, castingType));
					}).collectList().block();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}
	
	public void preparedBatch(List<String> requests, List<List<Object>> arguments) throws SQLException {
        if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");
        if(requests.size() != arguments.size()) throw new IllegalArgumentException("Incorrect number of arguments and requests for the prepared batch");

        Mono.from(this.connection.beginTransaction())
        	.thenMany(
        		Flux.range(0, requests.size())
        		.flatMap(i -> {
        			final Statement statement = this.connection.createStatement(requests.get(i));
        			final List<Object> argObj = arguments.get(i);
        			
        			for(int argidx = 0; argidx < argObj.size(); argidx++) {
						if (argObj.get(argidx) instanceof NullValue) statement.bindNull(argidx, ((NullValue) argObj.get(argidx)).getType());
						else statement.bind(argidx, argObj.get(argidx));
					}
        			
        			return Mono.from(statement.execute());
        		})
        	)
        	.then(Mono.from(this.connection.commitTransaction()))
        	.doOnError(error -> {
        		this.connection.rollbackTransaction();
        	})
        	.doFinally(sig -> {
        		if(this.autoClose)
					try {
						closeConnection();
					} catch (SQLException e) {
						throw new RuntimeException("Error while closing connection after prepared batch", e);
					}
        	})
        	.subscribe();
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
			final Statement statement = this.connection.createStatement(request);
			for(int i = 0; i < arguments.size(); i++) {
				if (arguments.get(i) instanceof NullValue) statement.bindNull(i, ((NullValue) arguments.get(i)).getType());
				else statement.bind(i, arguments.get(i));
			}
	
			Mono.from(
				statement.execute()
			).subscribe();
		}finally {
			if(this.autoClose) closeConnection();
		}
	}

	private void executeRequest(String request, Map<String, Object> arguments) throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot execute select method if no connection is open");

		final Statement statement = this.connection.createStatement(request);
		arguments.forEach((key, value) -> {
			if (arguments.get(key) instanceof NullValue) statement.bindNull(key, ((NullValue) arguments.get(key)).getType());
            else statement.bind(key, value);
		});

		Mono.from(
			statement.execute()
		).subscribe();

		if(this.autoClose) closeConnection();
	}

	@Override
	public void closeConnection() throws SQLException {
		if(this.connection == null) throw new SQLException("Cannot close a non-existing connection");

		Mono.from(
			this.connection.close()
		).subscribe();
	}

	@Override
	public DatabaseType getDatabaseType() {
		return DatabaseType.MARIADB;
	}
}
