package database.service;

import database.exception.DatabaseDoesNotExistException;
import database.exception.IdDoesNotExistException;
import database.entity.Student;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static database.service.JsonDatabaseService.EMPTY_BRACKETS_TO_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonDatabaseServiceTest {
    private final Student firstStudent = new Student("Mikhail Shashin", 5.0, List.of(0));
    private final Student secondStudent = new Student("Nadezhda Shashina", 4.5, List.of(1));
    private final Student thirdStudent = new Student("Nika Shashina", 5.0, List.of(1));
    private final Student fourthStudent = new Student("Harry Potter", 5.0, List.of(0));
    private final JsonDatabaseService jsonDatabaseService = new JsonDatabaseService();

    @Test
    public void createTableTest() {
        assertTrue(jsonDatabaseService.createTable(Student.class));
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void addNewRecordToTablePositiveTest() throws IOException {
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

        assertEquals("Database does not exist.", exception.getMessage());
    }

    @Test
    public void updateRecordInTableWIthCorrectIdTest() throws IOException {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);

        assertEquals(secondStudent, jsonDatabaseService.updateRecordInTable(secondStudent, 1));
        assertEquals(secondStudent, jsonDatabaseService.getById(Student.class, 1));
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void updateRecordInTableWIthIncorrectIdTest() throws IOException {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IdDoesNotExistException exception = assertThrows(IdDoesNotExistException.class, () ->
                jsonDatabaseService.updateRecordInTable(secondStudent, 12));

        assertEquals("Entity with provided Id does not exist.", exception.getMessage());
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void removeRecordFromTableTest() throws IOException {
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
    public void removeAllRecordsFromTableTest() throws IOException {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        jsonDatabaseService.removeAllRecordsFromTable(Student.class);
        Path databasePath = Path.of(jsonDatabaseService.getDatabasePath(Student.class));
        assertEquals(EMPTY_BRACKETS_TO_JSON, Files.readString(databasePath));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByIdTest() throws IOException {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        Student receivedStudentById = jsonDatabaseService.getById(Student.class, 1);

        assertEquals(secondStudent, receivedStudentById);
        assertEquals(1, receivedStudentById.getId());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByIdWhenIdDoesNotExistTest() throws IOException {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        assertNull(jsonDatabaseService.getById(Student.class, 13));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getAllRecordsFromTableTest() throws IOException {
        List<Student> students = List.of(firstStudent, secondStudent);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        assertEquals(students, jsonDatabaseService.getAllRecordsFromTable(Student.class));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersAllFiltersMatchTest() throws IOException {
        List<Student> students = List.of(firstStudent, fourthStudent);

        Map<String, Object> filters = new HashMap<>();
        filters.put("courseIds", List.of(0));
        filters.put("averageScore", 5.0);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        jsonDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersOnlyOneFilterMatchesTest() throws IOException {
        List<Student> students = List.of(thirdStudent);

        Map<String, Object> filters = new HashMap<>();
        filters.put("courseIds", List.of(1));
        filters.put("averageScore", 5.0);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        jsonDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersNoFilterMatchesTest() throws IOException {
        List<Student> students = new ArrayList<>();

        Map<String, Object> filters = new HashMap<>();
        filters.put("courseIds", List.of(4));
        filters.put("averageScore", 3.0);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }
}
