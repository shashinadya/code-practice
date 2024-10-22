package database.exception;

/**
 * The {@code ReadFileException} class is a custom runtime exception that indicates
 * an error occurred while attempting to read a file. This exception is thrown
 * when the file cannot be accessed, does not exist, or cannot be processed due
 * to unforeseen issues.
 *
 * <p>This exception is typically used in file handling operations where reading
 * from a file is essential for the application to function correctly. It helps
 * identify and handle errors related to file reading, allowing developers to
 * provide meaningful feedback and take corrective action.
 *
 * <p>By using this exception, developers can distinguish file reading errors
 * from other types of exceptions, enabling more granular error handling in
 * applications that depend on file input.
 */
public class ReadFileException extends RuntimeException {

    public ReadFileException(String message) {
        super(message);
    }
}
