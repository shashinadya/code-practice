package code.practice.exceptions.database;

public class IdDoesNotExistException extends RuntimeException {

    public IdDoesNotExistException(String message) {
        super(message);
    }
}
