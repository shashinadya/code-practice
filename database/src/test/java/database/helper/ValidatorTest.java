package database.helper;

import database.entity.Student;
import database.exception.EmptyValueException;
import database.exception.IncorrectPropertyNameException;
import database.exception.NullPropertyNameOrValueException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static database.helper.Validator.FILTER_CANNOT_BE_EMPTY_MESSAGE;
import static database.helper.Validator.FILTER_CANNOT_BE_NULL_MESSAGE;
import static database.helper.Validator.INCORRECT_FILTER_NAME_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorTest {
    private final Field fullNameField = Student.class.getDeclaredField("fullName");
    private final Field averageScoreField = Student.class.getDeclaredField("averageScore");
    private final List<Field> declaredFields = List.of(fullNameField, averageScoreField);

    public ValidatorTest() throws NoSuchFieldException {
    }

    @Test
    void validateDatabaseFiltersPropertyNameIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>();
        filters.put(null, List.of("0"));

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                Validator.validateDatabaseFilters(declaredFields, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersValueIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>();
        filters.put("averageScore", null);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                Validator.validateDatabaseFilters(declaredFields, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersValueIsEmptyTest() {
        Map<String, List<String>> filters = Map.of("fullName", List.of());

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                Validator.validateDatabaseFilters(declaredFields, filters));
        assertEquals(FILTER_CANNOT_BE_EMPTY_MESSAGE, exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersValuesContainOnlyEmptyStringTest() {
        Map<String, List<String>> filters = Map.of("fullName", List.of(""));

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                Validator.validateDatabaseFilters(declaredFields, filters));
        assertEquals(FILTER_CANNOT_BE_EMPTY_MESSAGE, exception.getMessage());
    }

    @Test
    void validateDatabaseFiltersIncorrectPropertyNameTest() {
        Map<String, List<String>> filters = Map.of("firstName", List.of("FirstName1"));

        IncorrectPropertyNameException exception = assertThrows(IncorrectPropertyNameException.class, () ->
                Validator.validateDatabaseFilters(declaredFields, filters));
        assertEquals(INCORRECT_FILTER_NAME_MESSAGE + ": firstName", exception.getMessage());
    }
}
