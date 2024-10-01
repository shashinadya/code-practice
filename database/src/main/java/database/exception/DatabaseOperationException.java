package database.exception;

public class DatabaseOperationException extends BadRequestException {

    public DatabaseOperationException(String message) {
        super(message);
    }
}
