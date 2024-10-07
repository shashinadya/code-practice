package database.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PooledConnectionTest {

    private PooledConnection pooledConnection;
    private Connection realConnection;
    private MySQLConnectionPool pool;

    @BeforeEach
    void setUp() throws SQLException {
        realConnection = Mockito.mock(Connection.class);
        when(realConnection.createStatement()).thenReturn(mock(Statement.class));
        pool = Mockito.mock(MySQLConnectionPool.class);
        pooledConnection = new PooledConnection(realConnection, pool);
    }

    @Test
    void testCloseReturnsConnectionToPool() throws SQLException {
        pooledConnection.close();

        verify(pool).releaseConnection(pooledConnection);
    }

    @Test
    void testCreateStatement() throws SQLException {
        Statement statement = pooledConnection.createStatement();

        assertNotNull(statement);
        verify(realConnection).createStatement();
    }

    @Test
    void testCommit() throws SQLException {
        pooledConnection.commit();

        verify(realConnection).commit();
    }
}
