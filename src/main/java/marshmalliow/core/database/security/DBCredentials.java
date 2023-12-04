package marshmalliow.core.database.security;

import java.time.Duration;

import org.mariadb.r2dbc.util.HostAddress;

/**
 * Stores the credentials and basics parameters used by a database instance.<br/>
 * The credentials is an abstract object used in {@link DBCredentialsHolder} to create the databases' instances own credentials classes.
 * @author 278deco
 * @version 1.0.0
 */
public class DBCredentials {

	private final String database;
	private final HostAddress host;
	private final String username;
	private final String password;
	private final Duration connectionTimeout;
	private final boolean autoCommit;
	private final boolean allowMultiQueries;
	private boolean withPool = false;
	private Duration poolTimeout;
	private int poolMaxSize;

	private DBCredentials(Builder builder) {
		this.host = builder.host;
		this.database = builder.database;
		this.username = builder.username;
		this.password = builder.password;
		this.connectionTimeout = builder.connectionTimeout;
		this.autoCommit = builder.autoCommit;
		this.allowMultiQueries = builder.allowMultiQueries;
		this.withPool = builder.withPool;
		this.poolTimeout = builder.poolTimeout;
		this.poolMaxSize = builder.poolMaxSize;
	}

	/**
	 * Create a new instance of {@link DBCredentials} with the help of its builder.
	 * @return a {@link DBCredentials.Builder}
	 */
	public static final DBCredentials.Builder builder() {
		return new DBCredentials.Builder();
	}

	/**
	 * Get the host of the database as a {@link HostAddress}.<br/>
	 * The host address is an object provided by mariadb's library but can be used for any databases.<br/>
	 * The object stores the address and port pair.
	 * @return the host of the database
	 * @see HostAddress
	 */
	public HostAddress getHostAddress() {
		return host;
	}

	/**
	 * Get the address of the databases's host as a {@link String}.<br/>
	 * This method doesn't return the port of the host.
	 * @return the address of the database's host 
	 * @see HostAddress#getHost()
	 */
	public String getHost() {
		return this.host.getHost();
	}

	/**
	 * Get the port of the databases's host as a {@link String}.<br/>
	 * This method doesn't return the address of the host.
	 * @return the port of the database's host 
	 * @see HostAddress#getPort()
	 */
	public int getPort() {
		return this.host.getPort();
	}

	/**
	 * Get the name of the database. Can be {@code empty} or {@code null}.
	 * @return The database's name
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Get the username of the database. Can be {@code empty} or {@code null}.
	 * @return The database's username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get the password of the database. Can be {@code empty} or {@code null}.
	 * @return The database's password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Get the connection timeout of the database as a {@link Duration}. Can be {@code empty} or {@code null}.<br/>
	 * The connection timeout parameter define for how long the connection between the program and the database
	 * can stay open before being shutdown.
	 * @return The database's connection timeout
	 */
	public Duration getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * Get if the auto-commit property is enable on the database. By default the property is {@code true}.<br/>
	 * 
	 * @return The database's auto-commit property
	 */
	public boolean isAutoCommitEnabled() {
		return autoCommit;
	}

	/**
	 * Get if the multi-queries property is enable on the database. By default the property is {@code true}.
	 * @return If multi-queries are allowed
	 */
	public boolean areMultiQueriesAllowed() {
		return allowMultiQueries;
	}
	
	/**
	 * Configure the program to use a pool of connections for better and faster communication with the database. 
	 * By default the property is {@code false}.<br/>
	 * If this property is false, properties {@link #getPoolMaxSize()} and {@link #getPoolTimeout()} have no usage.
	 * @return If a pool of connection will be used
	 */
	public boolean isWithPool() {
		return withPool;
	}
	
	/**
	 * Get the maximum number of connections authorized in a single pool.<br/>
	 * If the property {@link #isWithPool()} is false, this property have no usage.
	 * @return the maximum of connections in a pool
	 */
	public int getPoolMaxSize() {
		return poolMaxSize;
	}
	
	/**
	 * Get the connection timeout for the connections present in a pool.<br/>
	 * If the property {@link #isWithPool()} is false, this property have no usage.<br/>
	 * This property differs from {@link #getConnectionTimeout()}, because it controls 
	 * the maximum idle time of a connection present in a pool.
	 * @return the maximum of connections in a pool
	 */
	public Duration getPoolTimeout() {
		return poolTimeout;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		return obj instanceof DBCredentials && areEquals((DBCredentials)obj);
	}

	private final boolean areEquals(DBCredentials obj) {
		return this.host == obj.host &&
				this.database == obj.database &&
				this.username == obj.username &&
				this.password == obj.password &&
				this.connectionTimeout == obj.connectionTimeout &&
				this.autoCommit == obj.autoCommit &&
				this.allowMultiQueries == obj.allowMultiQueries &&
				this.withPool == obj.withPool &&
				this.poolMaxSize == obj.poolMaxSize &&
				this.poolTimeout == obj.poolTimeout;
	}

	public static final class Builder implements Cloneable {

		private String database;
		private HostAddress host;
		private String username;
		private String password;
		private Duration connectionTimeout;
		private boolean autoCommit = true;
		private boolean allowMultiQueries = true;
		private boolean withPool = false;
		private Duration poolTimeout;
		private int poolMaxSize;

		private Builder() {}

		public Builder database(String database) {
			this.database = database;
			return this;
		}

		public Builder host(String host, int port) {
			this.host = new HostAddress(host, port);
			return this;
		}

		public Builder host(HostAddress address) {
			this.host = address;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}


		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder connectionTimeout(Duration timeout) {
			this.connectionTimeout = timeout;
			return this;
		}

		public Builder autoCommit(boolean value) {
			this.autoCommit = value;
			return this;
		}

		public Builder allowMultiQueries(boolean value) {
			this.allowMultiQueries = value;
			return this;
		}
		
		public Builder withPool(boolean value) {
			this.withPool = value;
			return this;
		}
		
		public Builder poolMaxIdleTime(Duration duration) {
			this.poolTimeout = duration;
			return this;
		}
		
		public Builder poolMaxSize(int size) {
			this.poolMaxSize = size;
			return this;
		}

		public DBCredentials build() {
			if(this.host == null ) throw new IllegalArgumentException("Host must not be null");

			return new DBCredentials(this);
		}


		@Override
		protected Builder clone() throws CloneNotSupportedException {
			return (Builder)super.clone();
		}
	}
}
