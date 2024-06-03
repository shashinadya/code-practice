package database.service.service;

import code.practice.exceptions.CriticalDatabaseException;
import code.practice.exceptions.DatabaseDoesNotExistException;
import code.practice.exceptions.TableIsNotCompatibleWithEntityException;
import database.service.entity.Student;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void addNewRecordToTable() throws IOException, TableIsNotCompatibleWithEntityException, DatabaseDoesNotExistException {
        Student firstStudent = new Student("Mikhail Shashin", 0, 5.0);
        Student secondStudent = new Student("Nadezhda Shashina", 1, 4.5);
        
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);


        //assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }


}
