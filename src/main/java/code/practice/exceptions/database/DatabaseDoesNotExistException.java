package code.practice.exceptions.database;

public class DatabaseDoesNotExistException extends RuntimeException {

    public DatabaseDoesNotExistException(String message) {
        super(message);
    }
}
