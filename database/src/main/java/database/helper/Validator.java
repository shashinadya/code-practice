package database.helper;

import database.exception.EmptyValueException;
import database.exception.IncorrectPropertyNameException;
import database.exception.NullPropertyNameOrValueException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * The {@code Validator} class provides a set of utility methods for performing various validation tasks.
 * <p>
 * This class is designed to validate input data, such as property names, values, and other entities.
 * It offers general-purpose validation methods that ensure inputs meet specific criteria, such as
 * non-null values, non-empty strings, or matching declared fields of a class.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class Validator {
    public static final String FILTER_CANNOT_BE_NULL_MESSAGE = "Property name and value cannot be null";
    public static final String FILTER_CANNOT_BE_EMPTY_MESSAGE = "Value cannot be empty";
    public static final String INCORRECT_FILTER_NAME_MESSAGE = "Incorrect filter name";

    /**
     * Validates the filters applied to database queries.
     *
     * <p>This method checks whether each filter's property name exists among the declared fields
     * of a given class, whether the property name and values are not null, and whether the
     * values are not empty or blank. If any of these validation rules are violated, an appropriate
     * exception is thrown.
     *
     * @param declaredFields a list of fields that are declared in the target entity class
     * @param filters        a map of filter names (property names) and their corresponding filter values
     * @throws NullPropertyNameOrValueException if a filter's property name or value is null
     * @throws EmptyValueException              if a filter's value is empty or blank
     * @throws IncorrectPropertyNameException   if a filter's property name does not match any declared field
     */
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
