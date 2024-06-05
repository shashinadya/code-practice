package database.service.service;

import code.practice.exceptions.CriticalDatabaseException;
import code.practice.exceptions.DatabaseDoesNotExistException;
import code.practice.exceptions.ProvidedIdDoesNotExistException;
import database.service.entity.Student;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonDatabaseServiceTest {
    private final JsonDatabaseService jsonDatabaseService = new JsonDatabaseService();

    public JsonDatabaseServiceTest() throws CriticalDatabaseException {
    }

    @Test
    public void createTableTest() throws IOException {
        assertTrue(jsonDatabaseService.createTable(Student.class));
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void addNewRecordToTablePositiveTest() throws IOException, DatabaseDoesNotExistException {
        Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);
        Student secondStudent = new Student("Nadezhda Shashina", 1, 4.5);

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
        Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);
        DatabaseDoesNotExistException exception = assertThrows(DatabaseDoesNotExistException.class, () ->
                jsonDatabaseService.addNewRecordToTable(firstStudent));

        assertEquals("Database does not exist.", exception.getMessage());
    }

    @Test
    public void updateRecordInTableWIthCorrectIdTest() throws IOException, DatabaseDoesNotExistException,
            ProvidedIdDoesNotExistException {
        Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);
        Student secondStudent = new Student("Nadezhda Shashina", 1, 4.5);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        assertEquals(secondStudent, jsonDatabaseService.updateRecordInTable(secondStudent, 0));
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void updateRecordInTableWIthIncorrectIdTest() throws IOException, DatabaseDoesNotExistException {
        Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);
        Student secondStudent = new Student("Nadezhda Shashina", 1, 4.5);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        ProvidedIdDoesNotExistException exception = assertThrows(ProvidedIdDoesNotExistException.class, () ->
                jsonDatabaseService.updateRecordInTable(secondStudent, 2));

        assertEquals("Entity with provided Id does not exist.", exception.getMessage());
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void removeRecordFromTableTest() throws IOException, DatabaseDoesNotExistException {
        Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);
        Student secondStudent = new Student("Nadezhda Shashina", 1, 4.5);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        assertTrue(jsonDatabaseService.removeRecordFromTable(Student.class, 0));
        assertFalse(jsonDatabaseService.removeRecordFromTable(Student.class, 3));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void removeAllRecordsFromTableTest() throws IOException, DatabaseDoesNotExistException {
        Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);
        Student secondStudent = new Student("Nadezhda Shashina", 1, 4.5);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        jsonDatabaseService.removeAllRecordsFromTable(Student.class);
        Path databasePath = Path.of(jsonDatabaseService.getDatabasePath(Student.class));
        assertEquals("[]", Files.readString(databasePath));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByIdTest() throws IOException, ProvidedIdDoesNotExistException, DatabaseDoesNotExistException {
        Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);
        Student secondStudent = new Student("Nadezhda Shashina", 1, 4.5);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        Student receivedStudentById = jsonDatabaseService.getById(Student.class, 1);

        assertEquals(secondStudent, receivedStudentById);
        assertEquals(1, receivedStudentById.getId());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getByIdWhenIdDoesNotExistTest() throws IOException, DatabaseDoesNotExistException {
        Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        ProvidedIdDoesNotExistException exception = assertThrows(ProvidedIdDoesNotExistException.class, () ->
                jsonDatabaseService.getById(Student.class, 3));
        assertEquals("Entity with provided Id does not exist.", exception.getMessage());

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    public void getAllRecordsFromTableTest() throws IOException, DatabaseDoesNotExistException {
        Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);
        Student secondStudent = new Student("Nadezhda Shashina", 1, 4.5);
        List<Student> students = new ArrayList<>(List.of(firstStudent, secondStudent));

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        assertEquals(students, jsonDatabaseService.getAllRecordsFromTable(Student.class));

        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }
}
