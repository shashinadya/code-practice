package database.dao;

import database.entity.BaseEntity;
import database.exception.EmptyValueException;
import database.exception.IdProvidedManuallyException;
import database.exception.IncorrectPropertyNameException;
import database.exception.NullOrEmptyListException;
import database.exception.NullPropertyNameOrValueException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final String SHUTDOWN_MESSAGE = "Database work is stopped";

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

    /**
     * Validates that the ID field of the given entity has not been manually set.
     *
     * <p>If the ID is already populated (i.e., not null), an {@link IdProvidedManuallyException}
     * is thrown to enforce automatic ID management.
     *
     * @param entity the entity to check
     * @throws IdProvidedManuallyException if the entity's ID is manually provided (not null)
     */
    public <T extends BaseEntity> void validateIdNotProvidedManually(T entity) {
        if (entity.getId() != null) {
            throw new IdProvidedManuallyException(ID_PROVIDED_MANUALLY);
        }
    }

    /**
     * Retrieves all declared fields of a given entity class, including fields declared in
     * its superclass hierarchy.
     *
     * @param entityClass the class whose fields are to be retrieved
     * @return a list of all declared fields in the class and its superclasses
     */
    public List<Field> getAllFields(Class<?> entityClass) {
        List<Field> fields = new ArrayList<>();
        while (entityClass != null && entityClass != Object.class) {
            fields.addAll(Arrays.asList(entityClass.getDeclaredFields()));
            entityClass = entityClass.getSuperclass();
        }
        return fields;
    }

    /**
     * Validates a list of entities by ensuring the list is neither null nor empty, and by
     * validating that none of the entities have a manually provided ID.
     *
     * @param entities the list of entities to validate
     * @throws NullOrEmptyListException    if the entity list is null or empty
     * @throws IdProvidedManuallyException if any entity in the list has a manually provided ID
     */
    public <T extends BaseEntity> void validateEntities(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            throw new NullOrEmptyListException(ENTITIES_LIST_NULL_OR_EMPTY);
        }
        for (T entity : entities) {
            validateIdNotProvidedManually(entity);
        }
    }
}
