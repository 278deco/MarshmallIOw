package marshmalliow.core.database.security;

import java.time.Duration;

import org.mariadb.r2dbc.util.HostAddress;

public class DBCredentials {

	private final String database;
	private final HostAddress host;
	private final String username;
	private final String password;
	private final Duration connectionTimeout;
	private final boolean autoCommit;
	private final boolean allowMultiQueries;

	private DBCredentials(Builder builder) {
		this.host = builder.host;
		this.database = builder.database;
		this.username = builder.username;
		this.password = builder.password;
		this.connectionTimeout = builder.connectionTimeout;
		this.autoCommit = builder.autoCommit;
		this.allowMultiQueries = builder.allowMultiQueries;
	}

	public static final DBCredentials.Builder builder() {
		return new DBCredentials.Builder();
	}

	public HostAddress getHostAddress() {
		return host;
	}

	public String getHost() {
		return this.host.getHost();
	}

	public int getPort() {
		return this.host.getPort();
	}

	public String getDatabase() {
		return database;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Duration getConnectionTimeout() {
		return connectionTimeout;
	}

	public boolean isAutoCommitEnabled() {
		return autoCommit;
	}

	public boolean areMultiQueriesAllowed() {
		return allowMultiQueries;
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
				this.allowMultiQueries == obj.allowMultiQueries;
	}

	public static final class Builder implements Cloneable {

		private String database;
		private HostAddress host;
		private String username;
		private String password;
		private Duration connectionTimeout;
		private boolean autoCommit = true;
		private boolean allowMultiQueries = false;

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

		public DBCredentials build() {
			if(this.host == null ) throw new IllegalArgumentException("host must not be null");

			return new DBCredentials(this);
		}


		@Override
		protected Builder clone() throws CloneNotSupportedException {
			return (Builder)super.clone();
		}
	}
}
