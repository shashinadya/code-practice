package database.exception.exception;

public class IdDoesNotExistException extends BadRequestException {

    public IdDoesNotExistException(String message) {
        super(message);
    }
}
