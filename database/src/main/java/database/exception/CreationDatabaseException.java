package database.exception;

/**
 * The {@code CreationDatabaseException} class represents a custom runtime exception
 * that is thrown when there is an error related to the creation of a database or
 * its components.
 *
 * <p>This exception is typically used to indicate issues such as failure to create
 * a database directory, problems with database initialization, or other related
 * errors that occur during the setup process. It extends {@code RuntimeException},
 * allowing it to be thrown without being explicitly declared in the method signature.
 *
 * <p>By using this specific exception, developers can more easily identify and
 * handle errors associated with database creation operations, improving the
 * clarity and maintainability of the code.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class CreationDatabaseException extends RuntimeException {

    public CreationDatabaseException(String message) {
        super(message);
    }
}
