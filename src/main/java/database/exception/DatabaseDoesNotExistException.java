package database.exception;

public class DatabaseDoesNotExistException extends BadRequestException {

    public DatabaseDoesNotExistException(String message) {
        super(message);
    }
}
