package database.exception;

/**
 * The {@code TableDoesNotExistException} class is a custom runtime exception
 * that extends {@code BadRequestException}. It is thrown when an operation
 * attempts to access or manipulate a table that does not exist.
 *
 * <p>This exception is typically used to indicate that a requested table
 * could not be found, which may occur during operations such as connection
 * attempts, data retrieval, or updates. By providing a specific exception
 * type, developers can handle such cases distinctly, improving error handling
 * and clarity within the application.
 *
 * <p>Since this class extends {@code BadRequestException}, it indicates
 * that the client's request is invalid due to the non-existence of the
 * specified table.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class TableDoesNotExistException extends BadRequestException {

    public TableDoesNotExistException(String message) {
        super(message);
    }
}
