package database.exception.exception;

public class IncorrectPropertyNameException extends BadRequestException {

    public IncorrectPropertyNameException(String message) {
        super(message);
    }
}
