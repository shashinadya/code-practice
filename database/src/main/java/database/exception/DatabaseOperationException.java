package database.exception;

/**
 * The {@code DatabaseOperationException} class is a custom runtime exception
 * that extends {@code BadRequestException}. It is thrown to indicate
 * an error that occurs during database operations, such as
 * inserting, updating, deleting, or querying data.
 *
 * <p>This exception serves as a general-purpose error handler for
 * various database-related issues that do not fall under more specific
 * exception categories. It helps to encapsulate and communicate problems
 * that may arise during database interactions, providing a clear
 * indication that the operation requested by the client could not be completed
 * due to an underlying issue.
 *
 * <p>By using this exception, developers can implement error handling
 * mechanisms that specifically address issues related to database
 * operations, thereby enhancing the robustness of the application.
 */
public class DatabaseOperationException extends BadRequestException {

    public DatabaseOperationException(String message) {
        super(message);
    }
}
