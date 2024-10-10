package database.exception;

public class NoFreeDatabaseConnectionException extends RuntimeException {

    public NoFreeDatabaseConnectionException(String message) {
        super(message);
    }
}
