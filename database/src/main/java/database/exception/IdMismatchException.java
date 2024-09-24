package database.exception;

public class IdMismatchException extends BadRequestException {

    public IdMismatchException(String message) {
        super(message);
    }
}
