package code.practice.exceptions;

public class ProvidedIdDoesNotExistException extends Exception {
    public ProvidedIdDoesNotExistException(String message) {
        super(message);
    }
}
