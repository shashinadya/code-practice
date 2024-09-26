package database.exception;

public class FailedAccessFieldValueException extends BadRequestException {

    public FailedAccessFieldValueException(String message) {
        super(message);
    }
}
