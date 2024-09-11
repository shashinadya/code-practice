package exception;

public class NullPropertyNameOrValueException extends BadRequestException {

    public NullPropertyNameOrValueException(String message) {
        super(message);
    }
}
