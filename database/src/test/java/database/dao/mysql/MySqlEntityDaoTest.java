package database.dao.mysql;

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
import static database.dao.EntityDaoConstants.ENTITIES_LIST_NULL_OR_EMPTY;
import static database.dao.EntityDaoConstants.ENTITY_IS_NOT_FOUND;
import static database.dao.EntityDaoConstants.IDS_LIST_NULL_OR_EMPTY;
import static database.dao.EntityDaoConstants.ID_PROVIDED_MANUALLY;
import static database.dao.EntityDaoConstants.INVALID_PARAMETER_VALUE;
import static database.dao.mysql.MySqlEntityDao.TABLE_NOT_EXIST;
import static database.dao.mysql.MySqlEntityDao.UNABLE_CREATE_TABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The {@code MySqlEntityDaoTest} class contains unit tests for the {@code MySqlEntityDao} class.
 *
 * <p>This class tests the core functionality of the {@code EntityDao}, which is responsible for
 * managing SQL database operations such as creating tables, deleting tables, and adding new records to tables.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 * @see MySqlEntityDao
 */
public class MySqlEntityDaoTest {
    private final Settings settings = new Settings("Db_app_properties_files/application.properties");
    private final MySqlEntityDao mySqlEntityDao = new MySqlEntityDao(settings);
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
            mySqlEntityDao.deleteTable(Student.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void createTableTest() {
        assertTrue(mySqlEntityDao.createTable(Student.class));
    }

    @Test
    void createTableFailedTest() {
        mySqlEntityDao.createTable(Student.class);

        CreationDatabaseException exception = assertThrows(CreationDatabaseException.class, () ->
                mySqlEntityDao.createTable(Student.class));
        assertTrue(exception.getMessage().startsWith(UNABLE_CREATE_TABLE));
    }

    @Test
    void deleteTableTest() {
        mySqlEntityDao.createTable(Student.class);
        assertTrue(mySqlEntityDao.deleteTable(Student.class));
    }

    @Test
    void addNewRecordToTablePositiveTest() {
        mySqlEntityDao.createTable(Student.class);

        Student receivedFirstStudent = mySqlEntityDao.addNewRecordToTable(firstStudent);
        assertEquals(firstStudent, receivedFirstStudent);
        assertEquals(1, receivedFirstStudent.getId());

        Student receivedSecondStudent = mySqlEntityDao.addNewRecordToTable(secondStudent);
        assertEquals(secondStudent, receivedSecondStudent);
        assertEquals(2, receivedSecondStudent.getId());
    }

    @Test
    void addNewRecordWhenTableDoesNotExistTest() {
        TableDoesNotExistException exception = assertThrows(TableDoesNotExistException.class, () ->
                mySqlEntityDao.addNewRecordToTable(firstStudent));

        assertTrue(exception.getMessage().startsWith(TABLE_NOT_EXIST));
    }

    @Test
    void addNewRecordIdProvidedManuallyTest() {
        mySqlEntityDao.createTable(Student.class);

        Student studentWithManuallyProvidedId = new Student.Builder()
                .withFullName("FirstName1 LastName1")
                .withAverageScore(5.0)
                .build();

        studentWithManuallyProvidedId.setId(7);

        IdProvidedManuallyException exception = assertThrows(IdProvidedManuallyException.class, () ->
                mySqlEntityDao.addNewRecordToTable(studentWithManuallyProvidedId));

        assertEquals(ID_PROVIDED_MANUALLY, exception.getMessage());
    }

    @Test
    void addNewRecordsToTablePositiveTest() {
        List<Student> newStudents = List.of(firstStudent, secondStudent);

        mySqlEntityDao.createTable(Student.class);

        assertEquals(newStudents, mySqlEntityDao.addNewRecordsToTable(Student.class, newStudents));
        assertEquals(firstStudent, mySqlEntityDao.getById(Student.class, 1));
        assertEquals(secondStudent, mySqlEntityDao.getById(Student.class, 2));
    }

    @Test
    void addNewRecordsWhenTableDoesNotExistTest() {
        List<Student> newStudents = List.of(firstStudent, secondStudent);

        TableDoesNotExistException exception = assertThrows(TableDoesNotExistException.class, () ->
                mySqlEntityDao.addNewRecordsToTable(Student.class, newStudents));

        assertTrue(exception.getMessage().startsWith(TABLE_NOT_EXIST));
    }

    @Test
    void addNewRecordsIdProvidedManuallyTest() {
        mySqlEntityDao.createTable(Student.class);

        Student studentWithManuallyProvidedId = new Student.Builder()
                .withFullName("FirstName1 LastName1")
                .withAverageScore(5.0)
                .build();

        studentWithManuallyProvidedId.setId(7);
        List<Student> newStudents = List.of(firstStudent, studentWithManuallyProvidedId);

        IdProvidedManuallyException exception = assertThrows(IdProvidedManuallyException.class, () ->
                mySqlEntityDao.addNewRecordsToTable(Student.class, newStudents));

        assertEquals(ID_PROVIDED_MANUALLY, exception.getMessage());
    }

    @Test
    void addNewRecordsEntitiesListIsEmptyTest() {
        mySqlEntityDao.createTable(Student.class);

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                mySqlEntityDao.addNewRecordsToTable(Student.class, List.of()));

        assertEquals(ENTITIES_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void addNewRecordsEntitiesListIsNullTest() {
        mySqlEntityDao.createTable(Student.class);

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                mySqlEntityDao.addNewRecordsToTable(Student.class, null));

        assertEquals(ENTITIES_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void updateRecordInTableWIthCorrectIdTest() {
        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);
        mySqlEntityDao.addNewRecordToTable(thirdStudent);

        secondStudent.setId(5);

        assertEquals(secondStudent, mySqlEntityDao.updateRecordInTable(secondStudent, 1));
        assertEquals(secondStudent, mySqlEntityDao.getById(Student.class, 1));
    }

    @Test
    void updateOxfordStudentTest() {
        try {
            mySqlEntityDao.createTable(OxfordStudent.class);

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

            mySqlEntityDao.addNewRecordToTable(os);
            assertEquals(os2, mySqlEntityDao.updateRecordInTable(os2, 1));
        } finally {
            mySqlEntityDao.deleteTable(OxfordStudent.class);
        }
    }

    @Test
    void updateRecordInTableWIthIncorrectIdTest() {
        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);

        IdDoesNotExistException exception = assertThrows(IdDoesNotExistException.class, () ->
                mySqlEntityDao.updateRecordInTable(secondStudent, 12));

        assertEquals(ENTITY_IS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void removeRecordFromTableTest() {
        List<Student> studentsBeforeDeletion = List.of(firstStudent, secondStudent);
        List<Student> studentsAfterDeletion = List.of(secondStudent);

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);
        mySqlEntityDao.addNewRecordToTable(secondStudent);

        assertEquals(studentsBeforeDeletion, mySqlEntityDao.getAllRecordsFromTable(Student.class));
        assertTrue(mySqlEntityDao.removeRecordFromTable(Student.class, 1));
        assertEquals(studentsAfterDeletion, mySqlEntityDao.getAllRecordsFromTable(Student.class));
    }

    @Test
    void removeSpecificRecordsFromTablePositiveTest() {
        List<Student> studentsBeforeDeletion = List.of(firstStudent, secondStudent, thirdStudent);
        List<Student> studentsAfterDeletion = List.of(firstStudent);
        List<Integer> idsForDeletion = List.of(2, 3);

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordsToTable(Student.class, studentsBeforeDeletion);

        assertTrue(mySqlEntityDao.removeSpecificRecordsFromTable(Student.class, idsForDeletion));
        assertEquals(studentsAfterDeletion, mySqlEntityDao.getAllRecordsFromTable(Student.class));
    }

    @Test
    void removeSpecificRecordsIdsListIsEmptyTest() {
        List<Integer> idsForDeletion = List.of();

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                mySqlEntityDao.removeSpecificRecordsFromTable(Student.class, idsForDeletion));

        assertEquals(IDS_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void removeAllRecordsFromTableTest() {
        List<Student> studentsAfterDeletion = List.of();

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);
        mySqlEntityDao.addNewRecordToTable(secondStudent);

        mySqlEntityDao.removeAllRecordsFromTable(Student.class);

        assertEquals(studentsAfterDeletion, mySqlEntityDao.getAllRecordsFromTable(Student.class));
    }

    @Test
    void removeAllRecordsIdCounterCheckTest() {
        try {
            List<Student> studentsAfterDeletion = List.of();

            mySqlEntityDao.createTable(Student.class);
            mySqlEntityDao.addNewRecordToTable(firstStudent);
            mySqlEntityDao.addNewRecordToTable(secondStudent);

            mySqlEntityDao.createTable(Course.class);
            mySqlEntityDao.addNewRecordToTable(new Course.Builder()
                    .withName("Course1")
                    .build());

            mySqlEntityDao.removeAllRecordsFromTable(Student.class);
            assertEquals(studentsAfterDeletion, mySqlEntityDao.getAllRecordsFromTable(Student.class));

            mySqlEntityDao.addNewRecordToTable(thirdStudent);
            assertEquals(thirdStudent, mySqlEntityDao.getById(Student.class, 3));

            mySqlEntityDao.addNewRecordToTable(new Course.Builder()
                    .withName("Course2")
                    .build());

            assertEquals(2, mySqlEntityDao.getById(Course.class, 2).getId());
        } finally {
            mySqlEntityDao.deleteTable(Course.class);
        }
    }

    @Test
    void getByIdTest() {
        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);
        mySqlEntityDao.addNewRecordToTable(secondStudent);

        Student receivedStudentById = mySqlEntityDao.getById(Student.class, 2);

        assertEquals(secondStudent, receivedStudentById);
        assertEquals(2, receivedStudentById.getId());
    }

    @Test
    void getByIdWhenIdDoesNotExistTest() {
        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);

        assertNull(mySqlEntityDao.getById(Student.class, 13));
    }

    @Test
    void getAllRecordsFromTableTest() {
        List<Student> students = List.of(firstStudent, secondStudent);

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);
        mySqlEntityDao.addNewRecordToTable(secondStudent);

        assertEquals(students, mySqlEntityDao.getAllRecordsFromTable(Student.class));
    }

    @Test
    void getAllRecordsWithValidLimitOffsetParametersTest() {
        List<Student> resultStudents = List.of(secondStudent, thirdStudent);

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);
        mySqlEntityDao.addNewRecordToTable(secondStudent);
        mySqlEntityDao.addNewRecordToTable(thirdStudent);
        mySqlEntityDao.addNewRecordToTable(fourthStudent);

        assertEquals(resultStudents, mySqlEntityDao.getAllRecordsFromTable(Student.class, 2, 1));
    }

    @Test
    void getAllRecordsWithNegativeLimitOffsetParametersTest() {
        var exception = assertThrows(InvalidParameterValueException.class, () ->
                mySqlEntityDao.getAllRecordsFromTable(Student.class, -1, -1));

        assertEquals(INVALID_PARAMETER_VALUE.replace("{MAX_LIMIT_VALUE}", "100"),
                exception.getMessage());
    }

    @Test
    void getAllRecordsWithOverLimitParameterTest() {
        var exception = assertThrows(InvalidParameterValueException.class, () ->
                mySqlEntityDao.getAllRecordsFromTable(Student.class, 200, 1));

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

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);
        mySqlEntityDao.addNewRecordToTable(secondStudent);
        mySqlEntityDao.addNewRecordToTable(thirdStudent);
        mySqlEntityDao.addNewRecordToTable(fourthStudent);

        assertEquals(students, mySqlEntityDao.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersSingleFilterWithListOfValuesMatchTest() {
        List<Student> students = List.of(firstStudent, secondStudent, fourthStudent);

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("FirstName1 LastName1", "FirstName2 LastName2")
        );

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);
        mySqlEntityDao.addNewRecordToTable(secondStudent);
        mySqlEntityDao.addNewRecordToTable(thirdStudent);
        mySqlEntityDao.addNewRecordToTable(fourthStudent);

        assertEquals(students, mySqlEntityDao.getByFilters(Student.class, filters));
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

        mySqlEntityDao.createTable(OxfordStudent.class);
        mySqlEntityDao.addNewRecordToTable(os);

        assertEquals(students, mySqlEntityDao.getByFilters(OxfordStudent.class, filters));

        mySqlEntityDao.deleteTable(OxfordStudent.class);
    }

    @Test
    void getByFiltersOnlyOneFilterMatchesTest() {
        List<Student> students = List.of();

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("Harry Potter"),
                "averageScore", List.of("5.0")
        );

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);
        mySqlEntityDao.addNewRecordToTable(secondStudent);
        mySqlEntityDao.addNewRecordToTable(thirdStudent);
        mySqlEntityDao.addNewRecordToTable(fourthStudent);

        assertEquals(students, mySqlEntityDao.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersNoFilterMatchesTest() {
        List<Student> students = List.of();

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("FirstName1 LastName1"),
                "averageScore", List.of("3.0")
        );

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);
        mySqlEntityDao.addNewRecordToTable(secondStudent);
        mySqlEntityDao.addNewRecordToTable(thirdStudent);

        assertEquals(students, mySqlEntityDao.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersPropertyNameIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>() {{
            put(null, List.of("0"));
        }};

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                mySqlEntityDao.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersValueIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>() {{
            put("averageScore", null);
        }};

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                mySqlEntityDao.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersValueIsEmptyTest() {
        Map<String, List<String>> filters = Map.of("fullName", List.of());

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                mySqlEntityDao.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_EMPTY_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersIncorrectPropertyNameTest() {
        Map<String, List<String>> filters = Map.of("firstName", List.of("FirstName1 LastName1"));

        mySqlEntityDao.createTable(Student.class);
        mySqlEntityDao.addNewRecordToTable(firstStudent);

        IncorrectPropertyNameException exception = assertThrows(IncorrectPropertyNameException.class, () ->
                mySqlEntityDao.getByFilters(Student.class, filters));
        assertEquals(INCORRECT_FILTER_NAME_MESSAGE + ": firstName", exception.getMessage());
    }
}
