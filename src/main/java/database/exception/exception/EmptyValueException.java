package database.exception.exception;

public class EmptyValueException extends BadRequestException {

    public EmptyValueException(String message) {
        super(message);
    }
}
