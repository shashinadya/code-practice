package database.exception;

public class EntityNotFoundException extends BadRequestException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
