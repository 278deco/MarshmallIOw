package marshmalliow.core.database.security;

import org.mariadb.r2dbc.MariadbConnectionConfiguration;
import org.mariadb.r2dbc.MariadbConnectionFactory;

public class DBCredentialsHolder {

	private final DBCredentials credentials;

	private MariadbConnectionFactory mariaDBFactory;

	public DBCredentialsHolder(DBCredentials credentials) {
		this.credentials = credentials;
	}

	public MariadbConnectionFactory getMariaDBFactory() {
		if(mariaDBFactory == null) {
			synchronized (DBCredentialsHolder.class) {
				if(mariaDBFactory == null) {
					mariaDBFactory = new MariadbConnectionFactory(MariadbConnectionConfiguration.builder()
						.host(credentials.getHost())
						.port(credentials.getPort())
						.username(credentials.getUsername())
						.password(credentials.getPassword())
						.database(credentials.getDatabase())
						.allowMultiQueries(credentials.areMultiQueriesAllowed())
						.autocommit(credentials.isAutoCommitEnabled())
						.connectTimeout(credentials.getConnectionTimeout()).build());
				}
			}
		}

		return this.mariaDBFactory;
	}

}
