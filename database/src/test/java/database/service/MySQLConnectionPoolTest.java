package database.service;

import database.helper.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class MySQLConnectionPoolTest {
    private final Settings settings = new Settings("Db_app_properties_files/application.properties");
    private MySQLConnectionPool connectionPool;

    @BeforeEach
    void setUp() throws SQLException {
        connectionPool = new MySQLConnectionPool(settings, 5, 10);
    }

    @Test
    void testGetConnection() {
        Connection connection = connectionPool.getConnection();
        assertNotNull(connection);
    }

    @Test
    void testReleaseConnection() {
        Connection connection = connectionPool.getConnection();
        connectionPool.releaseConnection(connection);

        assertEquals(5, connectionPool.size());
    }

    @Test
    void testGetConnectionHandlesSQLException() {
        MySQLConnectionPool mockPool = Mockito.mock(MySQLConnectionPool.class);

        when(mockPool.getConnection()).thenThrow(new RuntimeException("Unable to create new database connection."));

        Exception exception = assertThrows(RuntimeException.class, mockPool::getConnection);

        assertEquals("Unable to create new database connection.", exception.getMessage());
    }

    @Test
    void testPoolMaxSize() {
        for (int i = 0; i < 10; i++) {
            connectionPool.getConnection();
        }

        Connection connection = connectionPool.getConnection();
        assertNotNull(connection);
    }
}