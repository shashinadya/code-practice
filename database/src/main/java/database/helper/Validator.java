package database.helper;

import database.exception.EmptyValueException;
import database.exception.IncorrectPropertyNameException;
import database.exception.IncorrectValueTypeException;
import database.exception.NullPropertyNameOrValueException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class Validator {

    public static <V> void validateDatabaseFilters(List<Field> declaredFields, Map<String, V> filters)
            throws NullPropertyNameOrValueException, EmptyValueException, IncorrectPropertyNameException,
            IncorrectValueTypeException {

        for (Map.Entry<String, V> filter : filters.entrySet()) {
            String propertyName = filter.getKey();
            V expectedValue = filter.getValue();

            if (expectedValue == null || propertyName == null) {
                throw new NullPropertyNameOrValueException("Property name and value cannot be null.");
            }
            if (expectedValue.equals("")) {
                throw new EmptyValueException("Value cannot be empty.");
            }

            Field matchingField = declaredFields.stream()
                    .filter(field -> field.getName().equals(propertyName))
                    .findFirst()
                    .orElseThrow(() -> new IncorrectPropertyNameException("Incorrect property name: " + propertyName));

            if (!matchingField.getType().isInstance(expectedValue)) {
                throw new IncorrectValueTypeException("Incorrect value type for filter: " + propertyName
                        + ". Expected: " + matchingField.getType().getName() + ", but got: " +
                        expectedValue.getClass().getName());
            }
        }
    }
}
