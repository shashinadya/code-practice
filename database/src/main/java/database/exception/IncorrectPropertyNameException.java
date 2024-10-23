package database.exception;

/**
 * The {@code IncorrectPropertyNameException} class is a custom runtime exception
 * that indicates that a property name provided in a request or operation is
 * not recognized or does not match any valid property names defined in the
 * application context.
 *
 * <p>This exception is typically thrown when filtering or querying data,
 * where a client attempts to use a property name that is not defined in the
 * corresponding entity or object model. This helps to enforce strict validation
 * of property names to ensure that only valid fields can be used for filtering
 * or other operations.
 *
 * <p>By throwing this exception, the application can communicate to the user
 * or client that the operation has failed due to an incorrect or invalid
 * property name, thus prompting them to correct their input before retrying
 * the operation.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class IncorrectPropertyNameException extends BadRequestException {

    public IncorrectPropertyNameException(String message) {
        super(message);
    }
}
