package database.helper;

import database.exception.EmptyValueException;
import database.exception.IncorrectPropertyNameException;
import database.exception.NullPropertyNameOrValueException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class Validator {
    public static final String FILTER_CANNOT_BE_NULL_MESSAGE = "Property name and value cannot be null";
    public static final String FILTER_CANNOT_BE_EMPTY_MESSAGE = "Value cannot be empty";
    public static final String INCORRECT_FILTER_NAME_MESSAGE = "Incorrect filter name";

    public static void validateDatabaseFilters(List<Field> declaredFields, Map<String, List<String>> filters)
            throws NullPropertyNameOrValueException, EmptyValueException, IncorrectPropertyNameException {
        for (Map.Entry<String, List<String>> filter : filters.entrySet()) {
            String propertyName = filter.getKey();
            List<String> expectedValues = filter.getValue();

            if (expectedValues == null || propertyName == null) {
                throw new NullPropertyNameOrValueException(FILTER_CANNOT_BE_NULL_MESSAGE);
            }
            if (expectedValues.isEmpty() || expectedValues.stream().anyMatch(String::isBlank)) {
                throw new EmptyValueException(FILTER_CANNOT_BE_EMPTY_MESSAGE);
            }

            declaredFields.stream()
                    .filter(field -> field.getName().equals(propertyName))
                    .findAny()
                    .orElseThrow(() -> new IncorrectPropertyNameException(INCORRECT_FILTER_NAME_MESSAGE +
                            ": " + propertyName));
        }
    }
}
