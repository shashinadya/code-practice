package code.practice.tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilesTask {

    //Given a text file containing integers. Remove all even numbers from it.
    public void deleteEvenNumbersFromFile(String uriOfFileWithIntegers) throws IOException {

        if (uriOfFileWithIntegers.matches(".*[:*?\"<>|].*")) {
            throw new IOException(uriOfFileWithIntegers + " contains invalid characters.");
        }

        Path path = Path.of(uriOfFileWithIntegers);

        if (!path.toFile().exists() || !Files.exists(Paths.get(uriOfFileWithIntegers))) {
            throw new IOException(uriOfFileWithIntegers + " contains invalid file or directory name.");
        }

        if (!path.toFile().canWrite()) {
            throw new IOException(uriOfFileWithIntegers + " is not writable.");
        }

        String result = Files.readString(path);

        if (result.isEmpty()) {
            throw new IOException("File is empty and has no data.");
        }

        if (!result.matches(".*\\d.*")) {
            throw new IOException(uriOfFileWithIntegers + " doesn't contain any number.");
        }

        List<String> numbers = Arrays.asList(result.split(","));

        String oddNumbers = numbers.stream()
                .mapToInt(Integer::parseInt)
                .filter(s -> s % 2 != 0)
                .boxed()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        Files.writeString(path, oddNumbers);
    }
}
