package database.exception;

/**
 * The {@code IdDoesNotExistException} class is a custom runtime exception
 * that signals an error when an operation is attempted on an entity that
 * does not exist in the database, identified by a specific identifier (ID).
 *
 * <p>This exception is typically thrown when a request attempts to access,
 * modify, or delete an entity using an ID that cannot be found. It helps
 * enforce data integrity by ensuring that operations are only performed on
 * existing entities, preventing potential runtime errors and inconsistencies
 * within the application.
 *
 * <p>By throwing this exception, developers can provide clear feedback to
 * users or clients about invalid operations related to non-existent entities,
 * thus improving the robustness of the application.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class IdDoesNotExistException extends BadRequestException {

    public IdDoesNotExistException(String message) {
        super(message);
    }
}
