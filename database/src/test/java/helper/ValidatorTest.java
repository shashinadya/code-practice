package helper;

import entity.Student;
import exception.EmptyValueException;
import exception.IncorrectPropertyNameException;
import exception.IncorrectValueTypeException;
import exception.NullPropertyNameOrValueException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {
    private final Field fullNameField = Student.class.getDeclaredField("fullName");
    private final Field averageScoreField = Student.class.getDeclaredField("averageScore");
    private final List<Field> declaredFields = List.of(fullNameField, averageScoreField);

    public ValidatorTest() throws NoSuchFieldException {
    }

    @Test
    void validateDatabaseFiltersPropertyNameIsNullTest() {
        Map<String, Object> filters = new HashMap<>();
        filters.put(null, 0);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                Validator.validateDatabaseFilters(declaredFields, filters));
        assertEquals("Property name and value cannot be null.", exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersValueIsNullTest() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("averageScore", null);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                Validator.validateDatabaseFilters(declaredFields, filters));
        assertEquals("Property name and value cannot be null.", exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersValueIsEmptyTest() {
        Map<String, Object> filters = Map.of("fullName", "");

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                Validator.validateDatabaseFilters(declaredFields, filters));
        assertEquals("Value cannot be empty.", exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersIncorrectPropertyNameTest() {
        Map<String, Object> filters = Map.of("firstName", "FirstName1");

        IncorrectPropertyNameException exception = assertThrows(IncorrectPropertyNameException.class, () ->
                Validator.validateDatabaseFilters(declaredFields, filters));
        assertEquals("Incorrect property name: firstName", exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersIncorrectValueTypeTest() {
        Map<String, Object> filters = Map.of("fullName", 100);

        IncorrectValueTypeException exception = assertThrows(IncorrectValueTypeException.class, () ->
                Validator.validateDatabaseFilters(declaredFields, filters));
        assertTrue(exception.getMessage().contains("Incorrect value type for filter: fullName"));
    }
}
