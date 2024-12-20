package database.dao.mysql;

import database.exception.NoFreeDatabaseConnectionException;
import database.helper.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;

import static database.dao.mysql.MySQLConnectionPool.NO_FREE_DATABASE_CONNECTION;
import static database.dao.mysql.MySQLConnectionPool.UNABLE_CREATE_CONNECTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * The {@code MySQLConnectionPoolTest} class contains unit tests for the {@code MySQLConnectionPool} class.
 *
 * <p>This class tests various aspects of the {@code MySQLConnectionPool}, which manages a pool of MySQL database
 * connections. The tests cover scenarios such as acquiring and releasing connections, handling exceptions, managing
 * pool size limits, and closing the connection pool.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 * @see MySQLConnectionPool
 */
class MySQLConnectionPoolTest {
    private final Settings settings = new Settings("Db_app_properties_files/application.properties");
    private MySQLConnectionPool connectionPool;

    @BeforeEach
    void setUp() {
        connectionPool = new MySQLConnectionPool(settings);
    }

    @Test
    void getConnectionTest() {
        Connection connection = connectionPool.getConnection();
        assertNotNull(connection);
    }

    @Test
    void releaseConnectionTest() {
        Connection connection = connectionPool.getConnection();
        connectionPool.releaseConnection(connection);

        assertEquals(4, connectionPool.size());
    }

    @Test
    void getConnectionHandlesSQLExceptionTest() {
        MySQLConnectionPool mockPool = Mockito.mock(MySQLConnectionPool.class);

        when(mockPool.getConnection()).thenThrow(new RuntimeException(UNABLE_CREATE_CONNECTION));

        Exception exception = assertThrows(RuntimeException.class, mockPool::getConnection);

        assertEquals(UNABLE_CREATE_CONNECTION, exception.getMessage());
    }

    @Test
    void maxPoolSizeTest() {
        for (int i = 0; i < 14; i++) {
            connectionPool.getConnection();
        }

        var exception = assertThrows(NoFreeDatabaseConnectionException.class, () -> connectionPool.getConnection());

        assertEquals(NO_FREE_DATABASE_CONNECTION, exception.getMessage());
    }

    @Test
    public void closePoolTest() {
        connectionPool.closePool();

        assertEquals(0, connectionPool.size());
    }
}
