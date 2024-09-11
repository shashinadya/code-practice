package practice.tasks;

/*
 * Write calculator database.service, which provide ability to multiply and divide numbers:
 *
 * double multiply(String a, String b) {...}
 * double divide(String a, String b) {...}
 *
 * Add validation and issues handling:
 *     throw UnsupportedParameterTypeException if parameter cannot be parsed.
 *     throw DivisionByZeroOperationException if divider is zero.
 *     throw UnexpectedMajorException if any database.exception happened during calculation. We don't want to miss this issue in the calling code.
 *
 * You should decide what to do with these practice.exceptions from the calling code. Probably not all of them should be handled.
 * */

import practice.exceptions.DivisionByZeroOperationException;
import practice.exceptions.UnexpectedMajorException;
import practice.exceptions.UnsupportedParameterTypeException;

public class CalculatorService {
    double parsedFirstParameter;
    double parsedSecondParameter;

    public double multiply(String a, String b) throws UnexpectedMajorException {
        try {
            parsedFirstParameter = Double.parseDouble(a);
            parsedSecondParameter = Double.parseDouble(b);
            return parsedFirstParameter * parsedSecondParameter;
        } catch (NumberFormatException e) {
            throw new UnsupportedParameterTypeException("Caught UnsupportedParameterTypeException: " + e.getMessage());
        } catch (Exception e) {
            throw new UnexpectedMajorException("Caught UnexpectedMajorException: " + e.getMessage());
        }
    }

    public double divide(String a, String b) throws UnexpectedMajorException {
        try {
            parsedFirstParameter = Double.parseDouble(a);
            parsedSecondParameter = Double.parseDouble(b);
        } catch (NumberFormatException e) {
            throw new UnsupportedParameterTypeException("Caught UnsupportedParameterTypeException: " + e.getMessage());
        }
        if (parsedSecondParameter == 0) {
            throw new DivisionByZeroOperationException("Divider is zero");
        }
        try {
            return parsedFirstParameter / parsedSecondParameter;
        } catch (Exception e) {
            throw new UnexpectedMajorException("Caught UnexpectedMajorException: " + e.getMessage());
        }
    }
}
