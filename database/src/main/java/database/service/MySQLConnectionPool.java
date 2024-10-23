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

/**
 * The {@code MySQLConnectionPool} class manages a pool of reusable database connections
 * to a MySQL database. It optimizes resource usage by reusing connections and limiting
 * the number of open connections according to the configured pool size.
 *
 * <p>The pool is initialized with a configurable number of connections and can grow
 * up to a maximum limit. If no free connections are available when a request is made,
 * the pool will attempt to create new ones, or it will throw an exception if the maximum
 * pool size is reached.
 *
 * <p>Connections that are no longer needed can be returned to the pool, or they will
 * be closed if the pool is already at maximum size. The class is also responsible for
 * cleaning up the pool by closing all connections when the pool is shut down.
 *
 * <p>Logging is performed using SLF4J, with warnings and errors logged in scenarios such
 * as when the maximum pool size is reached or connection creation/closure fails.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
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

    /**
     * Constructs a new {@code MySQLConnectionPool} with the given settings.
     * The pool is initialized with a configurable number of connections, and
     * the data source is set up based on the provided {@link Settings}.
     *
     * @param settings the configuration object containing the database credentials,
     *                 initial pool size, maximum pool size, and other parameters
     * @throws InvalidParameterValueException if the initial pool size is greater than the maximum pool size
     */
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

    /**
     * Retrieves a connection from the pool. If no connection is available, the pool will
     * attempt to create a new one, unless the maximum pool size has been reached, in which case
     * a {@link NoFreeDatabaseConnectionException} is thrown.
     *
     * @return a {@link Connection} object from the pool
     * @throws NoFreeDatabaseConnectionException if all connections are in use and the maximum pool size is reached
     */
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

    /**
     * Releases a connection back into the pool. If the pool is full, the connection is closed.
     *
     * @param connection the {@link Connection} to be released
     */
    public void releaseConnection(Connection connection) {
        if (connection != null && connectionPool.size() < maxPoolSize) {
            connectionPool.add(connection);
        } else if (connection != null) {
            closeConnection(connection);
        }
    }

    /**
     * Closes all connections in the pool and resets the current connection count.
     * This method should be called when the pool is no longer needed, for example
     * during application shutdown, to release resources.
     */
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

    /**
     * Returns the number of available connections in the pool.
     *
     * @return the number of free connections currently in the pool
     */
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
