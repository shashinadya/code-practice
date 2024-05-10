package code.practice.exceptions;

import java.io.IOException;

public class InvalidFileDataFormatException extends IOException {
    public InvalidFileDataFormatException(String message) {
        super(message);
    }
}
