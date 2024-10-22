package database.exception;

/**
 * The {@code NullOrEmptyListException} class is a custom runtime exception
 * that signifies that a list is either null or empty when it is expected
 * to contain elements.
 *
 * <p>This exception is typically thrown in scenarios where a method or
 * operation requires a non-null and non-empty list as an argument. If
 * the provided list fails to meet these criteria, this exception helps
 * signal the invalid input state, allowing for appropriate error handling
 * and user feedback.
 *
 * <p>By using this exception, developers can enforce constraints on list
 * parameters, ensuring that they are validated before proceeding with
 * operations that depend on having valid data.
 */
public class NullOrEmptyListException extends BadRequestException {

    public NullOrEmptyListException(String message) {
        super(message);
    }
}
