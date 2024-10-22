package database.exception;

/**
 * The {@code SerializeDatabaseException} class is a custom runtime exception
 * that indicates an error occurred during the serialization of a database entity.
 * This exception is thrown when the process of converting an object or data
 * structure into a format that can be stored or transmitted fails, which can
 * lead to data loss or corruption.
 *
 * <p>This exception is typically used in scenarios where database records need
 * to be serialized for storage in files, sending over a network, or any other
 * form of data transfer. It helps identify and manage issues related to the
 * serialization process, ensuring that developers can take appropriate action
 * when errors arise.
 *
 * <p>By using this exception, developers can clearly communicate issues specific
 * to serialization, allowing for more robust error handling in applications that
 * rely on database operations and data persistence.
 */
public class SerializeDatabaseException extends RuntimeException {

    public SerializeDatabaseException(String message) {
        super(message);
    }
}
