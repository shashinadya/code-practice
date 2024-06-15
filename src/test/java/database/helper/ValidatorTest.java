package database.helper;

import database.service.JsonDatabaseService;
import database.entity.Student;
import database.exception.EmptyValueException;
import database.exception.IncorrectPropertyNameException;
import database.exception.IncorrectValueTypeException;
import database.exception.NullPropertyNameOrValueException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatorTest {
    private final Student firstStudent = new Student("Mikhail Shashin", 5.0, List.of(0));
    private final JsonDatabaseService jsonDatabaseService = new JsonDatabaseService();

    @Test
    public void getByFiltersPropertyNameIsNullTest() throws IOException {
        Map<String, Object> filters = new HashMap<>();
        filters.put(null, 0);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Property name and value cannot be null.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersValueIsNullTest() throws IOException {
        Map<String, Object> filters = new HashMap<>();
        filters.put("averageScore", null);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Property name and value cannot be null.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersValueIsEmptyTest() throws IOException {
        Map<String, Object> filters = new HashMap<>();
        filters.put("fullName", "");

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Value cannot be empty.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersIncorrectPropertyNameTest() throws IOException {
        Map<String, Object> filters = new HashMap<>();
        filters.put("firstName", "Mikhail");

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IncorrectPropertyNameException exception = assertThrows(IncorrectPropertyNameException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Incorrect property name: firstName", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersIncorrectValueTypeTest() throws IOException {
        Map<String, Object> filters = new HashMap<>();
        filters.put("fullName", 100);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IncorrectValueTypeException exception = assertThrows(IncorrectValueTypeException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertTrue(exception.getMessage().contains("Incorrect value type for filter: fullName"));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }
}
