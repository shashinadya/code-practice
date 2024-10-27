package database.dao.file;

import database.entity.Course;
import database.entity.OxfordStudent;
import database.exception.TableDoesNotExistException;
import database.exception.EmptyValueException;
import database.exception.IdDoesNotExistException;
import database.entity.Student;
import database.exception.IdProvidedManuallyException;
import database.exception.IncorrectPropertyNameException;
import database.exception.InvalidParameterValueException;
import database.exception.NullOrEmptyListException;
import database.exception.NullPropertyNameOrValueException;
import database.helper.Settings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static database.helper.Validator.FILTER_CANNOT_BE_EMPTY_MESSAGE;
import static database.helper.Validator.FILTER_CANNOT_BE_NULL_MESSAGE;
import static database.helper.Validator.INCORRECT_FILTER_NAME_MESSAGE;
import static database.dao.file.FileBasedEntityDao.DB_FILE_NOT_EXIST;
import static database.dao.file.FileBasedEntityDao.EMPTY_BRACKETS_TO_JSON;
import static database.dao.EntityDaoConstants.ENTITIES_LIST_NULL_OR_EMPTY;
import static database.dao.EntityDaoConstants.ENTITY_IS_NOT_FOUND;
import static database.dao.EntityDaoConstants.IDS_LIST_NULL_OR_EMPTY;
import static database.dao.EntityDaoConstants.ID_PROVIDED_MANUALLY;
import static database.dao.EntityDaoConstants.INVALID_PARAMETER_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The {@code FileBasedEntityDaoTest} class contains unit tests for the {@code FileBasedEntityDao} class.
 *
 * <p>This class tests the core functionality of the {@code EntityDao}, which is responsible for
 * managing database operations such as creating tables, deleting tables, and adding new records using JSON files.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 * @see FileBasedEntityDao
 */
class FileBasedEntityDaoTest {
    private final Settings settings = new Settings("Db_app_properties_files/application.properties");
    private final FileBasedEntityDao fileBasedEntityDao = new FileBasedEntityDao(settings);
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
            fileBasedEntityDao.deleteTable(Student.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void createTableTest() {
        assertTrue(fileBasedEntityDao.createTable(Student.class));
    }

    @Test
    void deleteTableWhenTableExistsTest() {
        fileBasedEntityDao.createTable(Student.class);
        assertTrue(fileBasedEntityDao.deleteTable(Student.class));
    }

    @Test
    void deleteTableWhenTableDoesNotExistTest() {
        TableDoesNotExistException exception = assertThrows(TableDoesNotExistException.class, () ->
                fileBasedEntityDao.deleteTable(Student.class));

        assertEquals(DB_FILE_NOT_EXIST, exception.getMessage());
    }

    @Test
    void addNewRecordToTablePositiveTest() {
        fileBasedEntityDao.createTable(Student.class);

        Student receivedFirstStudent = fileBasedEntityDao.addNewRecordToTable(firstStudent);
        assertEquals(firstStudent, receivedFirstStudent);
        assertEquals(0, receivedFirstStudent.getId());

        Student receivedSecondStudent = fileBasedEntityDao.addNewRecordToTable(secondStudent);
        assertEquals(secondStudent, receivedSecondStudent);
        assertEquals(1, receivedSecondStudent.getId());
    }

    @Test
    void addNewRecordWhenTableDoesNotExistTest() {
        TableDoesNotExistException exception = assertThrows(TableDoesNotExistException.class, () ->
                fileBasedEntityDao.addNewRecordToTable(firstStudent));

        assertEquals(DB_FILE_NOT_EXIST, exception.getMessage());
    }

    @Test
    void addNewRecordIdProvidedManuallyTest() {
        fileBasedEntityDao.createTable(Student.class);

        Student studentWithManuallyProvidedId = new Student.Builder()
                .withFullName("FirstName1 LastName1")
                .withAverageScore(5.0)
                .build();

        studentWithManuallyProvidedId.setId(7);

        IdProvidedManuallyException exception = assertThrows(IdProvidedManuallyException.class, () ->
                fileBasedEntityDao.addNewRecordToTable(studentWithManuallyProvidedId));

        assertEquals(ID_PROVIDED_MANUALLY, exception.getMessage());
    }

    @Test
    void addNewRecordsToTablePositiveTest() {
        List<Student> students = List.of(firstStudent, secondStudent);
        fileBasedEntityDao.createTable(Student.class);

        assertEquals(students, fileBasedEntityDao.addNewRecordsToTable(Student.class, students));
        assertEquals(firstStudent, fileBasedEntityDao.getById(Student.class, 0));
        assertEquals(secondStudent, fileBasedEntityDao.getById(Student.class, 1));
    }

    @Test
    void addNewRecordsWhenTableDoesNotExistTest() {
        List<Student> students = List.of(firstStudent, secondStudent);

        TableDoesNotExistException exception = assertThrows(TableDoesNotExistException.class, () ->
                fileBasedEntityDao.addNewRecordsToTable(Student.class, students));

        assertEquals(DB_FILE_NOT_EXIST, exception.getMessage());
    }

    @Test
    void addNewRecordsIdProvidedManuallyTest() {
        fileBasedEntityDao.createTable(Student.class);

        Student studentWithManuallyProvidedId = new Student.Builder()
                .withFullName("FirstName1 LastName1")
                .withAverageScore(5.0)
                .build();

        studentWithManuallyProvidedId.setId(7);
        List<Student> students = List.of(firstStudent, studentWithManuallyProvidedId);

        IdProvidedManuallyException exception = assertThrows(IdProvidedManuallyException.class, () ->
                fileBasedEntityDao.addNewRecordsToTable(Student.class, students));

        assertEquals(ID_PROVIDED_MANUALLY, exception.getMessage());
    }

    @Test
    void addNewRecordsEntitiesListIsEmptyTest() {
        fileBasedEntityDao.createTable(Student.class);

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                fileBasedEntityDao.addNewRecordsToTable(Student.class, List.of()));

        assertEquals(ENTITIES_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void addNewRecordsEntitiesListIsNullTest() {
        fileBasedEntityDao.createTable(Student.class);

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                fileBasedEntityDao.addNewRecordsToTable(Student.class, null));

        assertEquals(ENTITIES_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void updateRecordInTableWIthCorrectIdTest() {
        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);
        fileBasedEntityDao.addNewRecordToTable(thirdStudent);

        secondStudent.setId(5);

        assertEquals(secondStudent, fileBasedEntityDao.updateRecordInTable(secondStudent, 1));
        assertEquals(secondStudent, fileBasedEntityDao.getById(Student.class, 1));
    }

    @Test
    void updateOxfordStudentTest() {
        fileBasedEntityDao.createTable(OxfordStudent.class);

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

        fileBasedEntityDao.addNewRecordToTable(os);
        assertEquals(os2, fileBasedEntityDao.updateRecordInTable(os2, 0));

        fileBasedEntityDao.deleteTable(OxfordStudent.class);
    }

    @Test
    void updateRecordInTableWIthIncorrectIdTest() {
        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);

        IdDoesNotExistException exception = assertThrows(IdDoesNotExistException.class, () ->
                fileBasedEntityDao.updateRecordInTable(secondStudent, 12));

        assertEquals(ENTITY_IS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void removeRecordFromTableTest() {
        List<Student> studentsBeforeDeletion = List.of(firstStudent, secondStudent);
        List<Student> studentsAfterDeletion = List.of(secondStudent);

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);
        fileBasedEntityDao.addNewRecordToTable(secondStudent);

        assertEquals(studentsBeforeDeletion, fileBasedEntityDao.getAllRecordsFromTable(Student.class));
        assertTrue(fileBasedEntityDao.removeRecordFromTable(Student.class, 0));
        assertEquals(studentsAfterDeletion, fileBasedEntityDao.getAllRecordsFromTable(Student.class));
    }

    @Test
    void removeSpecificRecordsFromTablePositiveTest() {
        List<Student> studentsBeforeDeletion = List.of(firstStudent, secondStudent, thirdStudent);
        List<Student> studentsAfterDeletion = List.of(firstStudent);
        List<Integer> idsForDeletion = List.of(1, 2);

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordsToTable(Student.class, studentsBeforeDeletion);

        assertTrue(fileBasedEntityDao.removeSpecificRecordsFromTable(Student.class, idsForDeletion));
        assertEquals(studentsAfterDeletion, fileBasedEntityDao.getAllRecordsFromTable(Student.class));
    }

    @Test
    void removeSpecificRecordsIdsListIsEmptyTest() {
        List<Integer> idsForDeletion = List.of();

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                fileBasedEntityDao.removeSpecificRecordsFromTable(Student.class, idsForDeletion));

        assertEquals(IDS_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void removeAllRecordsFromTableTest() {
        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);
        fileBasedEntityDao.addNewRecordToTable(secondStudent);

        fileBasedEntityDao.removeAllRecordsFromTable(Student.class);
        Path databasePath = Path.of(fileBasedEntityDao.getDatabasePath(Student.class));
        assertEquals(EMPTY_BRACKETS_TO_JSON, fileBasedEntityDao.readDatabaseFile(databasePath));
    }

    @Test
    void removeAllRecordsIdCounterCheckTest() {
        try {
            fileBasedEntityDao.createTable(Student.class);
            fileBasedEntityDao.addNewRecordToTable(firstStudent);
            fileBasedEntityDao.addNewRecordToTable(secondStudent);

            fileBasedEntityDao.createTable(Course.class);
            fileBasedEntityDao.addNewRecordToTable(new Course.Builder()
                    .withName("Course1")
                    .build());

            fileBasedEntityDao.removeAllRecordsFromTable(Student.class);
            Path databasePath = Path.of(fileBasedEntityDao.getDatabasePath(Student.class));
            assertEquals(EMPTY_BRACKETS_TO_JSON, fileBasedEntityDao.readDatabaseFile(databasePath));

            fileBasedEntityDao.addNewRecordToTable(thirdStudent);
            assertEquals(thirdStudent, fileBasedEntityDao.getById(Student.class, 2));

            fileBasedEntityDao.addNewRecordToTable(new Course.Builder()
                    .withName("Course2")
                    .build());

            assertEquals(1, fileBasedEntityDao.getById(Course.class, 1).getId());
        } finally {
            fileBasedEntityDao.deleteTable(Course.class);
        }
    }

    @Test
    void getByIdTest() {
        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);
        fileBasedEntityDao.addNewRecordToTable(secondStudent);

        Student receivedStudentById = fileBasedEntityDao.getById(Student.class, 1);

        assertEquals(secondStudent, receivedStudentById);
        assertEquals(1, receivedStudentById.getId());
    }

    @Test
    void getByIdWhenIdDoesNotExistTest() {
        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);

        assertNull(fileBasedEntityDao.getById(Student.class, 13));
    }

    @Test
    void getAllRecordsFromTableTest() {
        List<Student> students = List.of(firstStudent, secondStudent);

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);
        fileBasedEntityDao.addNewRecordToTable(secondStudent);

        assertEquals(students, fileBasedEntityDao.getAllRecordsFromTable(Student.class));
    }

    @Test
    void getAllRecordsWithValidLimitOffsetParametersTest() {
        List<Student> resultStudents = List.of(secondStudent, thirdStudent);

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);
        fileBasedEntityDao.addNewRecordToTable(secondStudent);
        fileBasedEntityDao.addNewRecordToTable(thirdStudent);
        fileBasedEntityDao.addNewRecordToTable(fourthStudent);

        assertEquals(resultStudents, fileBasedEntityDao.getAllRecordsFromTable(Student.class, 2, 1));
    }

    @Test
    void getAllRecordsWithNegativeLimitOffsetParametersTest() {
        var exception = assertThrows(InvalidParameterValueException.class, () ->
                fileBasedEntityDao.getAllRecordsFromTable(Student.class, -1, -1));

        assertEquals(INVALID_PARAMETER_VALUE.replace("{MAX_LIMIT_VALUE}", "100"),
                exception.getMessage());
    }

    @Test
    void getAllRecordsWithOverLimitParameterTest() {
        var exception = assertThrows(InvalidParameterValueException.class, () ->
                fileBasedEntityDao.getAllRecordsFromTable(Student.class, 200, 1));

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

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);
        fileBasedEntityDao.addNewRecordToTable(secondStudent);
        fileBasedEntityDao.addNewRecordToTable(thirdStudent);
        fileBasedEntityDao.addNewRecordToTable(fourthStudent);

        assertEquals(students, fileBasedEntityDao.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersSingleFilterWithListOfValuesMatchTest() {
        List<Student> students = List.of(firstStudent, secondStudent, fourthStudent);

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("FirstName1 LastName1", "FirstName2 LastName2")
        );

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);
        fileBasedEntityDao.addNewRecordToTable(secondStudent);
        fileBasedEntityDao.addNewRecordToTable(thirdStudent);
        fileBasedEntityDao.addNewRecordToTable(fourthStudent);

        assertEquals(students, fileBasedEntityDao.getByFilters(Student.class, filters));
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

        fileBasedEntityDao.createTable(OxfordStudent.class);
        fileBasedEntityDao.addNewRecordToTable(os);

        assertEquals(students, fileBasedEntityDao.getByFilters(OxfordStudent.class, filters));

        fileBasedEntityDao.deleteTable(OxfordStudent.class);
    }

    @Test
    void getByFiltersOnlyOneFilterMatchesTest() {
        List<Student> students = List.of();

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("Harry Potter"),
                "averageScore", List.of("5.0")
        );

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);
        fileBasedEntityDao.addNewRecordToTable(secondStudent);
        fileBasedEntityDao.addNewRecordToTable(thirdStudent);
        fileBasedEntityDao.addNewRecordToTable(fourthStudent);

        assertEquals(students, fileBasedEntityDao.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersNoFilterMatchesTest() {
        List<Student> students = List.of();

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("FirstName1 LastName1"),
                "averageScore", List.of("3.0")
        );

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);
        fileBasedEntityDao.addNewRecordToTable(secondStudent);
        fileBasedEntityDao.addNewRecordToTable(thirdStudent);

        assertEquals(students, fileBasedEntityDao.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersPropertyNameIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>() {{
            put(null, List.of("0"));
        }};

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                fileBasedEntityDao.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersValueIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>() {{
            put("averageScore", null);
        }};

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                fileBasedEntityDao.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersValueIsEmptyTest() {
        Map<String, List<String>> filters = Map.of("fullName", List.of());

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                fileBasedEntityDao.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_EMPTY_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersIncorrectPropertyNameTest() {
        Map<String, List<String>> filters = Map.of("firstName", List.of("FirstName1 LastName1"));

        fileBasedEntityDao.createTable(Student.class);
        fileBasedEntityDao.addNewRecordToTable(firstStudent);

        IncorrectPropertyNameException exception = assertThrows(IncorrectPropertyNameException.class, () ->
                fileBasedEntityDao.getByFilters(Student.class, filters));
        assertEquals(INCORRECT_FILTER_NAME_MESSAGE + ": firstName", exception.getMessage());
    }
}
