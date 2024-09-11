package practice.exceptions;

public class InvalidFileDataFormatException extends RuntimeException {
    public InvalidFileDataFormatException(String message) {
        super(message);
    }
}
