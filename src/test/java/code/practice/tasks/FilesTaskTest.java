package code.practice.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilesTaskTest {
    private final String VALID_DATA_FILE_URI = "./src/test/resources/FilesTaskTest_1/ValidDataFile.txt";
    private final String EMPTY_FILE_URI = "./src/test/resources/FilesTaskTest_1/EmptyFile.txt";
    private final String ONLY_LETTERS_FILE_URI = "./src/test/resources/FilesTaskTest_1/OnlyLettersFile.txt";
    private final String VALID_DATA_FILE_PROTECTED_URI =
            "./src/test/resources/FilesTaskTest_1/ValidDataFileProtected.txt";
    private final String FILE_NOT_FOUND_URI = "./src/test/resources/" +
            "FilesTaskTest_1/FileIsNotFound.txt";
    private final String INVALID_PATH_URI = "./src/test/resources/FilesTaskTest_1?/ValidDataFile.txt";
    private final String INVALID_DIRECTORY_URI = "./src/test/resources/FilesTaskTest/ValidDataFile.txt";

    private final FilesTask filesTask = new FilesTask();

    @BeforeEach
    void setUp() throws IOException {
        Files.writeString(Path.of(VALID_DATA_FILE_URI), "1,2,3,4,5,6,7,8,9");
    }

    @Test
    void deleteEvenNumbersFromFileWithValidDataAndAvailableForRewriteTest() throws IOException {
        filesTask.deleteEvenNumbersFromFile(VALID_DATA_FILE_URI);

        assertEquals("1,3,5,7,9", Files.readString(Path.of(VALID_DATA_FILE_URI)));
    }

    @Test
    void deleteEvenNumbersFromEmptyFileTest() throws IOException {
        IOException exception = assertThrows(IOException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(EMPTY_FILE_URI);
        });

        assertEquals("File is empty and has no data.", exception.getMessage());
    }

    @Test
    void deleteEvenNumbersFileContainsOnlyLettersTest() throws IOException {
        IOException exception = assertThrows(IOException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(ONLY_LETTERS_FILE_URI);
        });

        assertEquals(ONLY_LETTERS_FILE_URI + " doesn't contain any number.", exception.getMessage());
    }

    @Test
    void deleteEvenNumbersFileIsProtectedTest() throws IOException {
        IOException exception = assertThrows(IOException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(VALID_DATA_FILE_PROTECTED_URI);
        });

        assertEquals(VALID_DATA_FILE_PROTECTED_URI + " is not writable.", exception.getMessage());
    }

    @Test
    void deleteEvenNumbersFileIsNotFoundTest() throws IOException {
        IOException exception = assertThrows(IOException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(FILE_NOT_FOUND_URI);
        });

        assertEquals(FILE_NOT_FOUND_URI + " contains invalid file or directory name.",
                exception.getMessage());
    }

    @Test
    void deleteEvenNumbersInvalidPathTest() throws IOException {
        IOException exception = assertThrows(IOException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(INVALID_PATH_URI);
        });

        assertEquals(INVALID_PATH_URI + " contains invalid characters.",
                exception.getMessage());
    }

    @Test
    void deleteEvenNumbersInvalidDirectoryName() throws IOException {
        IOException exception = assertThrows(IOException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(INVALID_DIRECTORY_URI);
        });

        assertEquals(INVALID_DIRECTORY_URI + " contains invalid file or directory name.",
                exception.getMessage());
    }
}
