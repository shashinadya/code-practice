package database.service;

import database.entity.Course;
import database.entity.OxfordStudent;
import database.exception.DatabaseDoesNotExistException;
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
import static database.service.JsonDatabaseService.DB_FILE_NOT_EXIST;
import static database.service.JsonDatabaseService.EMPTY_BRACKETS_TO_JSON;
import static database.service.ServiceConstants.ENTITIES_LIST_NULL_OR_EMPTY;
import static database.service.ServiceConstants.ENTITY_IS_NOT_FOUND;
import static database.service.ServiceConstants.IDS_LIST_NULL_OR_EMPTY;
import static database.service.ServiceConstants.ID_PROVIDED_MANUALLY;
import static database.service.ServiceConstants.INVALID_PARAMETER_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonDatabaseServiceTest {
    private final Settings settings = new Settings("Db_app_properties_files/application.properties");
    private final JsonDatabaseService jsonDatabaseService = new JsonDatabaseService(settings);
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
            jsonDatabaseService.deleteTable(Student.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void createTableTest() {
        assertTrue(jsonDatabaseService.createTable(Student.class));
    }

    @Test
    void deleteTableWhenTableExistsTest() {
        jsonDatabaseService.createTable(Student.class);
        assertTrue(jsonDatabaseService.deleteTable(Student.class));
    }

    @Test
    void deleteTableWhenTableDoesNotExistTest() {
        DatabaseDoesNotExistException exception = assertThrows(DatabaseDoesNotExistException.class, () ->
                jsonDatabaseService.deleteTable(Student.class));

        assertEquals(DB_FILE_NOT_EXIST, exception.getMessage());
    }

    @Test
    void addNewRecordToTablePositiveTest() {
        jsonDatabaseService.createTable(Student.class);

        Student receivedFirstStudent = jsonDatabaseService.addNewRecordToTable(firstStudent);
        assertEquals(firstStudent, receivedFirstStudent);
        assertEquals(0, receivedFirstStudent.getId());

        Student receivedSecondStudent = jsonDatabaseService.addNewRecordToTable(secondStudent);
        assertEquals(secondStudent, receivedSecondStudent);
        assertEquals(1, receivedSecondStudent.getId());
    }

    @Test
    void addNewRecordWhenDatabaseDoesNotExistTest() {
        DatabaseDoesNotExistException exception = assertThrows(DatabaseDoesNotExistException.class, () ->
                jsonDatabaseService.addNewRecordToTable(firstStudent));

        assertEquals(DB_FILE_NOT_EXIST, exception.getMessage());
    }

    @Test
    void addNewRecordIdProvidedManuallyTest() {
        jsonDatabaseService.createTable(Student.class);

        Student studentWithManuallyProvidedId = new Student.Builder()
                .withFullName("FirstName1 LastName1")
                .withAverageScore(5.0)
                .build();

        studentWithManuallyProvidedId.setId(7);

        IdProvidedManuallyException exception = assertThrows(IdProvidedManuallyException.class, () ->
                jsonDatabaseService.addNewRecordToTable(studentWithManuallyProvidedId));

        assertEquals(ID_PROVIDED_MANUALLY, exception.getMessage());
    }

    @Test
    void addNewRecordsToTablePositiveTest() {
        List<Student> students = List.of(firstStudent, secondStudent);
        jsonDatabaseService.createTable(Student.class);

        assertEquals(students, jsonDatabaseService.addNewRecordsToTable(Student.class, students));
        assertEquals(firstStudent, jsonDatabaseService.getById(Student.class, 0));
        assertEquals(secondStudent, jsonDatabaseService.getById(Student.class, 1));
    }

    @Test
    void addNewRecordsWhenDatabaseDoesNotExistTest() {
        List<Student> students = List.of(firstStudent, secondStudent);

        DatabaseDoesNotExistException exception = assertThrows(DatabaseDoesNotExistException.class, () ->
                jsonDatabaseService.addNewRecordsToTable(Student.class, students));

        assertEquals(DB_FILE_NOT_EXIST, exception.getMessage());
    }

    @Test
    void addNewRecordsIdProvidedManuallyTest() {
        jsonDatabaseService.createTable(Student.class);

        Student studentWithManuallyProvidedId = new Student.Builder()
                .withFullName("FirstName1 LastName1")
                .withAverageScore(5.0)
                .build();

        studentWithManuallyProvidedId.setId(7);
        List<Student> students = List.of(firstStudent, studentWithManuallyProvidedId);

        IdProvidedManuallyException exception = assertThrows(IdProvidedManuallyException.class, () ->
                jsonDatabaseService.addNewRecordsToTable(Student.class, students));

        assertEquals(ID_PROVIDED_MANUALLY, exception.getMessage());
    }

    @Test
    void addNewRecordsEntitiesListIsEmptyTest() {
        jsonDatabaseService.createTable(Student.class);

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                jsonDatabaseService.addNewRecordsToTable(Student.class, List.of()));

        assertEquals(ENTITIES_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void addNewRecordsEntitiesListIsNullTest() {
        jsonDatabaseService.createTable(Student.class);

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                jsonDatabaseService.addNewRecordsToTable(Student.class, null));

        assertEquals(ENTITIES_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void updateRecordInTableWIthCorrectIdTest() {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);

        secondStudent.setId(5);

        assertEquals(secondStudent, jsonDatabaseService.updateRecordInTable(secondStudent, 1));
        assertEquals(secondStudent, jsonDatabaseService.getById(Student.class, 1));
    }

    @Test
    void updateOxfordStudentTest() {
        jsonDatabaseService.createTable(OxfordStudent.class);

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

        jsonDatabaseService.addNewRecordToTable(os);
        assertEquals(os2, jsonDatabaseService.updateRecordInTable(os2, 0));

        jsonDatabaseService.deleteTable(OxfordStudent.class);
    }

    @Test
    void updateRecordInTableWIthIncorrectIdTest() {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IdDoesNotExistException exception = assertThrows(IdDoesNotExistException.class, () ->
                jsonDatabaseService.updateRecordInTable(secondStudent, 12));

        assertEquals(ENTITY_IS_NOT_FOUND, exception.getMessage());
    }

    @Test
    void removeRecordFromTableTest() {
        List<Student> studentsBeforeDeletion = List.of(firstStudent, secondStudent);
        List<Student> studentsAfterDeletion = List.of(secondStudent);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        assertEquals(studentsBeforeDeletion, jsonDatabaseService.getAllRecordsFromTable(Student.class));
        assertTrue(jsonDatabaseService.removeRecordFromTable(Student.class, 0));
        assertEquals(studentsAfterDeletion, jsonDatabaseService.getAllRecordsFromTable(Student.class));
    }

    @Test
    void removeSpecificRecordsFromTablePositiveTest() {
        List<Student> studentsBeforeDeletion = List.of(firstStudent, secondStudent, thirdStudent);
        List<Student> studentsAfterDeletion = List.of(firstStudent);
        List<Integer> idsForDeletion = List.of(1, 2);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordsToTable(Student.class, studentsBeforeDeletion);

        assertTrue(jsonDatabaseService.removeSpecificRecords(Student.class, idsForDeletion));
        assertEquals(studentsAfterDeletion, jsonDatabaseService.getAllRecordsFromTable(Student.class));
    }

    @Test
    void removeSpecificRecordsIdsListIsEmptyTest() {
        List<Integer> idsForDeletion = List.of();

        NullOrEmptyListException exception = assertThrows(NullOrEmptyListException.class, () ->
                jsonDatabaseService.removeSpecificRecords(Student.class, idsForDeletion));

        assertEquals(IDS_LIST_NULL_OR_EMPTY, exception.getMessage());
    }

    @Test
    void removeAllRecordsFromTableTest() {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        jsonDatabaseService.removeAllRecordsFromTable(Student.class);
        Path databasePath = Path.of(jsonDatabaseService.getDatabasePath(Student.class));
        assertEquals(EMPTY_BRACKETS_TO_JSON, jsonDatabaseService.readDatabaseFile(databasePath));
    }

    @Test
    void removeAllRecordsIdCounterCheckTest() {
        try {
            jsonDatabaseService.createTable(Student.class);
            jsonDatabaseService.addNewRecordToTable(firstStudent);
            jsonDatabaseService.addNewRecordToTable(secondStudent);

            jsonDatabaseService.createTable(Course.class);
            jsonDatabaseService.addNewRecordToTable(new Course.Builder()
                    .withName("Course1")
                    .build());

            jsonDatabaseService.removeAllRecordsFromTable(Student.class);
            Path databasePath = Path.of(jsonDatabaseService.getDatabasePath(Student.class));
            assertEquals(EMPTY_BRACKETS_TO_JSON, jsonDatabaseService.readDatabaseFile(databasePath));

            jsonDatabaseService.addNewRecordToTable(thirdStudent);
            assertEquals(thirdStudent, jsonDatabaseService.getById(Student.class, 2));

            jsonDatabaseService.addNewRecordToTable(new Course.Builder()
                    .withName("Course2")
                    .build());

            assertEquals(1, jsonDatabaseService.getById(Course.class, 1).getId());
        } finally {
            jsonDatabaseService.deleteTable(Course.class);
        }
    }

    @Test
    void getByIdTest() {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        Student receivedStudentById = jsonDatabaseService.getById(Student.class, 1);

        assertEquals(secondStudent, receivedStudentById);
        assertEquals(1, receivedStudentById.getId());
    }

    @Test
    void getByIdWhenIdDoesNotExistTest() {
        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        assertNull(jsonDatabaseService.getById(Student.class, 13));
    }

    @Test
    void getAllRecordsFromTableTest() {
        List<Student> students = List.of(firstStudent, secondStudent);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);

        assertEquals(students, jsonDatabaseService.getAllRecordsFromTable(Student.class));
    }

    @Test
    void getAllRecordsFromTableWithValidLimitOffsetParametersTest() {
        List<Student> resultStudents = List.of(secondStudent, thirdStudent);

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        jsonDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(resultStudents, jsonDatabaseService.getAllRecordsFromTable(Student.class, 2, 1));
    }

    @Test
    void getAllRecordsFromTableWithNegativeLimitOffsetParametersTest() {
        var exception = assertThrows(InvalidParameterValueException.class, () ->
                jsonDatabaseService.getAllRecordsFromTable(Student.class, -1, -1));

        assertEquals(INVALID_PARAMETER_VALUE.replace("{MAX_LIMIT_VALUE}", "100"),
                exception.getMessage());
    }

    @Test
    void getAllRecordsFromTableWithOverLimitParameterTest() {
        var exception = assertThrows(InvalidParameterValueException.class, () ->
                jsonDatabaseService.getAllRecordsFromTable(Student.class, 200, 1));

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

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        jsonDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersSingleFilterWithListOfValuesMatchTest() {
        List<Student> students = List.of(firstStudent, secondStudent, fourthStudent);

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("FirstName1 LastName1", "FirstName2 LastName2")
        );

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        jsonDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));
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

        jsonDatabaseService.createTable(OxfordStudent.class);
        jsonDatabaseService.addNewRecordToTable(os);

        assertEquals(students, jsonDatabaseService.getByFilters(OxfordStudent.class, filters));

        jsonDatabaseService.deleteTable(OxfordStudent.class);
    }

    @Test
    void getByFiltersOnlyOneFilterMatchesTest() {
        List<Student> students = List.of();

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("Harry Potter"),
                "averageScore", List.of("5.0")
        );

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);
        jsonDatabaseService.addNewRecordToTable(fourthStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersNoFilterMatchesTest() {
        List<Student> students = List.of();

        Map<String, List<String>> filters = Map.of(
                "fullName", List.of("FirstName1 LastName1"),
                "averageScore", List.of("3.0")
        );

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);
        jsonDatabaseService.addNewRecordToTable(secondStudent);
        jsonDatabaseService.addNewRecordToTable(thirdStudent);

        assertEquals(students, jsonDatabaseService.getByFilters(Student.class, filters));
    }

    @Test
    void getByFiltersPropertyNameIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>() {{
            put(null, List.of("0"));
        }};

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersValueIsNullTest() {
        Map<String, List<String>> filters = new HashMap<>() {{
            put("averageScore", null);
        }};

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        NullPropertyNameOrValueException exception = assertThrows(NullPropertyNameOrValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_NULL_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersValueIsEmptyTest() {
        Map<String, List<String>> filters = Map.of("fullName", List.of());

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        EmptyValueException exception = assertThrows(EmptyValueException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals(FILTER_CANNOT_BE_EMPTY_MESSAGE, exception.getMessage());
    }

    @Test
    void getByFiltersIncorrectPropertyNameTest() {
        Map<String, List<String>> filters = Map.of("firstName", List.of("FirstName1 LastName1"));

        jsonDatabaseService.createTable(Student.class);
        jsonDatabaseService.addNewRecordToTable(firstStudent);

        IncorrectPropertyNameException exception = assertThrows(IncorrectPropertyNameException.class, () ->
                jsonDatabaseService.getByFilters(Student.class, filters));
        assertEquals(INCORRECT_FILTER_NAME_MESSAGE + ": firstName", exception.getMessage());
    }
}
