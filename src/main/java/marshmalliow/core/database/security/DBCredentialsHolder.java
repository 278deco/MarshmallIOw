package marshmalliow.core.database.security;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import org.mariadb.r2dbc.MariadbConnectionConfiguration;
import org.mariadb.r2dbc.MariadbConnectionFactory;
import org.mariadb.r2dbc.api.MariadbConnection;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.pool.PoolMetrics;
import io.r2dbc.spi.Connection;
import reactor.core.publisher.Mono;

public class DBCredentialsHolder {
	private final ReentrantLock LOCK = new ReentrantLock();
	
	private final DBCredentials credentials;

	private MariadbConnectionFactory mariaDBConnectionFactory;
	private ConnectionPool mariaDBPool;

	public DBCredentialsHolder(DBCredentials credentials) {
		this.credentials = credentials;
	}
	
	/**
	 * Get the credentials for a MariaDB database.<br/>
	 * If the connection has already been established before, this method will use cached object to retrieve the instance much faster.<br/>
	 * If the pool usage is activated, the connection pool will be created or retrieved and provide a new connection.
	 * @return a mono containing the {@link MariadbConnection}
	 * @see DBCredentials
	 * @see MariadbConnection
	 */
	public Mono<Connection> getMariaDBConnection() {
		initializeMonoConnection();
		initializeConnectionPool();
		
		if(this.credentials.isWithPool() && mariaDBPool != null) {
			return this.mariaDBPool.create().cast(Connection.class);
		}else if(!this.credentials.isWithPool() && mariaDBConnectionFactory != null) {
			return this.mariaDBConnectionFactory.create().cast(Connection.class);
		}else {
			return Mono.empty();
		}
	}
	
	public void initializeConnectionPool() {
		if(this.credentials.isWithPool() && mariaDBPool == null) {
			try {
                LOCK.lock();
                if(mariaDBPool == null) {
                	if(this.mariaDBConnectionFactory == null) initializeMonoConnection();
                	
                    this.mariaDBPool = new ConnectionPool(
                        ConnectionPoolConfiguration.builder(this.mariaDBConnectionFactory)
                        .maxSize(this.credentials.getPoolMaxSize())
                        .maxIdleTime(this.credentials.getPoolTimeout())
                        .build());
                }
			} finally {
				LOCK.unlock();
			}
		}
	}
	
	private void initializeMonoConnection() {
		if(mariaDBConnectionFactory == null) {
			try {
				LOCK.lock();
				if (mariaDBConnectionFactory == null) {
					this.mariaDBConnectionFactory = new MariadbConnectionFactory(MariadbConnectionConfiguration
							.builder().host(credentials.getHost()).port(credentials.getPort())
							.username(credentials.getUsername()).password(credentials.getPassword())
							.database(credentials.getDatabase()).allowMultiQueries(credentials.areMultiQueriesAllowed())
							.autocommit(credentials.isAutoCommitEnabled())
							.connectTimeout(credentials.getConnectionTimeout()).build());
				}
			} finally {
				LOCK.unlock();
			}
		}
	}
	
	/**
	 * Close all connections to the database.<br/>
	 * If the pool usage is activated, the pool will be closed.
	 * 
	 * @return A {@link Mono} to handle the asynchronous closing of the connection
	 */
	public Mono<Void> closeAllConnections() {
		if(this.credentials.isWithPool() && this.mariaDBPool != null) {
			return this.mariaDBPool.disposeLater();	
		}
		
		return Mono.empty();
	}
	
	public Optional<PoolMetrics> getPoolMetrics() {
		if (this.credentials.isWithPool() && this.mariaDBPool != null) {
			return this.mariaDBPool.getMetrics();
		}

		return Optional.empty();
	}
	
}
