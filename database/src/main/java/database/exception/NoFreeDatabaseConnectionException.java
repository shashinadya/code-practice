package database.exception;

/**
 * The {@code NoFreeDatabaseConnectionException} class is a custom runtime exception
 * that indicates that no available database connections are currently free for
 * use.
 *
 * <p>This exception is typically thrown when an application attempts to acquire
 * a database connection from a connection pool, but all connections are in
 * use or unavailable. It signals that the limit on the number of concurrent
 * connections has been reached, and the application must either wait for
 * a connection to become available or handle the situation accordingly.
 *
 * <p>This exception helps to ensure robust handling of database connection
 * management, allowing developers to implement logic to retry acquiring
 * connections or to provide informative feedback to users or systems
 * about the unavailability of database resources.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class NoFreeDatabaseConnectionException extends RuntimeException {

    public NoFreeDatabaseConnectionException(String message) {
        super(message);
    }
}
