package database.service;

import com.mysql.cj.jdbc.MysqlDataSource;
import database.exception.InvalidParameterValueException;
import database.exception.NoFreeDatabaseConnectionException;
import database.exception.UnableCreateConnectionException;
import database.helper.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

public class MySQLConnectionPool {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLConnectionPool.class);
    private static final String INIT_POOL_SIZE_MORE_THAN_MAX =
            "Initial pool size cannot be greater than maximum pool size.";
    private final MysqlDataSource dataSource;
    private final Deque<Connection> connectionPool;
    private final int initialPoolSize;
    private final int maxPoolSize;
    private int currentConnections;
    static final String UNABLE_CREATE_CONNECTION = "Unable to create new database connection.";
    static final String NO_FREE_DATABASE_CONNECTION = "All connections are in use. Please try again later.";
    static final String UNABLE_CLOSE_CONNECTION =
            "Unable to close the database connection or pool of connections";

    public MySQLConnectionPool(Settings settings) {
        dataSource = createDataSource(settings);
        initialPoolSize = settings.getInitialPoolSize();
        maxPoolSize = settings.getMaxPoolSize();

        if (initialPoolSize > maxPoolSize) {
            LOG.warn(INIT_POOL_SIZE_MORE_THAN_MAX);
            throw new InvalidParameterValueException(INIT_POOL_SIZE_MORE_THAN_MAX);
        }
        connectionPool = new ArrayDeque<>();
        initializePool();
    }

    public Connection getConnection() {
        if (connectionPool.isEmpty()) {
            if (currentConnections < maxPoolSize) {
                return createConnectionOrThrow();
            } else {
                LOG.warn(NO_FREE_DATABASE_CONNECTION);
                throw new NoFreeDatabaseConnectionException(NO_FREE_DATABASE_CONNECTION);
            }
        } else {
            return connectionPool.poll();
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null && connectionPool.size() < maxPoolSize) {
            connectionPool.add(connection);
        } else if (connection != null) {
            closeConnection(connection);
        }
    }

    public void closePool() {
        while (!connectionPool.isEmpty()) {
            try {
                connectionPool.poll().close();
            } catch (SQLException e) {
                LOG.error(UNABLE_CLOSE_CONNECTION + ": {}", e.getMessage());
            }
        }
        currentConnections = 0;
    }

    public int size() {
        return connectionPool.size();
    }

    private MysqlDataSource createDataSource(Settings settings) {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(settings.getDatabaseBaseUrl() + settings.getDatabaseName());
        ds.setUser(settings.getDatabaseUsername());
        ds.setPassword(settings.getDatabasePassword());
        return ds;
    }

    private void initializePool() {
        for (int i = 0; i < initialPoolSize; i++) {
            try {
                connectionPool.add(createNewConnection());
                currentConnections++;
            } catch (SQLException e) {
                LOG.error(UNABLE_CREATE_CONNECTION + ": {}", e.getMessage());
                throw new UnableCreateConnectionException(UNABLE_CREATE_CONNECTION + ": " + e.getMessage());
            }
        }
    }

    private Connection createNewConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private Connection createConnectionOrThrow() {
        try {
            Connection connection = dataSource.getConnection();
            currentConnections++;
            return connection;
        } catch (SQLException e) {
            LOG.error(UNABLE_CREATE_CONNECTION + ": {}", e.getMessage());
            throw new UnableCreateConnectionException(UNABLE_CREATE_CONNECTION + ": " + e.getMessage());
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOG.error(UNABLE_CLOSE_CONNECTION + ": {}", e.getMessage());
        }
    }
}
