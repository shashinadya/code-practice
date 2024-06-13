package database.service.helper;

import code.practice.exceptions.database.EmptyValueException;
import code.practice.exceptions.database.IncorrectPropertyNameException;
import code.practice.exceptions.database.IncorrectValueTypeException;
import code.practice.exceptions.database.NullPropertyNameOrValueException;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Validator {

    public <V> void validateDatabaseFilters(Field[] declaredFields, Set<Pair<String, V>> filters)
            throws NullPropertyNameOrValueException, EmptyValueException, IncorrectPropertyNameException,
            IncorrectValueTypeException {

        Set<String> validFieldNames = Arrays.stream(declaredFields)
                .map(Field::getName)
                .collect(Collectors.toSet());

        for (Pair<String, V> filter : filters) {
            String propertyName = filter.getKey();
            V expectedValue = filter.getValue();

            if (expectedValue == null || propertyName == null) {
                throw new NullPropertyNameOrValueException("Property name and value cannot be null.");
            }
            if (expectedValue.equals("")) {
                throw new EmptyValueException("Value cannot be empty.");
            }
            if (!validFieldNames.contains(propertyName)) {
                throw new IncorrectPropertyNameException("Incorrect property name: " + propertyName);
            }

            Field matchingField = Arrays.stream(declaredFields)
                    .filter(field -> field.getName().equals(propertyName))
                    .findFirst()
                    .orElseThrow(() -> new IncorrectPropertyNameException("Incorrect property name: " + propertyName));

            if (!matchingField.getType().isInstance(expectedValue)) {
                throw new IncorrectValueTypeException("Incorrect value type for filter: " + propertyName
                        + ". Expected: " + matchingField.getType().getName() + ", but got: " + expectedValue.getClass().getName());
            }
        }
    }
}
