package code.practice.exceptions.database;

public class IncorrectValueTypeException extends RuntimeException {

    public IncorrectValueTypeException(String message) {
        super(message);
    }
}
