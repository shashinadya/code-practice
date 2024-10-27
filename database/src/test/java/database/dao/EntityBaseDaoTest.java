package database.dao;

import database.entity.Student;
import database.exception.EmptyValueException;
import database.exception.IncorrectPropertyNameException;
import database.exception.NullPropertyNameOrValueException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static database.dao.EntityDaoBase.FILTER_CANNOT_BE_EMPTY_MESSAGE;
import static database.dao.EntityDaoBase.FILTER_CANNOT_BE_NULL_MESSAGE;
import static database.dao.EntityDaoBase.INCORRECT_FILTER_NAME_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;

/**
 * Unit tests for the {@code EntityDaoBase} class.
 * <p>This class ensures the proper functioning of methods in {@code EntityDaoBase}.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 * @see EntityDaoBase
 */
class EntityBaseDaoTest {
    private final EntityDaoBase entityDaoBase = spy(EntityDaoBase.class);
    private final Field fullNameField = Student.class.getDeclaredField("fullName");
    private final Field averageScoreField = Student.class.getDeclaredField("averageScore");
    private final List<Field> declaredFields = List.of(fullNameField, averageScoreField);

    public EntityBaseDaoTest() throws NoSuchFieldException {
    }

    @Test
    void validateDatabaseFiltersPropertyNameIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>();
        filters.put(null, List.of("0"));

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                entityDaoBase.validateDatabaseFilters(declaredFields, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersValueIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>();
        filters.put("averageScore", null);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                entityDaoBase.validateDatabaseFilters(declaredFields, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersValueIsEmptyTest() {
        Map<String, List<String>> filters = Map.of("fullName", List.of());

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                entityDaoBase.validateDatabaseFilters(declaredFields, filters));
        assertEquals(FILTER_CANNOT_BE_EMPTY_MESSAGE, exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersValuesContainOnlyEmptyStringTest() {
        Map<String, List<String>> filters = Map.of("fullName", List.of(""));

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                entityDaoBase.validateDatabaseFilters(declaredFields, filters));
        assertEquals(FILTER_CANNOT_BE_EMPTY_MESSAGE, exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersIncorrectPropertyNameTest() {
        Map<String, List<String>> filters = Map.of("firstName", List.of("FirstName1"));

        IncorrectPropertyNameException exception = assertThrows(IncorrectPropertyNameException.class, () ->
                entityDaoBase.validateDatabaseFilters(declaredFields, filters));
        assertEquals(INCORRECT_FILTER_NAME_MESSAGE + ": firstName", exception.getMessage());
    }
}