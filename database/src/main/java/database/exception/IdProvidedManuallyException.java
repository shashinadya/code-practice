package database.exception;

/**
 * The {@code IdProvidedManuallyException} class is a custom runtime exception
 * that indicates an attempt to manually provide an identifier (ID) for an entity
 * when it is not allowed by the application's design or logic.
 *
 * <p>This exception is typically thrown in scenarios where entities are
 * expected to have their IDs auto-generated (e.g., by the database or application
 * logic), but a manual ID has been provided in the request. This helps enforce
 * consistency and integrity within the data handling processes, ensuring that
 * IDs are generated and assigned automatically rather than being manually set
 * by users or clients.
 *
 * <p>By throwing this exception, developers can clearly communicate to users
 * or clients that the operation has failed due to an improper ID provision,
 * thus preventing potential conflicts and errors in entity management.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class IdProvidedManuallyException extends BadRequestException {

    public IdProvidedManuallyException(String message) {
        super(message);
    }
}
