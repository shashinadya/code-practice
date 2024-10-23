package database.exception;

/**
 * The {@code DeletionDatabaseException} class is a custom runtime exception
 * that signals an error encountered during a database deletion operation.
 *
 * <p>This exception is intended to provide a clear indication of issues that
 * arise when attempting to delete data from a database. It serves as a specific
 * error handler for deletion-related failures, allowing developers to
 * differentiate between various types of database operations and handle errors
 * appropriately.
 *
 * <p>By using this exception, developers can implement targeted error
 * handling logic to manage situations where deletion requests cannot be
 * completed, enhancing the overall robustness and reliability of the
 * application.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class DeletionDatabaseException extends RuntimeException {

    public DeletionDatabaseException(String message) {
        super(message);
    }
}
