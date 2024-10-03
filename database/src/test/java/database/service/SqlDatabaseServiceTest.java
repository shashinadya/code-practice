package database.service;

import database.entity.BaseEntity;
import database.entity.Course;
import database.entity.OxfordStudent;
import database.entity.Student;
import database.exception.CreationDatabaseException;
import database.exception.DatabaseDoesNotExistException;
import database.exception.IdDoesNotExistException;
import database.exception.IdProvidedManuallyException;
import database.exception.InvalidParameterValueException;
import database.helper.Settings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static database.service.ServiceConstants.ENTITY_IS_NOT_FOUND;
import static database.service.ServiceConstants.ID_PROVIDED_MANUALLY;
import static database.service.ServiceConstants.INVALID_PARAMETER_VALUE;
import static database.service.SqlDatabaseService.TABLE_NOT_EXIST;
import static database.service.SqlDatabaseService.UNABLE_CREATE_TABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlDatabaseServiceTest {
    private final Settings settings = new Settings("Db_app_properties_files/application.properties");
    private final SqlDatabaseService sqlDatabaseService = new SqlDatabaseService(settings);
    private static final String DATABASE_NAME = "test_entities";
    private Student firstStudent;
    private Student secondStudent;
    private Student thirdStudent;
    private Student fourthStudent;

    @BeforeEach
    void setUp() {
        firstStudent = new Student("FirstName1 LastName1", 5.0);
        secondStudent = new Student("FirstName2 LastName2", 4.5);
        thirdStudent = new Student("FirstName3 LastName3", 5.0);
        fourthStudent = new Student("FirstName1 LastName1", 5.0);
    }

    @AfterEach
    void tearDown() {
        try {
            sqlDatabaseService.deleteTable(Student.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void createTableTest() throws SQLException {
        Class<? extends BaseEntity> entityClass = Student.class;

        assertTrue(sqlDatabaseService.createTable(entityClass));
        assertTrue(sqlDatabaseService.checkTableExists(DATABASE_NAME, "Student"));
    }

    @Test
    void createTableFailedTest() {
        Class<? extends BaseEntity> entityClass = Student.class;
        sqlDatabaseService.createTable(entityClass);

        CreationDatabaseException exception = assertThrows(CreationDatabaseException.class, () ->
                sqlDatabaseService.createTable(entityClass));
        assertTrue(exception.getMessage().startsWith(UNABLE_CREATE_TABLE));
    }

    @Test
    void deleteTableTest() throws SQLException {
        Class<? extends BaseEntity> entityClass = Student.class;
        sqlDatabaseService.createTable(entityClass);

        assertTrue(sqlDatabaseService.deleteTable(entityClass));
        assertFalse(sqlDatabaseService.checkTableExists(DATABASE_NAME, "Student"));
    }

    @Test
    void addNewRecordToTablePositiveTest() {
        Class<? extends BaseEntity> entityClass = Student.class;

        sqlDatabaseService.createTable(entityClass);

        Student receivedFirstStudent = sqlDatabaseService.addNewRecordToTable(firstStudent);
        assertEquals(firstStudent, receivedFirstStudent);
        assertEquals(1, receivedFirstStudent.getId());

        Student receivedSecondStudent = sqlDatabaseService.addNewRecordToTable(secondStudent);
        assertEquals(secondStudent, receivedSecondStudent);
        assertEquals(2, receivedSecondStudent.getId());
    }

    @Test
    void addNewRecordWhenDatabaseDoesNotExistTest() {
        DatabaseDoesNotExistException exception = assertThrows(DatabaseDoesNotExistException.class, () ->
                sqlDatabaseService.addNewRecordToTable(firstStudent));

        assertTrue(exception.getMessage().startsWith(TABLE_NOT_EXIST));
    }

    @Test
    void addNewRecordIdProvidedManually() {
        sqlDatabaseService.createTable(Student.class);

        Student studentWithManuallyProvidedId = new Student("FirstName1 LastName1", 5.0);
        studentWithManuallyProvidedId.setId(7);

        IdProvidedManuallyException exception = assertThrows(IdProvidedManuallyException.class, () ->
                sqlDatabaseService.addNewRecordToTable(studentWithManuallyProvidedId));

        assertEquals(ID_PROVIDED_MANUALLY, exception.getMessage());
    }

    @Test
    void updateRecordInTableWIthCorrectIdTest() {
        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(thirdStudent);
        //Check that original Id is not changed
        secondStudent.setId(5);

        assertEquals(secondStudent, sqlDatabaseService.updateRecordInTable(secondStudent, 1));
        assertEquals(secondStudent, sqlDatabaseService.getById(Student.class, 1));
    }

    @Test
    void updateOxfordStudentTest() {
        try {
            sqlDatabaseService.createTable(OxfordStudent.class);
            OxfordStudent os = new OxfordStudent("N", 4.5, 20);
            OxfordStudent os2 = new OxfordStudent("M", 4.2, 21);

            sqlDatabaseService.addNewRecordToTable(os);
            assertEquals(os2, sqlDatabaseService.updateRecordInTable(os2, 1));
        } finally {
            sqlDatabaseService.deleteTable(OxfordStudent.class);
        }
    }

    @Test
    void updateRecordInTableWIthIncorrectIdTest() {
        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);

        IdDoesNotExistException exception = assertThrows(IdDoesNotExistException.class, () ->
                sqlDatabaseService.updateRecordInTable(secondStudent, 12));

        assertEquals(ENTITY_IS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void removeRecordFromTableTest() {
        List<Student> studentsBeforeDeletion = List.of(firstStudent, secondStudent);
        List<Student> studentsAfterDeletion = List.of(secondStudent);

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(secondStudent);

        assertEquals(studentsBeforeDeletion, sqlDatabaseService.getAllRecordsFromTable(Student.class));
        assertTrue(sqlDatabaseService.removeRecordFromTable(Student.class, 1));
        assertEquals(studentsAfterDeletion, sqlDatabaseService.getAllRecordsFromTable(Student.class));
    }

    @Test
    void removeAllRecordsFromTableTest() {
        List<Student> studentsAfterDeletion = List.of();

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(secondStudent);

        sqlDatabaseService.removeAllRecordsFromTable(Student.class);

        assertEquals(studentsAfterDeletion, sqlDatabaseService.getAllRecordsFromTable(Student.class));
    }

    @Test
    void removeAllRecordsResetIdCounterCheckTest() {
        try {
            List<Student> studentsAfterDeletion = List.of();

            sqlDatabaseService.createTable(Student.class);
            sqlDatabaseService.addNewRecordToTable(firstStudent);
            sqlDatabaseService.addNewRecordToTable(secondStudent);

            sqlDatabaseService.createTable(Course.class);
            sqlDatabaseService.addNewRecordToTable(new Course("Course1"));

            sqlDatabaseService.removeAllRecordsFromTable(Student.class);
            assertEquals(studentsAfterDeletion, sqlDatabaseService.getAllRecordsFromTable(Student.class));

            sqlDatabaseService.addNewRecordToTable(thirdStudent);
            assertEquals(thirdStudent, sqlDatabaseService.getById(Student.class, 1));

            sqlDatabaseService.addNewRecordToTable(new Course("Course2"));
            assertEquals(2, sqlDatabaseService.getById(Course.class, 2).getId());
        } finally {
            sqlDatabaseService.deleteTable(Course.class);
        }
    }

    @Test
    void getByIdTest() {
        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(secondStudent);

        Student receivedStudentById = sqlDatabaseService.getById(Student.class, 2);

        assertEquals(secondStudent, receivedStudentById);
        assertEquals(2, receivedStudentById.getId());
    }

    @Test
    void getByIdWhenIdDoesNotExistTest() {
        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);

        assertNull(sqlDatabaseService.getById(Student.class, 13));
    }

    @Test
    void getAllRecordsFromTableTest() {
        List<Student> students = List.of(firstStudent, secondStudent);

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(secondStudent);

        assertEquals(students, sqlDatabaseService.getAllRecordsFromTable(Student.class));
    }

    @Test
    void getAllRecordsFromTableWithValidLimitOffsetParametersTest() {
        List<Student> resultStudents = List.of(secondStudent, thirdStudent);

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(secondStudent);
        sqlDatabaseService.addNewRecordToTable(thirdStudent);
        sqlDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(resultStudents, sqlDatabaseService.getAllRecordsFromTable(Student.class, 2, 1));
    }

    @Test
    void getAllRecordsFromTableWithNegativeLimitOffsetParametersTest() {
        var exception = assertThrows(InvalidParameterValueException.class, () ->
                sqlDatabaseService.getAllRecordsFromTable(Student.class, -1, -1));

        assertEquals(INVALID_PARAMETER_VALUE.replace("{MAX_LIMIT_VALUE}", "100"),
                exception.getMessage());
    }

    @Test
    void getAllRecordsFromTableWithOverLimitParameterTest() {
        var exception = assertThrows(InvalidParameterValueException.class, () ->
                sqlDatabaseService.getAllRecordsFromTable(Student.class, 200, 1));

        assertEquals(INVALID_PARAMETER_VALUE.replace("{MAX_LIMIT_VALUE}", "100"),
                exception.getMessage());
    }
}
