package code.practice.tasks;

import code.practice.exceptions.InvalidFileDataFormatException;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static code.practice.tasks.FilesTask.EMPTY_FILE_MSG;
import static code.practice.tasks.FilesTask.INVALID_FILE_DIR_MSG;
import static code.practice.tasks.FilesTask.INVALID_PATH_MSG;
import static code.practice.tasks.FilesTask.NO_NUMBER_MSG;
import static code.practice.tasks.FilesTask.PROTECTED_FILE_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilesTaskTest {
    private final FilesTask filesTask = new FilesTask();

    private String getFilePath(String fileName) throws URISyntaxException, FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        URI filePathURI;
        try {
            filePathURI = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();
        } catch (NullPointerException e) {
            throw new FileNotFoundException("File is not found: " + fileName);
        }
        return Paths.get(filePathURI).toString();
    }

    private void revert(String filePath, String content) throws IOException {
        Files.writeString(Path.of(filePath), content);
    }

    @Test
    void deleteEvenNumbersFromFileWithValidDataAndAvailableForRewriteTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/ValidDataFile.txt");
        filesTask.deleteEvenNumbersFromFile(filePath);

        assertEquals("1,3,5,7,9", Files.readString(Path.of(filePath)));
        revert(filePath, "1,2,3,4,5,6,7,8,9");
    }

    @Test
    void deleteEvenNumbersFromEmptyFileTest() throws IOException {
        InvalidFileDataFormatException exception = assertThrows(InvalidFileDataFormatException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(getFilePath("FilesTaskTest_1/EmptyFile.txt"));
        });

        assertEquals(EMPTY_FILE_MSG, exception.getMessage());
    }

    @Test
    void deleteEvenNumbersFileContainsOnlyLettersTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/OnlyLettersFile.txt");
        InvalidFileDataFormatException exception = assertThrows(InvalidFileDataFormatException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(filePath);
        });

        assertEquals(filePath + NO_NUMBER_MSG, exception.getMessage());
    }

    @Test
    void deleteEvenNumbersFileIsProtectedTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/ValidDataFileProtected.txt");
        Path.of(filePath).toFile().setReadOnly();
        IOException exception = assertThrows(IOException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(filePath);
        });

        assertEquals(filePath + PROTECTED_FILE_MSG, exception.getMessage());
    }

    @Test
    void deleteEvenNumbersFileIsNotFoundTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/ValidDataFile.txt");
        var nonExistFilePath = filePath.replace("ValidDataFile.txt", "ValidDataFile1.txt");
        IOException exception = assertThrows(IOException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(nonExistFilePath);
        });

        assertEquals(nonExistFilePath + INVALID_FILE_DIR_MSG,
                exception.getMessage());
    }

    @Test
    void deleteEvenNumbersInvalidPathTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/ValidDataFile.txt");
        var invalidFilePath = filePath.replace("FilesTaskTest_1", "Fi&lesTaskTest_1");
        IOException exception = assertThrows(IOException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(invalidFilePath);
        });

        assertEquals(invalidFilePath + INVALID_PATH_MSG,
                exception.getMessage());
    }

    @Test
    void deleteEvenNumbersInvalidDirectoryName() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/ValidDataFile.txt");
        var invalidDirNameInPath = filePath.replace("FilesTaskTest_1", "FilesTaskTest_11");
        IOException exception = assertThrows(IOException.class, () -> {
            filesTask.deleteEvenNumbersFromFile(invalidDirNameInPath);
        });

        assertEquals(invalidDirNameInPath + INVALID_FILE_DIR_MSG,
                exception.getMessage());
    }

    @Test
    void deleteAllWordsThatContainAtLeastOneNumberTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/WordsWithNumbers.txt");
        filesTask.deleteAllWordsThatContainAtLeastOneNumber(filePath);

        assertEquals("word,word,number", Files.readString(Path.of(filePath)));
        revert(filePath, "word,word1,word2,word,word3,number");
    }
}
