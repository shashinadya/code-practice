package database.service;

import com.mysql.cj.jdbc.MysqlDataSource;
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
    private final MysqlDataSource dataSource;
    private final Deque<Connection> connectionPool;
    private final int initialPoolSize;
    private final int maxPoolSize;
    private int currentConnections;
    static final String UNABLE_CREATE_CONNECTION = "Unable to create new database connection.";
    static final String NO_FREE_DATABASE_CONNECTION = "All connections are in use. Please try again later.";

    public MySQLConnectionPool(Settings settings) {
        dataSource = createDataSource(settings);
        this.initialPoolSize = settings.getInitialPoolSize();
        this.maxPoolSize = settings.getMaxPoolSize();
        this.connectionPool = new ArrayDeque<>();
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
            connectionPool.offer(connection);
        } else if (connection != null) {
            closeConnection(connection);
        }
    }

    public void closePool() {
        while (!connectionPool.isEmpty()) {
            try {
                connectionPool.poll().close();
            } catch (SQLException e) {
                LOG.error("Unable to close the database connection from the pool.", e);
            }
        }
        currentConnections = 0;
    }

    public int size() {
        return connectionPool.size();
    }

    private Connection createNewConnection() throws SQLException {
        return dataSource.getConnection();
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
                throw new UnableCreateConnectionException(UNABLE_CREATE_CONNECTION);
            }
        }
    }

    private Connection createConnectionOrThrow() {
        try {
            currentConnections++;
            return createNewConnection();
        } catch (SQLException e) {
            LOG.error(UNABLE_CREATE_CONNECTION);
            throw new UnableCreateConnectionException(UNABLE_CREATE_CONNECTION + ": " + e.getMessage());
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOG.error("Unable to close the database connection.", e);
        }
    }
}
