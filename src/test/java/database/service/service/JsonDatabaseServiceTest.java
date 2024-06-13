package database.service.service;

import code.practice.exceptions.database.DatabaseDoesNotExistException;
import code.practice.exceptions.database.EmptyValueException;
import code.practice.exceptions.database.IdDoesNotExistException;
import code.practice.exceptions.database.IncorrectPropertyNameException;
import code.practice.exceptions.database.IncorrectValueTypeException;
import code.practice.exceptions.database.NullPropertyNameOrValueException;
import database.service.entity.Student;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static database.service.service.JsonDatabaseService.EMPTY_BRACKETS_TO_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonDatabaseServiceTest {
    private final Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);
    private final Student secondStudent = new Student("Nadezhda Shashina", 1, 4.5);
    private final Student thirdStudent = new Student("Nika Shashina", 1, 5.0);
    private final Student fourthStudent = new Student("Harry Potter", 0, 5.0);
    private final JsonDatabaseService jsonDatabaseService = new JsonDatabaseService();
    ;

    @Test
    public void createTableTest() {
        assertTrue(jsonDatabaseService.createTable(Student.class));
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void addNewRecordToTablePositiveTest() throws DatabaseDoesNotExistException {
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
    public void updateRecordInTableWIthCorrectIdTest() throws DatabaseDoesNotExistException, IdDoesNotExistException {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);

        assertEquals(secondStudent, jsonDatabaseService.updateRecordInTable(secondStudent, 1));
        assertEquals(secondStudent, jsonDatabaseService.getById(Student.class, 1));
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void updateRecordInTableWIthIncorrectIdTest() throws DatabaseDoesNotExistException {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IdDoesNotExistException exception = assertThrows(IdDoesNotExistException.class, () ->
                jsonDatabaseService.updateRecordInTable(secondStudent, 12));

        assertEquals("Entity with provided Id does not exist.", exception.getMessage());
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void removeRecordFromTableTest() throws DatabaseDoesNotExistException {
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
    public void removeAllRecordsFromTableTest() throws IOException, DatabaseDoesNotExistException {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        jsonDatabaseService.removeAllRecordsFromTable(Student.class);
        Path databasePath = Path.of(jsonDatabaseService.getDatabasePath(Student.class));
        assertEquals(EMPTY_BRACKETS_TO_JSON, Files.readString(databasePath));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByIdTest() throws IdDoesNotExistException, DatabaseDoesNotExistException {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        Student receivedStudentById = jsonDatabaseService.getById(Student.class, 1);

        assertEquals(secondStudent, receivedStudentById);
        assertEquals(1, receivedStudentById.getId());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByIdWhenIdDoesNotExistTest() throws DatabaseDoesNotExistException {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IdDoesNotExistException exception = assertThrows(IdDoesNotExistException.class, () ->
                jsonDatabaseService.getById(Student.class, 13));
        assertEquals("Entity with provided Id does not exist.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getAllRecordsFromTableTest() throws DatabaseDoesNotExistException {
        List<Student> students = List.of(firstStudent, secondStudent);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        assertEquals(students, jsonDatabaseService.getAllRecordsFromTable(Student.class));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersAllFiltersMatchTest() throws DatabaseDoesNotExistException {
        List<Student> students = List.of(firstStudent, fourthStudent);

        Set<Pair<String, Object>> filters = new HashSet<>();
        filters.add(new ImmutablePair<>("courseId", 0));
        filters.add(new ImmutablePair<>("averageScore", 5.0));

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        jsonDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersOnlyOneFilterMatchesTest() throws DatabaseDoesNotExistException {
        List<Student> students = List.of(thirdStudent);

        Set<Pair<String, Object>> filters = new HashSet<>();
        filters.add(new ImmutablePair<>("courseId", 1));
        filters.add(new ImmutablePair<>("averageScore", 5.0));

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        jsonDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersNoFilterMatchesTest() throws DatabaseDoesNotExistException {
        List<Student> students = new ArrayList<>();

        Set<Pair<String, Object>> filters = new HashSet<>();
        filters.add(new ImmutablePair<>("courseId", 4));
        filters.add(new ImmutablePair<>("averageScore", 3.0));

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersPropertyNameIsNullTest() {
        Set<Pair<String, Object>> filters = new HashSet<>();
        filters.add(new ImmutablePair<>(null, 0));

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Property name and value cannot be null.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersValueIsNullTest() {
        Set<Pair<String, Object>> filters = new HashSet<>();
        filters.add(new ImmutablePair<>("averageScore", null));

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Property name and value cannot be null.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersValueIsEmptyTest() {
        Set<Pair<String, Object>> filters = new HashSet<>();
        filters.add(new ImmutablePair<>("fullName", ""));

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Value cannot be empty.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersIncorrectPropertyNameTest() {
        Set<Pair<String, Object>> filters = new HashSet<>();
        filters.add(new ImmutablePair<>("firstName", "Mikhail"));

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IncorrectPropertyNameException exception = assertThrows(IncorrectPropertyNameException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Incorrect property name: firstName", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByFiltersIncorrectValueTypeTest() {
        Set<Pair<String, Object>> filters = new HashSet<>();
        filters.add(new ImmutablePair<>("fullName", 100));

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IncorrectValueTypeException exception = assertThrows(IncorrectValueTypeException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals("Incorrect value type for filter: fullName. Expected: java.lang.String, " +
                        "but got: java.lang.Integer",
                exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }
}
