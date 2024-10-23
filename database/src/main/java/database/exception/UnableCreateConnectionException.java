package database.exception;

/**
 * The {@code UnableCreateConnectionException} class is a custom runtime
 * exception that indicates a failure to establish a connection to a database.
 * This exception is thrown when an application encounters issues while
 * trying to create a new database connection, which can prevent operations
 * that rely on database access.
 *
 * <p>Possible reasons for this exception include incorrect database
 * credentials, network issues, database server unavailability, or
 * configuration errors in the connection settings. It provides developers
 * with a clear indication of the inability to connect to the database,
 * enabling better error handling and debugging.
 *
 * <p>Using this exception allows for a more granular approach to managing
 * connection-related errors, facilitating the implementation of retries or
 * fallback mechanisms in database connectivity logic.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class UnableCreateConnectionException extends RuntimeException {

    public UnableCreateConnectionException(String message) {
        super(message);
    }
}
