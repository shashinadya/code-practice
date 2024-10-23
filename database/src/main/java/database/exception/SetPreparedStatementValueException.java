package database.exception;

/**
 * The {@code SetPreparedStatementValueException} class is a custom runtime
 * exception that indicates an error occurred while setting a value in a
 * {@link java.sql.PreparedStatement}. This exception is thrown when there is
 * an issue with assigning a parameter value to a prepared statement, which
 * can prevent SQL queries from executing correctly.
 *
 * <p>This exception may arise due to various reasons, including but not
 * limited to type mismatches, null values where they are not allowed, or
 * other issues related to the prepared statement's configuration. It serves
 * as a clear indicator that something went wrong during the process of
 * binding values to the SQL statement.
 *
 * <p>Using this exception helps developers pinpoint errors related to
 * setting parameters in prepared statements, enabling more effective error
 * handling and debugging in applications that interact with databases.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class SetPreparedStatementValueException extends RuntimeException {

    public SetPreparedStatementValueException(String message) {
        super(message);
    }
}
