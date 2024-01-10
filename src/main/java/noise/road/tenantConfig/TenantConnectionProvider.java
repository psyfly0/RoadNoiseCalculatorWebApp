package noise.road.tenantConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class TenantConnectionProvider implements MultiTenantConnectionProvider{
	
	private DataSource datasource;
	
	public TenantConnectionProvider(DataSource dataSource) {
        this.datasource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return datasource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();
        connection.createStatement()
                .execute(String.format("SET SCHEMA \"%s\";", tenantIdentifier));
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.createStatement()
                .execute(String.format("SET SCHEMA \"%s\";", TenantIdentifierResolver.DEFAULT_TENANT));
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}
/*
	@Autowired
	DataSource dataSource;
	
	@Override
	public Connection getAnyConnection() throws SQLException {
		return getConnection("PUBLIC");
	}
	
	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		connection.close();
	}
	
	@Override
	public Connection getConnection(String schema) throws SQLException {
		Connection connection = dataSource.getConnection();
		connection.setSchema(schema);
		return connection;
	}
	
	@Override
	public void releaseConnection(String s, Connection connection) throws SQLException {
		connection.setSchema("PUBLIC");
		connection.close();
	}
	
	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
	}

	@Override
	public @UnknownKeyFor @NonNull @Initialized boolean isUnwrappableAs(
			@UnknownKeyFor @NonNull @Initialized Class<@UnknownKeyFor @NonNull @Initialized ?> unwrapType) {
		return false;
	}	

	@Override
	public <T> @UnknownKeyFor @NonNull @Initialized T unwrap(
			@UnknownKeyFor @NonNull @Initialized Class<@UnknownKeyFor @NonNull @Initialized T> unwrapType) {
		return null;
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}
}*/
