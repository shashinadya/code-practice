package practice.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import practice.exceptions.DivisionByZeroOperationException;
import practice.exceptions.UnexpectedMajorException;
import practice.exceptions.UnsupportedParameterTypeException;
import org.junit.jupiter.api.Test;

public class CalculatorServiceTest {
    CalculatorService calculatorService = new CalculatorService();

    @Test
    void multiplyMethodParametersContainInvalidSymbolsTest() {
        UnsupportedParameterTypeException e = assertThrows(UnsupportedParameterTypeException.class, () -> {
            calculatorService.multiply("4a", "2g");
        });
        assertEquals("Caught UnsupportedParameterTypeException: For input string: \"4a\"", e.getMessage());
    }

    @Test
    void multiplyMethodParametersContainValidSymbolsTest() throws UnexpectedMajorException {
        assertEquals(8, calculatorService.multiply("4.0", "2"));
        assertEquals(0, calculatorService.multiply("8", "0"));
        assertEquals(-8, calculatorService.multiply("-4", "2.0"));
        assertEquals(-8, calculatorService.multiply("4", "-2"));
    }

    @Test
    void divideMethodParametersContainInvalidSymbolsTest() {
        UnsupportedParameterTypeException e = assertThrows(UnsupportedParameterTypeException.class, () -> {
            calculatorService.divide("4a", "2g");
        });
        assertEquals("Caught UnsupportedParameterTypeException: For input string: \"4a\"", e.getMessage());
    }

    @Test
    void divideMethodParametersContainValidSymbolsTest() throws UnexpectedMajorException {
        assertEquals(2, calculatorService.divide("4.0", "2"));
        assertEquals(-2, calculatorService.divide("-4", "2.0"));
        assertEquals(-2, calculatorService.divide("4", "-2"));
    }

    @Test
    void dividerIsZeroTest() {
        DivisionByZeroOperationException e = assertThrows(DivisionByZeroOperationException.class, () -> {
            calculatorService.divide("4", "0");
        });
        assertEquals("Divider is zero", e.getMessage());
    }

    @Test
    void exceptionHappenedDuringCalculationTest() {
        UnexpectedMajorException e = assertThrows(UnexpectedMajorException.class, () -> {
            calculatorService.multiply("4", null);
        });
        assertEquals("Caught UnexpectedMajorException: Cannot invoke \"String.trim()\" because \"in\" is null",
                e.getMessage());
    }
}
