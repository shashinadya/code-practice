package database.dao;

import database.exception.EmptyValueException;
import database.exception.IncorrectPropertyNameException;
import database.exception.NullPropertyNameOrValueException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Provides a base implementation for data access operations on entities, offering common methods
 * for database operations.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public abstract class EntityDaoBase implements EntityDao {
    public static final String ENTITY_IS_NOT_FOUND = "Entity with provided Id does not exist";
    public static final String ID_PROVIDED_MANUALLY = "User cannot provide id manually. Ids are filled automatically.";
    public static final String INVALID_PARAMETER_VALUE = "Invalid parameter value. " +
            "Limit value should be in(0..{MAX_LIMIT_VALUE}), offset value should be >= 0";
    public static final String ENTITIES_LIST_NULL_OR_EMPTY = "Entities list cannot be null or empty";
    public static final String IDS_LIST_NULL_OR_EMPTY = "IDs list cannot be null or empty";
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
    public void validateDatabaseFilters(List<Field> declaredFields, Map<String, List<String>> filters)
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
