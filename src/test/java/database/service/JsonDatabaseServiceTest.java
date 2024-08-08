package database.service;

import database.exception.DatabaseDoesNotExistException;
import database.exception.EmptyValueException;
import database.exception.IdDoesNotExistException;
import database.entity.Student;
import database.exception.IdProvidedManuallyException;
import database.exception.IncorrectPropertyNameException;
import database.exception.IncorrectValueTypeException;
import database.exception.NullPropertyNameOrValueException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static database.service.JsonDatabaseService.DB_FILE_NOT_EXIST;
import static database.service.JsonDatabaseService.EMPTY_BRACKETS_TO_JSON;
import static database.service.JsonDatabaseService.ENTITY_DOES_NOT_EXIST;
import static database.service.JsonDatabaseService.ID_PROVIDED_MANUALLY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonDatabaseServiceTest {
    private final Student firstStudent = new Student("FirstName1 LastName1", 5.0);
    private final Student secondStudent = new Student("FirstName2 LastName2", 4.5);
    private final Student thirdStudent = new Student("FirstName3 LastName3", 5.0);
    private final Student fourthStudent = new Student("FirstName1 LastName1", 5.0);
    private final JsonDatabaseService jsonDatabaseService = new JsonDatabaseService();

    @Test
    public void createTableTest() {
        assertTrue(jsonDatabaseService.createTable(Student.class));
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void addNewRecordToTablePositiveTest() {
        jsonDatabaseService.createTable(Student.class);

        Student receivedFirstStudent = jsonDatabaseService.addNewRecordToTable(firstStudent);
        assertEquals(firstStudent, receivedFirstStudent);
        assertEquals(0, receivedFirstStudent.getId());

        Student receivedSecondStudent = jsonDatabaseService.addNewRecordToTable(secondStudent);
        assertEquals(secondStudent, receivedSecondStudent);
        assertEquals(1, receivedSecondStudent.getId());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void addNewRecordWhenDatabaseDoesNotExistTest() {
        DatabaseDoesNotExistException exception = assertThrows(DatabaseDoesNotExistException.class, () ->
                jsonDatabaseService.addNewRecordToTable(firstStudent));

        assertEquals(DB_FILE_NOT_EXIST, exception.getMessage());
    }

    @Test
    public void addNewRecordIdProvidedManually() {
        jsonDatabaseService.createTable(Student.class);

        Student studentWithManuallyProvidedId = new Student("FirstName1 LastName1", 5.0);
        studentWithManuallyProvidedId.setId(7);

        IdProvidedManuallyException exception = assertThrows(IdProvidedManuallyException.class, () ->
                jsonDatabaseService.addNewRecordToTable(studentWithManuallyProvidedId));

        assertEquals(ID_PROVIDED_MANUALLY, exception.getMessage());
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void updateRecordInTableWIthCorrectIdTest() {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        //Check that original Id is not changed
        secondStudent.setId(5);

        assertEquals(secondStudent, jsonDatabaseService.updateRecordInTable(secondStudent, 1));
        assertEquals(secondStudent, jsonDatabaseService.getById(Student.class, 1));
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void updateRecordInTableWIthIncorrectIdTest() {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IdDoesNotExistException exception = assertThrows(IdDoesNotExistException.class, () ->
                jsonDatabaseService.updateRecordInTable(secondStudent, 12));

        assertEquals(ENTITY_DOES_NOT_EXIST, exception.getMessage());
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void removeRecordFromTableTest() {
        List<Student> studentsBeforeDeletion = List.of(firstStudent, secondStudent);
        List<Student> studentsAfterDeletion = List.of(secondStudent);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        assertEquals(studentsBeforeDeletion, jsonDatabaseService.getAllRecordsFromTable(Student.class));
        assertTrue(jsonDatabaseService.removeRecordFromTable(Student.class, 0));
        assertEquals(studentsAfterDeletion, jsonDatabaseService.getAllRecordsFromTable(Student.class));
        assertFalse(jsonDatabaseService.removeRecordFromTable(Student.class, 0));
        assertFalse(jsonDatabaseService.removeRecordFromTable(Student.class, 13));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void removeAllRecordsFromTableTest() {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        jsonDatabaseService.removeAllRecordsFromTable(Student.class);
        Path databasePath = Path.of(jsonDatabaseService.getDatabasePath(Student.class));
        assertEquals(EMPTY_BRACKETS_TO_JSON, jsonDatabaseService.readDatabaseFile(databasePath));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByIdTest() {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        Student receivedStudentById = jsonDatabaseService.getById(Student.class, 1);

        assertEquals(secondStudent, receivedStudentById);
        assertEquals(1, receivedStudentById.getId());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByIdWhenIdDoesNotExistTest() {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        assertNull(jsonDatabaseService.getById(Student.class, 13));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getAllRecordsFromTableTest() {
        List<Student> students = List.of(firstStudent, secondStudent);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        assertEquals(students, jsonDatabaseService.getAllRecordsFromTable(Student.class));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersAllFiltersMatchTest() {
        List<Student> students = List.of(firstStudent, fourthStudent);

        Map<String, Object> filters = Map.of("fullName", "FirstName1 LastName1", "averageScore", 5.0);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        jsonDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersOnlyOneFilterMatchesTest() {
        List<Student> students = List.of();

        Map<String, Object> filters = Map.of("fullName", "Harry Potter", "averageScore", 5.0);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        jsonDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersNoFilterMatchesTest() {
        List<Student> students = List.of();

        Map<String, Object> filters = Map.of("fullName", "FirstName1 LastName1", "averageScore", 3.0);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersPropertyNameIsNullTest() {
        Map<String, Object> filters = new HashMap<>() {{
            put(null, 0);
        }};

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Property name and value cannot be null.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersValueIsNullTest() {
        Map<String, Object> filters = new HashMap<>() {{
            put("averageScore", null);
        }};

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Property name and value cannot be null.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersValueIsEmptyTest() {
        Map<String, Object> filters = Map.of("fullName", "");

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Value cannot be empty.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersIncorrectPropertyNameTest() {
        Map<String, Object> filters = Map.of("firstName", "FirstName1");

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IncorrectPropertyNameException exception = assertThrows(IncorrectPropertyNameException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Incorrect property name: firstName", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersIncorrectValueTypeTest() {
        Map<String, Object> filters = Map.of("fullName", 100);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IncorrectValueTypeException exception = assertThrows(IncorrectValueTypeException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertTrue(exception.getMessage().contains("Incorrect value type for filter: fullName"));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }
}
