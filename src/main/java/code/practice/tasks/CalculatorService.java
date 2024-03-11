package code.practice.tasks;

/*
 * Write calculator service, which provide ability to multiply and divide numbers:
 *
 * double multiply(String a, String b) {...}
 * double divide(String a, String b) {...}
 *
 * Add validation and issues handling:
 *     throw UnsupportedParameterTypeException if parameter cannot be parsed.
 *     throw DivisionByZeroOperationException if divider is zero.
 *     throw UnexpectedMajorException if any exception happened during calculation. We don't want to miss this issue in the calling code.
 *
 * You should decide what to do with these exceptions from the calling code. Probably not all of them should be handled.
 * */

import code.practice.exceptions.DivisionByZeroOperationException;
import code.practice.exceptions.UnexpectedMajorException;
import code.practice.exceptions.UnsupportedParameterTypeException;

public class CalculatorService {
    double parsedFirstParameter;
    double parsedSecondParameter;

    public double multiply(String a, String b) throws UnsupportedParameterTypeException, UnexpectedMajorException {
        try {
            parsedFirstParameter = Double.parseDouble(a);
            parsedSecondParameter = Double.parseDouble(b);
        } catch (NumberFormatException e) {
            throw new UnsupportedParameterTypeException("Caught UnsupportedParameterTypeException");
        }
        try {
            return parsedFirstParameter * parsedSecondParameter;
        } catch (Exception e) {
            throw new UnexpectedMajorException("Caught UnexpectedMajorException");
        }
    }

    public double divide(String a, String b) throws DivisionByZeroOperationException, UnexpectedMajorException,
            UnsupportedParameterTypeException {
        try {
            parsedFirstParameter = Double.parseDouble(a);
            parsedSecondParameter = Double.parseDouble(b);
        } catch (NumberFormatException e) {
            throw new UnsupportedParameterTypeException("Caught UnsupportedParameterTypeException");
        }
        if (parsedSecondParameter == 0) {
            throw new DivisionByZeroOperationException("Divider is zero");
        }
        try {
            return parsedFirstParameter / parsedSecondParameter;
        } catch (Exception e) {
            throw new UnexpectedMajorException("Caught UnexpectedMajorException");
        }
    }
}
