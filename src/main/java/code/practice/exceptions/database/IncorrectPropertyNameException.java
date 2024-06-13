package code.practice.exceptions.database;

public class IncorrectPropertyNameException extends RuntimeException {

    public IncorrectPropertyNameException(String message) {
        super(message);
    }
}
