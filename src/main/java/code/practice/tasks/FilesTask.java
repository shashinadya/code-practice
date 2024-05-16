package code.practice.tasks;

import code.practice.exceptions.InvalidFileDataFormatException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;


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
                .collect(joining(","));

        Files.writeString(path, oddNumbers);
    }

    //In this text file, delete all words that contain at least one number.
    public void deleteAllWordsThatContainAtLeastOneNumber(String uriOfFileWithIntegers) throws IOException {
        Path path = Path.of(uriOfFileWithIntegers);
        String result = Files.readString(path);
        List<String> words = Arrays.asList(result.split(","));

        String wordsWithoutNumbers = words.stream()
                .filter(w -> !w.matches(".*\\d.*"))
                .collect(joining(","));

        Files.writeString(path, wordsWithoutNumbers);
    }

    //Given a text file. Create a new file, each line of which is obtained from the corresponding
    // line of the source file by rearranging the words in reverse order.
    public File createNewFileWithReversedWords(String uriOfFileWithIntegers, boolean rewriteIfExist)
            throws IOException, URISyntaxException {
        Path path = Path.of(uriOfFileWithIntegers);
        List<String> reversedLines = Files.lines(path)
                .map(line -> {
                    String[] words = line.split(" ");
                    List<String> reversedWords = Collections.singletonList(stream(words)
                            .map(StringBuilder::new).reduce((sb1, sb2) -> sb2.append(" ").append(sb1))
                            .orElse(new StringBuilder())
                            .toString());
                    return String.join(" ", reversedWords);
                })
                .collect(Collectors.toList());


        URI resourceFolder = getClass().getResource(File.separator).toURI();
        String pathToResourceFolder = Paths.get(resourceFolder).toString();
        File reversedWords = new File(Paths.get(pathToResourceFolder + File.separator + "ReversedWords.txt")
                .toUri());

        if (!reversedWords.createNewFile() && !rewriteIfExist) {
            throw new IOException(formatMessage(reversedWords.toPath().toString(),
                    "ReversedWords.txt file already exists"));
        }
        Files.write(reversedWords.toPath(), reversedLines);
        return reversedWords;
    }

    static String formatMessage(String filePath, String exceptionMessage) {
        return exceptionMessage + ": " + filePath;
    }
}
