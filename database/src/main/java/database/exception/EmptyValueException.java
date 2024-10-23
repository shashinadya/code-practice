package database.exception;

/**
 * The {@code EmptyValueException} class is a custom runtime exception that
 * indicates an error related to the presence of an empty value in a data
 * structure or a request parameter.
 *
 * <p>This exception is typically thrown when a value that is expected to be
 * non-empty (e.g., a string, list, or collection) is found to be empty,
 * which may violate application business rules or input validation
 * constraints.
 *
 * <p>By using this exception, developers can effectively handle scenarios
 * where required data is missing, ensuring that appropriate feedback is
 * provided to users or clients regarding their input.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class EmptyValueException extends BadRequestException {

    public EmptyValueException(String message) {
        super(message);
    }
}
