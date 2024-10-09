package database.service;

import com.mysql.cj.jdbc.MysqlDataSource;
import database.exception.UnableCreateConnectionException;
import database.helper.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class MySQLConnectionPool {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLConnectionPool.class);
    private static final String UNABLE_CREATE_CONNECTION = "Unable to create new database connection.";
    private final MysqlDataSource dataSource;
    private final Queue<Connection> connectionPool;
    private final int MAX_POOL_SIZE;
    private int currentConnections;

    public MySQLConnectionPool(Settings settings, int initialPoolSize, int maxPoolSize) {
        dataSource = new MysqlDataSource();
        dataSource.setURL(settings.getDatabaseBaseUrl() + settings.getDatabaseName());
        dataSource.setUser(settings.getDatabaseUsername());
        dataSource.setPassword(settings.getDatabasePassword());

        connectionPool = new LinkedList<>();
        MAX_POOL_SIZE = maxPoolSize;

        for (int i = 0; i < initialPoolSize; i++) {
            try {
                connectionPool.add(createNewConnection());
            } catch (SQLException e) {
                throw new UnableCreateConnectionException(UNABLE_CREATE_CONNECTION);
            }
        }
    }

    public synchronized Connection getConnection() {
        if (connectionPool.isEmpty()) {
            if (currentConnections < MAX_POOL_SIZE) {
                try {
                    currentConnections++;
                    return createNewConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(UNABLE_CREATE_CONNECTION, e);
                }
            } else {
                LOG.warn("All connections are in use. Please try again later.");
                return null;
            }
        } else {
            return connectionPool.poll();
        }
    }

    public synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            if (connectionPool.size() < MAX_POOL_SIZE) {
                connectionPool.offer(connection);
            } else {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOG.error("Unable to close the database connection.", e);
                }
            }
        }
    }

    public synchronized void closePool() {
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
}
