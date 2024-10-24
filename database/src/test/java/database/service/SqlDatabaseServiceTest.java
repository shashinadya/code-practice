package database.service;

import database.entity.Course;
import database.entity.OxfordStudent;
import database.entity.Student;
import database.exception.CreationDatabaseException;
import database.exception.TableDoesNotExistException;
import database.exception.EmptyValueException;
import database.exception.IdDoesNotExistException;
import database.exception.IdProvidedManuallyException;
import database.exception.IncorrectPropertyNameException;
import database.exception.InvalidParameterValueException;
import database.exception.NullOrEmptyListException;
import database.exception.NullPropertyNameOrValueException;
import database.helper.Settings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static database.helper.Validator.FILTER_CANNOT_BE_EMPTY_MESSAGE;
import static database.helper.Validator.FILTER_CANNOT_BE_NULL_MESSAGE;
import static database.helper.Validator.INCORRECT_FILTER_NAME_MESSAGE;
import static database.service.ServiceConstants.ENTITIES_LIST_NULL_OR_EMPTY;
import static database.service.ServiceConstants.ENTITY_IS_NOT_FOUND;
import static database.service.ServiceConstants.IDS_LIST_NULL_OR_EMPTY;
import static database.service.ServiceConstants.ID_PROVIDED_MANUALLY;
import static database.service.ServiceConstants.INVALID_PARAMETER_VALUE;
import static database.service.SqlDatabaseService.TABLE_NOT_EXIST;
import static database.service.SqlDatabaseService.UNABLE_CREATE_TABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The {@code SqlDatabaseServiceTest} class contains unit tests for the {@code SqlDatabaseService} class.
 *
 * <p>This class tests the core functionality of the {@code SqlDatabaseService}, which is responsible for
 * managing SQL database operations such as creating tables, deleting tables, and adding new records to tables.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 * @see SqlDatabaseService
 */
public class SqlDatabaseServiceTest {
    private final Settings settings = new Settings("Db_app_properties_files/application.properties");
    private final SqlDatabaseService sqlDatabaseService = new SqlDatabaseService(settings);
    private Student firstStudent;
    private Student secondStudent;
    private Student thirdStudent;
    private Student fourthStudent;

    @BeforeEach
    void setUp() {
        firstStudent = new Student.Builder()
                .withFullName("FirstName1 LastName1")
                .withAverageScore(5.0)
                .build();
        secondStudent = new Student.Builder()
                .withFullName("FirstName2 LastName2")
                .withAverageScore(4.5)
                .build();
        thirdStudent = new Student.Builder()
                .withFullName("FirstName3 LastName3")
                .withAverageScore(5.0)
                .build();
        fourthStudent = new Student.Builder()
                .withFullName("FirstName1 LastName1")
                .withAverageScore(5.0)
                .build();
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
    void createTableTest() {
        assertTrue(sqlDatabaseService.createTable(Student.class));
    }

    @Test
    void createTableFailedTest() {
        sqlDatabaseService.createTable(Student.class);

        CreationDatabaseException exception = assertThrows(CreationDatabaseException.class, () ->
                sqlDatabaseService.createTable(Student.class));
        assertTrue(exception.getMessage().startsWith(UNABLE_CREATE_TABLE));
    }

    @Test
    void deleteTableTest() {
        sqlDatabaseService.createTable(Student.class);
        assertTrue(sqlDatabaseService.deleteTable(Student.class));
    }

    @Test
    void addNewRecordToTablePositiveTest() {
        sqlDatabaseService.createTable(Student.class);

        Student receivedFirstStudent = sqlDatabaseService.addNewRecordToTable(firstStudent);
        assertEquals(firstStudent, receivedFirstStudent);
        assertEquals(1, receivedFirstStudent.getId());

        Student receivedSecondStudent = sqlDatabaseService.addNewRecordToTable(secondStudent);
        assertEquals(secondStudent, receivedSecondStudent);
        assertEquals(2, receivedSecondStudent.getId());
    }

    @Test
    void addNewRecordWhenTableDoesNotExistTest() {
        TableDoesNotExistException exception = assertThrows(TableDoesNotExistException.class, () ->
                sqlDatabaseService.addNewRecordToTable(firstStudent));

        assertTrue(exception.getMessage().startsWith(TABLE_NOT_EXIST));
    }

    @Test
    void addNewRecordIdProvidedManuallyTest() {
        sqlDatabaseService.createTable(Student.class);

        Student studentWithManuallyProvidedId = new Student.Builder()
                .withFullName("FirstName1 LastName1")
                .withAverageScore(5.0)
                .build();

        studentWithManuallyProvidedId.setId(7);

        IdProvidedManuallyException exception = assertThrows(IdProvidedManuallyException.class, () ->
                sqlDatabaseService.addNewRecordToTable(studentWithManuallyProvidedId));

        assertEquals(ID_PROVIDED_MANUALLY, exception.getMessage());
    }

    @Test
    void addNewRecordsToTablePositiveTest() {
        List<Student> newStudents = List.of(firstStudent, secondStudent);

        sqlDatabaseService.createTable(Student.class);

        assertEquals(newStudents, sqlDatabaseService.addNewRecordsToTable(Student.class, newStudents));
        assertEquals(firstStudent, sqlDatabaseService.getById(Student.class, 1));
        assertEquals(secondStudent, sqlDatabaseService.getById(Student.class, 2));
    }

    @Test
    void addNewRecordsWhenTableDoesNotExistTest() {
        List<Student> newStudents = List.of(firstStudent, secondStudent);

        TableDoesNotExistException exception = assertThrows(TableDoesNotExistException.class, () ->
                sqlDatabaseService.addNewRecordsToTable(Student.class, newStudents));

        assertTrue(exception.getMessage().startsWith(TABLE_NOT_EXIST));
    }

    @Test
    void addNewRecordsIdProvidedManuallyTest() {
        sqlDatabaseService.createTable(Student.class);

        Student studentWithManuallyProvidedId = new Student.Builder()
                .withFullName("FirstName1 LastName1")
                .withAverageScore(5.0)
                .build();

        studentWithManuallyProvidedId.setId(7);
        List<Student> newStudents = List.of(firstStudent, studentWithManuallyProvidedId);

        IdProvidedManuallyException exception = assertThrows(IdProvidedManuallyException.class, () ->
                sqlDatabaseService.addNewRecordsToTable(Student.class, newStudents));

        assertEquals(ID_PROVIDED_MANUALLY, exception.getMessage());
    }

    @Test
    void addNewRecordsEntitiesListIsEmptyTest() {
        sqlDatabaseService.createTable(Student.class);

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                sqlDatabaseService.addNewRecordsToTable(Student.class, List.of()));

        assertEquals(ENTITIES_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void addNewRecordsEntitiesListIsNullTest() {
        sqlDatabaseService.createTable(Student.class);

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                sqlDatabaseService.addNewRecordsToTable(Student.class, null));

        assertEquals(ENTITIES_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void updateRecordInTableWIthCorrectIdTest() {
        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(thirdStudent);

        secondStudent.setId(5);

        assertEquals(secondStudent, sqlDatabaseService.updateRecordInTable(secondStudent, 1));
        assertEquals(secondStudent, sqlDatabaseService.getById(Student.class, 1));
    }

    @Test
    void updateOxfordStudentTest() {
        try {
            sqlDatabaseService.createTable(OxfordStudent.class);

            OxfordStudent os = new OxfordStudent.Builder()
                    .withFullName("N")
                    .withAverageScore(4.5)
                    .withAge(20)
                    .build();
            OxfordStudent os2 = new OxfordStudent.Builder()
                    .withFullName("M")
                    .withAverageScore(4.2)
                    .withAge(21)
                    .build();

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
    void removeSpecificRecordsFromTablePositiveTest() {
        List<Student> studentsBeforeDeletion = List.of(firstStudent, secondStudent, thirdStudent);
        List<Student> studentsAfterDeletion = List.of(firstStudent);
        List<Integer> idsForDeletion = List.of(2, 3);

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordsToTable(Student.class, studentsBeforeDeletion);

        assertTrue(sqlDatabaseService.removeSpecificRecordsFromTable(Student.class, idsForDeletion));
        assertEquals(studentsAfterDeletion, sqlDatabaseService.getAllRecordsFromTable(Student.class));
    }

    @Test
    void removeSpecificRecordsIdsListIsEmptyTest() {
        List<Integer> idsForDeletion = List.of();

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                sqlDatabaseService.removeSpecificRecordsFromTable(Student.class, idsForDeletion));

        assertEquals(IDS_LIST_NULL_OR_EMPTY, exception.getMessage());
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
    void removeAllRecordsIdCounterCheckTest() {
        try {
            List<Student> studentsAfterDeletion = List.of();

            sqlDatabaseService.createTable(Student.class);
            sqlDatabaseService.addNewRecordToTable(firstStudent);
            sqlDatabaseService.addNewRecordToTable(secondStudent);

            sqlDatabaseService.createTable(Course.class);
            sqlDatabaseService.addNewRecordToTable(new Course.Builder()
                    .withName("Course1")
                    .build());

            sqlDatabaseService.removeAllRecordsFromTable(Student.class);
            assertEquals(studentsAfterDeletion, sqlDatabaseService.getAllRecordsFromTable(Student.class));

            sqlDatabaseService.addNewRecordToTable(thirdStudent);
            assertEquals(thirdStudent, sqlDatabaseService.getById(Student.class, 3));

            sqlDatabaseService.addNewRecordToTable(new Course.Builder()
                    .withName("Course2")
                    .build());

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
    void getAllRecordsWithValidLimitOffsetParametersTest() {
        List<Student> resultStudents = List.of(secondStudent, thirdStudent);

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(secondStudent);
        sqlDatabaseService.addNewRecordToTable(thirdStudent);
        sqlDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(resultStudents, sqlDatabaseService.getAllRecordsFromTable(Student.class, 2, 1));
    }

    @Test
    void getAllRecordsWithNegativeLimitOffsetParametersTest() {
        var exception = assertThrows(InvalidParameterValueException.class, () ->
                sqlDatabaseService.getAllRecordsFromTable(Student.class, -1, -1));

        assertEquals(INVALID_PARAMETER_VALUE.replace("{MAX_LIMIT_VALUE}", "100"),
                exception.getMessage());
    }

    @Test
    void getAllRecordsWithOverLimitParameterTest() {
        var exception = assertThrows(InvalidParameterValueException.class, () ->
                sqlDatabaseService.getAllRecordsFromTable(Student.class, 200, 1));

        assertEquals(INVALID_PARAMETER_VALUE.replace("{MAX_LIMIT_VALUE}", "100"),
                exception.getMessage());
    }

    @Test
    void getByFiltersAllFiltersMatchTest() {
        List<Student> students = List.of(firstStudent, fourthStudent);

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("FirstName1 LastName1"),
                "averageScore", List.of("5.0")
        );

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(secondStudent);
        sqlDatabaseService.addNewRecordToTable(thirdStudent);
        sqlDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, sqlDatabaseService.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersSingleFilterWithListOfValuesMatchTest() {
        List<Student> students = List.of(firstStudent, secondStudent, fourthStudent);

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("FirstName1 LastName1", "FirstName2 LastName2")
        );

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(secondStudent);
        sqlDatabaseService.addNewRecordToTable(thirdStudent);
        sqlDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, sqlDatabaseService.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersOxfordStudentTest() {
        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("N"),
                "averageScore", List.of("4.5"),
                "age", List.of("20")
        );
        OxfordStudent os = new OxfordStudent.Builder()
                .withFullName("N")
                .withAverageScore(4.5)
                .withAge(20)
                .build();

        List<OxfordStudent> students = List.of(os);

        sqlDatabaseService.createTable(OxfordStudent.class);
        sqlDatabaseService.addNewRecordToTable(os);

        assertEquals(students, sqlDatabaseService.getByFilters(OxfordStudent.class, filters));

        sqlDatabaseService.deleteTable(OxfordStudent.class);
    }

    @Test
    void getByFiltersOnlyOneFilterMatchesTest() {
        List<Student> students = List.of();

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("Harry Potter"),
                "averageScore", List.of("5.0")
        );

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(secondStudent);
        sqlDatabaseService.addNewRecordToTable(thirdStudent);
        sqlDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, sqlDatabaseService.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersNoFilterMatchesTest() {
        List<Student> students = List.of();

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("FirstName1 LastName1"),
                "averageScore", List.of("3.0")
        );

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);
        sqlDatabaseService.addNewRecordToTable(secondStudent);
        sqlDatabaseService.addNewRecordToTable(thirdStudent);

        assertEquals(students, sqlDatabaseService.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersPropertyNameIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>() {{
            put(null, List.of("0"));
        }};

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                sqlDatabaseService.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersValueIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>() {{
            put("averageScore", null);
        }};

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                sqlDatabaseService.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersValueIsEmptyTest() {
        Map<String, List<String>> filters = Map.of("fullName", List.of());

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                sqlDatabaseService.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_EMPTY_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersIncorrectPropertyNameTest() {
        Map<String, List<String>> filters = Map.of("firstName", List.of("FirstName1 LastName1"));

        sqlDatabaseService.createTable(Student.class);
        sqlDatabaseService.addNewRecordToTable(firstStudent);

        IncorrectPropertyNameException exception = assertThrows(IncorrectPropertyNameException.class, () ->
                sqlDatabaseService.getByFilters(Student.class, filters));
        assertEquals(INCORRECT_FILTER_NAME_MESSAGE + ": firstName", exception.getMessage());
    }
}
