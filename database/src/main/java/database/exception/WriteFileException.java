package database.exception;

/**
 * The {@code WriteFileException} class is a custom runtime
 * exception that indicates an error occurred while attempting
 * to write data to a file. This exception is thrown when
 * the application encounters issues during the file writing
 * process, which can impede data persistence and integrity.
 *
 * <p>Common causes for this exception may include
 * insufficient permissions, lack of available storage space,
 * invalid file paths, or issues related to file system
 * integrity. By using this exception, developers can more
 * effectively handle file writing errors and implement
 * appropriate recovery strategies or user notifications.
 *
 * <p>This exception serves as a clear indicator of file
 * writing failures, enabling better error handling in
 * applications that perform file I/O operations.
 */
public class WriteFileException extends RuntimeException {

    public WriteFileException(String message) {
        super(message);
    }
}
