package database.helper;

import database.exception.EmptyValueException;
import database.exception.IncorrectPropertyNameException;
import database.exception.IncorrectValueTypeException;
import database.exception.NullPropertyNameOrValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

public class Validator {
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);

    public static <V> void validateDatabaseFilters(Field[] declaredFields, Map<String, V> filters)
            throws NullPropertyNameOrValueException, EmptyValueException, IncorrectPropertyNameException,
            IncorrectValueTypeException {

        for (Map.Entry<String, V> filter : filters.entrySet()) {
            String propertyName = filter.getKey();
            V expectedValue = filter.getValue();

            if (expectedValue == null || propertyName == null) {
                logger.error("Property name or value is null.");
                throw new NullPropertyNameOrValueException("Property name and value cannot be null.");
            }
            if (expectedValue.equals("")) {
                logger.error("Value is empty.");
                throw new EmptyValueException("Value cannot be empty.");
            }

            Field matchingField = Arrays.stream(declaredFields)
                    .filter(field -> field.getName().equals(propertyName))
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.error("Property name is incorrect.");
                        return new IncorrectPropertyNameException("Incorrect property name: " + propertyName);
                    });

            if (!matchingField.getType().isInstance(expectedValue)) {
                throw new IncorrectValueTypeException("Incorrect value type for filter: " + propertyName
                        + ". Expected: " + matchingField.getType().getName() + ", but got: " + expectedValue.getClass().getName());
            }
        }
    }
}
