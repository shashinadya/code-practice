package database.exception;

/**
 * The {@code IdMismatchException} class is a custom runtime exception
 * that indicates an inconsistency between expected and actual entity identifiers (IDs).
 *
 * <p>This exception is typically thrown when an operation, such as an update
 * or deletion, is attempted on an entity with an ID that does not match
 * the ID provided in the request. This helps prevent unintended modifications
 * to the wrong entity, ensuring that operations are performed only on the
 * intended target.
 *
 * <p>By throwing this exception, developers can signal to users or clients
 * that the requested operation could not be completed due to an ID mismatch,
 * thereby enhancing the integrity and reliability of the application's data
 * handling processes.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class IdMismatchException extends BadRequestException {

    public IdMismatchException(String message) {
        super(message);
    }
}
