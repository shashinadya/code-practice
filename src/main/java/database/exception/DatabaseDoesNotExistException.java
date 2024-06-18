package database.exception;

public class DatabaseDoesNotExistException extends RuntimeException {

    public DatabaseDoesNotExistException(String message) {
        super(message);
    }
}
