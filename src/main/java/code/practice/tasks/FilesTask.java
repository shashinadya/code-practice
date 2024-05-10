package code.practice.tasks;

import code.practice.exceptions.InvalidFileDataFormatException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilesTask {

    static final String EMPTY_FILE_MSG = "File is empty and has no data.";
    static final String INVALID_FILE_DIR_MSG = " contains invalid file or directory name.";
    static final String PROTECTED_FILE_MSG = " is not writable.";
    static final String NO_NUMBER_MSG = " doesn't contain any number.";
    static final String INVALID_PATH_MSG = " contains invalid characters.";

    //Given a text file containing integers. Remove all even numbers from it.
    public void deleteEvenNumbersFromFile(String uriOfFileWithIntegers) throws IOException {

        if (uriOfFileWithIntegers.matches(".*[@#$%^&!?].*")) {
            throw new IOException(uriOfFileWithIntegers + INVALID_PATH_MSG);
        }

        Path path = Path.of(uriOfFileWithIntegers);

        if (!path.toFile().exists() || !Files.exists(path)) {
            throw new IOException(uriOfFileWithIntegers + INVALID_FILE_DIR_MSG);
        }

        if (!path.toFile().canWrite()) {
            throw new IOException(uriOfFileWithIntegers + PROTECTED_FILE_MSG);
        }

        String result = Files.readString(path);

        if (result.isEmpty()) {
            throw new InvalidFileDataFormatException(EMPTY_FILE_MSG);
        }

        if (!result.matches(".*\\d.*")) {
            throw new InvalidFileDataFormatException(uriOfFileWithIntegers + NO_NUMBER_MSG);
        }

        List<String> numbers = Arrays.asList(result.split(","));

        String oddNumbers = numbers.stream()
                .map(Integer::parseInt)
                .filter(s -> s % 2 != 0)
                .map(Object::toString)
                .collect(Collectors.joining(","));

        Files.writeString(path, oddNumbers);
    }
}
