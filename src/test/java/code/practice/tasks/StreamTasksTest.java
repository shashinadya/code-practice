package code.practice.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StreamTasksTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    StreamTasks streamTasks = new StreamTasks();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void sumOfPositiveIntegersTest() {
        var numbers = List.of("1", "-4", "5", "-1", "6");
        assertEquals(12, streamTasks.sumOfPositiveIntegers(numbers));
    }

    @Test
    void sumOfIntegersTest() {
        var numbersLists = List.of(List.of("1", "-4", "5", "-1", "6"), List.of("-1", "4", "-5", "1", "-6"));
        assertEquals(12, streamTasks.sumOfIntegers(numbersLists));
    }

    @Test
    void justPrintSequenceTest() {
        streamTasks.justPrintSequence(2, 9);
        assertEquals("23456789", outputStreamCaptor.toString()
                .trim());
    }

    @Test
    void getAnyNegativeIntAsStringTest() {
        var listOfInts = List.of(1, -4, 5, 1, 6);
        assertEquals("-4", streamTasks.getAnyNegativeIntAsString(listOfInts));
    }

    @Test
    void getAnyNegativeIntAsStringIfNoNegativeIntegersFoundTest() {
        var listOfInts = List.of(1, 4, 5, 1, 6);

        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> {
            streamTasks.getAnyNegativeIntAsString(listOfInts);
        });

        assertEquals("No value present", e.getMessage());
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
