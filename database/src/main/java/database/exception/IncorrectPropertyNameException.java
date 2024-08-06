package database.exception;

public class IncorrectPropertyNameException extends BadRequestException {

    public IncorrectPropertyNameException(String message) {
        super(message);
    }
}
