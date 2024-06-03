package code.practice.exceptions;

public class DatabaseDoesNotExistException extends Exception {
    public DatabaseDoesNotExistException(String message) {
        super(message);
    }
}
