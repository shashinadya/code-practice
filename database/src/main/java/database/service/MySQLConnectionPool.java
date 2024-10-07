package database.service;

import com.mysql.cj.jdbc.MysqlDataSource;
import database.helper.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class MySQLConnectionPool {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLConnectionPool.class);
    private final MysqlDataSource dataSource;
    private final Queue<PooledConnection> connectionPool;
    private final int MAX_POOL_SIZE;
    private int currentConnections;

    public MySQLConnectionPool(Settings settings, int initialPoolSize, int maxPoolSize) throws SQLException {
        dataSource = new MysqlDataSource();
        dataSource.setURL(settings.getDatabaseBaseUrl() + settings.getDatabaseName());
        dataSource.setUser(settings.getDatabaseUsername());
        dataSource.setPassword(settings.getDatabasePassword());

        connectionPool = new LinkedList<>();
        MAX_POOL_SIZE = maxPoolSize;

        for (int i = 0; i < initialPoolSize; i++) {
            connectionPool.add(new PooledConnection(createNewConnection(), this));
        }
    }

    Connection createNewConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public synchronized Connection getConnection() {
        if (connectionPool.isEmpty()) {
            if (currentConnections < MAX_POOL_SIZE) {
                try {
                    currentConnections++;
                    return new PooledConnection(createNewConnection(), this);
                } catch (SQLException e) {
                    throw new RuntimeException("Unable to create new database connection.", e);
                }
            } else {
                LOG.warn("All connections are in use. Please try again later.");
                return null;
            }
        } else {
            return connectionPool.poll();
        }
    }

    public synchronized void releaseConnection(PooledConnection connection) {
        if (connectionPool.size() < MAX_POOL_SIZE) {
            connectionPool.offer(connection);
        } else {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Unable to close the database connection.", e);
            }
        }
    }

    public synchronized void closePool() throws SQLException {
        for (PooledConnection connection : connectionPool) {
            connection.connection.close();
        }
        connectionPool.clear();
    }

    public int size() {
        return connectionPool.size();
    }
}
