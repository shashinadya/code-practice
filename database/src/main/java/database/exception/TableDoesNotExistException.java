package database.exception;

public class TableDoesNotExistException extends BadRequestException {

    public TableDoesNotExistException(String message) {
        super(message);
    }
}
