package code.practice.tasks;

import code.practice.exceptions.UnexpectedMajorException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamTasksTest {
    StreamTasks streamTasks = new StreamTasks();

    @Test
    void sumOfPositiveIntegersTest() {
        var numbers = List.of("1", "-4", "5", "-1", "6");
        assertEquals(12, streamTasks.sumOfPositiveIntegers(numbers));
    }

    @Test
    void sumOfIntegersTest() {
        var numbersLists = List.of(List.of("1", "-4", "5", "-1", "6"));
        assertEquals(12, streamTasks.sumOfIntegers(numbersLists));
    }

    @Test
    void justPrintSequenceTest() {
        streamTasks.justPrintSequence(2, 9);
    }

    @Test
    void getAnyNegativeIntAsStringTest() throws UnexpectedMajorException {
        var listOfInts = List.of(1, -4, 5, 1, 6);
        assertEquals("-4", streamTasks.getAnyNegativeIntAsString(listOfInts));
    }

    @Test
    void getFirstNegativeIntAsStringIfItIsPresentedTest() {
        var listOfInts = List.of(1, -1, 5, -5, 6);
        assertEquals("-1", streamTasks.getFirstNegativeIntAsString(listOfInts));
    }

    @Test
    void getFirstNegativeIntAsStringIfItIsNotPresentedTest() {
        var listOfInts = List.of(6, 2, 1, 5, 6);
        assertEquals(String.valueOf(Integer.MIN_VALUE), streamTasks.getFirstNegativeIntAsString(listOfInts));
    }
}
