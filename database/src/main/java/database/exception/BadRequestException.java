package database.exception;

/**
 * The {@code BadRequestException} class represents a custom runtime exception
 * that is thrown when a request to the database is deemed invalid or malformed.
 *
 * <p>This exception is typically used to signal an HTTP 400 Bad Request error
 * when input data provided by the client is incorrect or cannot be processed
 * by the server. It extends {@code RuntimeException}, allowing it to be
 * thrown without being explicitly declared in the method signature.
 *
 * <p>Usage of this exception helps in distinguishing client-side errors
 * from other types of exceptions in the application, promoting better
 * error handling and clearer response messages.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
