package database.exception;

public class NullOrEmptyListException extends BadRequestException {

    public NullOrEmptyListException(String message) {
        super(message);
    }
}
