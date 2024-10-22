package database.exception;

/**
 * The {@code InvalidParameterValueException} class is a custom runtime exception
 * that signals that a parameter value provided in a request or operation is
 * not valid or does not meet the required criteria.
 *
 * <p>This exception is typically thrown when the value of a parameter
 * does not conform to expected formats, types, or ranges defined by the
 * application logic. It serves to enforce input validation and ensure
 * that operations are performed with valid data.
 *
 * <p>By throwing this exception, the application can provide clear feedback
 * to the user or client about the nature of the error, enabling them to
 * correct the parameter value before retrying the operation.
 */
public class InvalidParameterValueException extends BadRequestException {

    public InvalidParameterValueException(String message) {
        super(message);
    }
}
