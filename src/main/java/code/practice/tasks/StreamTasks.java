package code.practice.tasks;

import code.practice.exceptions.UnexpectedMajorException;

import java.util.List;
import java.util.stream.IntStream;

public class StreamTasks {
    //Given list of strings representing int numbers. Return sum of all positive elements. write each element to console.
    public int sumOfPositiveIntegers(List<String> numbers) {
        return numbers.stream()
                .mapToInt(Integer::parseInt)
                .filter(e -> e >= 0)
                .peek(System.out::println)
                .sum();
    }

    //Given list of lists of strings representing int numbers. Return sum of all positive elements.
    public int sumOfIntegers(List<List<String>> numbersLists) {
        return numbersLists.stream()
                .flatMap(List::stream)
                .mapToInt(Integer::parseInt)
                .filter(e -> e >= 0)
                .sum();
    }

    //Print sequence between from and to
    public void justPrintSequence(int from, int to) {
        IntStream.rangeClosed(from, to)
                .forEach(System.out::print);
    }

    //Return any negative int string from the given list of ints. Throw some exception if not found.
    public String getAnyNegativeIntAsString(List<Integer> listOfInts) throws UnexpectedMajorException {
        if (listOfInts.stream().noneMatch(e -> e < 0)) {
            throw new UnexpectedMajorException("No negative integers found");
        } else {
            return listOfInts.stream()
                    .filter(e -> e < 0)
                    .map(Object::toString)
                    .findAny()
                    .orElse("");
        }
    }

    //Return first negative int string from the given list of ints. Return minimum possible int value if not found in the given list.
    public String getFirstNegativeIntAsString(List<Integer> listOfInts) {
        return listOfInts.stream()
                .filter(e -> e < 0)
                .map(Object::toString)
                .findFirst()
                .orElse(String.valueOf(Integer.MIN_VALUE));
    }
}
