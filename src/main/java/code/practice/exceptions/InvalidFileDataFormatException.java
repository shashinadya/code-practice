package code.practice.exceptions;

import java.io.IOException;

public class InvalidFileDataFormatException extends RuntimeException {
    public InvalidFileDataFormatException(String message) {
        super(message);
    }
}
