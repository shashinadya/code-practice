package database.exception;

public class UnableCreateConnectionException extends RuntimeException {

    public UnableCreateConnectionException(String message) {
        super(message);
    }
}
