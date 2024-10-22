package database.exception;

/**
 * The {@code DeserializeDatabaseException} class is a custom runtime exception
 * that indicates an error encountered during the deserialization of database
 * entities.
 *
 * <p>This exception is typically thrown when there is an issue converting
 * serialized data back into its original object form, which can occur due to
 * various reasons such as data corruption, format mismatches, or unexpected
 * data types.
 *
 * <p>By using this exception, developers can effectively manage and respond
 * to deserialization failures, providing better error handling and user
 * feedback in scenarios where data integrity is compromised.
 */
public class DeserializeDatabaseException extends RuntimeException {

    public DeserializeDatabaseException(String message) {
        super(message);
    }
}
