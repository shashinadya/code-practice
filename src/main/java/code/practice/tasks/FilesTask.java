package code.practice.tasks;

import code.practice.exceptions.InvalidFileDataFormatException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilesTask {
    static final String EMPTY_FILE_MSG = "Given file is empty and has no data";
    static final String INVALID_FILE_DIR_MSG = "Given path contains invalid file or directory name";
    static final String PROTECTED_FILE_MSG = "Given file is not writable";
    static final String NO_NUMBER_MSG = "Given file doesn't contain any number";
    static final String INVALID_PATH_MSG = "Given path contains invalid characters";

    //Given a text file containing integers. Remove all even numbers from it.
    public void deleteEvenNumbersFromFile(String uriOfFileWithIntegers) throws IOException {
        if (uriOfFileWithIntegers.matches(".*[@#$%^&!?].*")) {
            throw new IOException(formatMessage(uriOfFileWithIntegers, INVALID_PATH_MSG));
        }

        Path path = Path.of(uriOfFileWithIntegers);

        if (!path.toFile().exists() || !Files.exists(path)) {
            throw new IOException(formatMessage(uriOfFileWithIntegers, INVALID_FILE_DIR_MSG));
        }

        if (!path.toFile().canWrite()) {
            throw new IOException(formatMessage(uriOfFileWithIntegers, PROTECTED_FILE_MSG));
        }

        String result = Files.readString(path);

        if (result.isEmpty()) {
            throw new InvalidFileDataFormatException(formatMessage(uriOfFileWithIntegers, EMPTY_FILE_MSG));
        }

        if (!result.matches(".*\\d.*")) {
            throw new InvalidFileDataFormatException(formatMessage(uriOfFileWithIntegers, NO_NUMBER_MSG));
        }

        List<String> numbers = Arrays.asList(result.split(","));

        String oddNumbers = numbers.stream()
                .map(Integer::parseInt)
                .filter(s -> s % 2 != 0)
                .map(Object::toString)
                .collect(Collectors.joining(","));

        Files.writeString(path, oddNumbers);
    }

    //In this text file, delete all words that contain at least one number.
    public void deleteAllWordsThatContainAtLeastOneNumber(String uriOfFileWithIntegers) throws IOException {
        Path path = Path.of(uriOfFileWithIntegers);
        String result = Files.readString(path);
        List<String> words = Arrays.asList(result.split(","));

        String wordsWithoutNumbers = words.stream()
                .filter(w -> !w.matches(".*\\d.*"))
                .collect(Collectors.joining(","));

        Files.writeString(path, wordsWithoutNumbers);
    }

    static String formatMessage(String filePath, String exceptionMessage) {
        return exceptionMessage + ": " + filePath;
    }
}
