package exception;

public class IncorrectValueTypeException extends BadRequestException {

    public IncorrectValueTypeException(String message) {
        super(message);
    }
}
