package database.exception;

public class InvalidParameterValueException extends BadRequestException {

    public InvalidParameterValueException(String message) {
        super(message);
    }
}
