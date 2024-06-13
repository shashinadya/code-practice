package code.practice.exceptions.database;

public class NullPropertyNameOrValueException extends RuntimeException {

    public NullPropertyNameOrValueException(String message) {
        super(message);
    }
}
