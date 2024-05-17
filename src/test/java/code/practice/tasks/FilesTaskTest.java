package code.practice.tasks;

import code.practice.exceptions.InvalidFileDataFormatException;
import org.junit.jupiter.api.Test;

import java.io.File;
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
import static code.practice.tasks.FilesTask.formatMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilesTaskTest {
    private final FilesTask filesTask = new FilesTask();

    @Test
    void deleteEvenNumbersFromFileWithValidDataAndAvailableForRewriteTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/ValidDataFile.txt");
        filesTask.deleteEvenNumbersFromFile(filePath);

        assertEquals("1,3,5,7,9", Files.readString(Path.of(filePath)));
        revert(filePath, "1,2,3,4,5,6,7,8,9");
    }

    @Test
    void deleteEvenNumbersFromEmptyFileTest() throws FileNotFoundException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/EmptyFile.txt");
        InvalidFileDataFormatException exception = assertThrows(InvalidFileDataFormatException.class, () ->
                filesTask.deleteEvenNumbersFromFile(filePath));

        assertEquals(formatMessage(filePath, EMPTY_FILE_MSG), exception.getMessage());
    }

    @Test
    void deleteEvenNumbersFileContainsOnlyLettersTest() throws URISyntaxException, FileNotFoundException {
        String filePath = getFilePath("FilesTaskTest_1/OnlyLettersFile.txt");
        InvalidFileDataFormatException exception = assertThrows(InvalidFileDataFormatException.class, () ->
                filesTask.deleteEvenNumbersFromFile(filePath));

        assertEquals(formatMessage(filePath, NO_NUMBER_MSG), exception.getMessage());
    }

    @Test
    void deleteEvenNumbersFileIsProtectedTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/ValidDataFileProtected.txt");
        boolean fileSetToReadOnly = Path.of(filePath).toFile().setReadOnly();
        assertTrue(fileSetToReadOnly);

        IOException exception = assertThrows(IOException.class, () -> filesTask.deleteEvenNumbersFromFile(filePath));

        assertEquals(formatMessage(filePath, PROTECTED_FILE_MSG), exception.getMessage());
    }

    @Test
    void deleteEvenNumbersFileIsNotFoundTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/ValidDataFile.txt");
        var nonExistFilePath = filePath.replace("ValidDataFile.txt", "ValidDataFile1.txt");
        IOException exception = assertThrows(IOException.class, () ->
                filesTask.deleteEvenNumbersFromFile(nonExistFilePath));

        assertEquals(formatMessage(nonExistFilePath, INVALID_FILE_DIR_MSG), exception.getMessage());
    }

    @Test
    void deleteEvenNumbersInvalidPathTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/ValidDataFile.txt");
        var invalidFilePath = filePath.replace("FilesTaskTest_1", "Fi&lesTaskTest_1");
        IOException exception = assertThrows(IOException.class, () ->
                filesTask.deleteEvenNumbersFromFile(invalidFilePath));

        assertEquals(formatMessage(invalidFilePath, INVALID_PATH_MSG), exception.getMessage());
    }

    @Test
    void deleteEvenNumbersInvalidDirectoryName() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/ValidDataFile.txt");
        var invalidDirNameInPath = filePath.replace("FilesTaskTest_1", "FilesTaskTest_11");
        IOException exception = assertThrows(IOException.class, () ->
                filesTask.deleteEvenNumbersFromFile(invalidDirNameInPath));

        assertEquals(formatMessage(invalidDirNameInPath, INVALID_FILE_DIR_MSG), exception.getMessage());
    }

    @Test
    void deleteAllWordsThatContainAtLeastOneNumberTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/WordsWithNumbers.txt");
        filesTask.deleteAllWordsThatContainAtLeastOneNumber(filePath);

        assertEquals("word,word,number", Files.readString(Path.of(filePath)));
        revert(filePath, "word,word1,word2,word,word3,number");
    }

    @Test
    void createNewFileWithReversedWordsPositiveTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/WordsToReverse.txt");
        String reversedWords = "words line First" + System.lineSeparator() +
                "beach Second" + System.lineSeparator() +
                "Trail Pond Creek Gold" + System.lineSeparator();
        File reversedFile = filesTask.createNewFileWithReversedWords(filePath, true);
        String result = Files.readString(reversedFile.toPath());

        assertEquals(reversedWords, result);

        reversedFile.delete();
        assertFalse(reversedFile.exists());
    }

    @Test
    void createNewFileWithReversedWordsNegativeTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/WordsToReverse.txt");
        File reversedFile = filesTask.createNewFileWithReversedWords(filePath, true);
        IOException exception = assertThrows(IOException.class, () ->
                filesTask.createNewFileWithReversedWords(filePath, false));

        assertEquals("ReversedWords.txt file already exists: " + reversedFile.getPath(),
                exception.getMessage());

        reversedFile.delete();
        assertFalse(reversedFile.exists());
    }

    @Test
    void createNewFileWithArithmeticProgressionPositiveTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/FileForArithmeticProgression.txt");
        String arithmeticProgressionLines = "3,5,7,9,11" + System.lineSeparator() +
                "2,5,8" + System.lineSeparator();
        File arithmeticProgressionFile = filesTask.createNewFileWithArithmeticProgression(filePath, true);
        String result = Files.readString(arithmeticProgressionFile.toPath());

        assertEquals(arithmeticProgressionLines, result);

        arithmeticProgressionFile.delete();
        assertFalse(arithmeticProgressionFile.exists());
    }

    @Test
    void createNewFileWithArithmeticProgressionNegativeTest() throws IOException, URISyntaxException {
        String filePath = getFilePath("FilesTaskTest_1/FileForArithmeticProgression.txt");
        File arithmeticProgressionFile = filesTask.createNewFileWithArithmeticProgression(filePath, true);
        IOException exception = assertThrows(IOException.class, () ->
                filesTask.createNewFileWithArithmeticProgression(filePath, false));

        assertEquals("ArithmeticProgressionFile.txt file already exists: " + arithmeticProgressionFile.getPath(),
                exception.getMessage());

        arithmeticProgressionFile.delete();
        assertFalse(arithmeticProgressionFile.exists());
    }

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
}
