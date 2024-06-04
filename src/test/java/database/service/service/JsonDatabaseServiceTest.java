package database.service.service;

import code.practice.exceptions.CriticalDatabaseException;
import code.practice.exceptions.DatabaseDoesNotExistException;
import code.practice.exceptions.ProvidedIdDoesNotExistException;
import database.service.entity.Student;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonDatabaseServiceTest {
    JsonDatabaseService jsonDatabaseService = new JsonDatabaseService();

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

}
