package database.exception;

public class IdProvidedManuallyException extends BadRequestException {

    public IdProvidedManuallyException(String message) {
        super(message);
    }
}
