package database.exception;

/**
 * The {@code NullPropertyNameOrValueException} class is a custom runtime exception
 * that indicates that either a property name or its corresponding value is null
 * when it is required to be non-null.
 *
 * <p>This exception is typically thrown in scenarios where properties are expected
 * to have valid names and values, such as when filtering or processing data in a
 * database. If either the property name or value is null, this exception serves
 * as a signal to indicate the invalid input state, allowing for appropriate error
 * handling and user feedback.
 *
 * <p>By using this exception, developers can enforce constraints on property names
 * and values, ensuring that they are validated before proceeding with operations
 * that rely on valid properties.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class NullPropertyNameOrValueException extends BadRequestException {

    public NullPropertyNameOrValueException(String message) {
        super(message);
    }
}
