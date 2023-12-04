package marshmalliow.core.database.security;

import java.util.concurrent.locks.ReentrantLock;

import org.mariadb.r2dbc.MariadbConnectionConfiguration;
import org.mariadb.r2dbc.MariadbConnectionFactory;
import org.mariadb.r2dbc.api.MariadbConnection;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
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
	public Mono<MariadbConnection> getMariaDBConnection() {
		if(this.credentials.isWithPool() && mariaDBPool == null || !this.credentials.isWithPool() && mariaDBConnectionFactory == null) {
			try {
				LOCK.lock();
				if(mariaDBConnectionFactory == null) {
					this.mariaDBConnectionFactory = new MariadbConnectionFactory(MariadbConnectionConfiguration.builder()
						.host(credentials.getHost())
						.port(credentials.getPort())
						.username(credentials.getUsername())
						.password(credentials.getPassword())
						.database(credentials.getDatabase())
						.allowMultiQueries(credentials.areMultiQueriesAllowed())
						.autocommit(credentials.isAutoCommitEnabled())
						.connectTimeout(credentials.getConnectionTimeout()).build());
				}
				
				if(this.credentials.isWithPool() && mariaDBPool == null) {
					this.mariaDBPool = new ConnectionPool(
						ConnectionPoolConfiguration.builder(this.mariaDBConnectionFactory)
						.maxSize(this.credentials.getPoolMaxSize())
						.maxIdleTime(this.credentials.getPoolTimeout())
						.build());
				}
				
				return this.credentials.isWithPool() ? this.mariaDBPool.create().cast(MariadbConnection.class) : this.mariaDBConnectionFactory.create();
			}finally {
				LOCK.unlock();
			}
		}else if(this.credentials.isWithPool() && mariaDBPool != null) {
			return this.mariaDBPool.create().cast(MariadbConnection.class);
		}else if(!this.credentials.isWithPool() && mariaDBConnectionFactory != null) {
			return this.mariaDBConnectionFactory.create();
		}else {
			return Mono.empty();
		}
	}
}
