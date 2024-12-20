package database.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.entity.BaseEntity;
import database.entity.Course;
import database.entity.OxfordStudent;
import database.entity.Student;
import database.exception.BadRequestException;
import database.dao.EntityDao;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static database.controller.DatabaseServiceRestController.ID_PARAMETER_NAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@code DatabaseServiceRestController} class.
 * <p>
 * This test class verifies the behavior of the {@code DatabaseServiceRestController} methods
 * related to creating, deleting tables, and adding new records.
 * <p>
 * The tests use mocked instances of {@code EntityDao} and {@code Context} to simulate
 * the behavior of the controller in various scenarios, ensuring that appropriate responses are
 * returned or exceptions are thrown based on the dao's outcome.
 * <p>
 * Each test focuses on a specific REST endpoint (GET, POST, PUT or DELETE) and tests both successful
 * and failure cases.
 *
 * <p>Mocking frameworks such as {@code Mockito} are used to mock the dependencies and control the behavior
 * of the dao and context, allowing the tests to focus on the controller logic.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 * @see DatabaseServiceRestController
 * @see EntityDao
 * @see Context
 */
class DatabaseServiceRestControllerTest {
    private final EntityDao entityDao = mock(EntityDao.class);
    private final Context ctx = mock(Context.class);
    private final DatabaseServiceRestController controller = new DatabaseServiceRestController(entityDao,
            Set.of(Student.class, OxfordStudent.class, Course.class));
    private final String ENTITY_CLASS_NAME = "Student";
    private final String ENTITY_CLASS_PARAMETER_NAME = "entityClass";

    @Test
    void POST_to_create_table_returns_true() {
        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(entityDao.createTable(any())).thenReturn(true);

        controller.handleCreateTable(ctx);
        verify(ctx).json(true);
    }

    @Test
    void POST_to_create_table_returns_500_when_table_already_exists() {
        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(entityDao.createTable(any())).thenThrow(InternalServerErrorResponse.class);

        assertThrows(InternalServerErrorResponse.class, () -> controller.handleCreateTable(ctx));
    }

    @Test
    void DELETE_to_delete_table_returns_true() {
        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(entityDao.deleteTable(any())).thenReturn(true);

        controller.handleDeleteTable(ctx);
        verify(ctx).json(true);
    }

    @Test
    void DELETE_to_delete_table_returns_500_when_table_not_exist() {
        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(entityDao.deleteTable(any())).thenThrow(InternalServerErrorResponse.class);

        assertThrows(InternalServerErrorResponse.class, () -> controller.handleDeleteTable(ctx));
    }

    @Test
    void POST_to_add_new_record_returns_json_with_entity() {
        Student entity = new Student();
        Student newEntity = new Student();

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.bodyAsClass(Student.class)).thenReturn(entity);
        when(entityDao.addNewRecordToTable(entity)).thenReturn(newEntity);

        controller.handleAddNewRecord(ctx);
        verify(ctx).json(newEntity);
    }

    @Test
    void POST_to_add_new_record_returns_400_when_table_not_exist() {
        Student entity = new Student();

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.bodyAsClass(Student.class)).thenReturn(entity);
        when(entityDao.addNewRecordToTable(entity)).thenThrow(BadRequestResponse.class);

        assertThrows(BadRequestResponse.class, () -> controller.handleAddNewRecord(ctx));
    }

    @Test
    void POST_to_add_new_records_returns_json_with_entities() throws JsonProcessingException {
        List<Student> newStudents = List.of(new Student("Iva", 3.5), new Student("Nadya", 4.5));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(newStudents);

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.body()).thenReturn(jsonString);
        when(entityDao.addNewRecordsToTable(Student.class, newStudents)).thenReturn(newStudents);

        controller.handleAddNewRecords(ctx);
        verify(ctx).json(newStudents);
    }

    @Test
    void POST_to_add_new_records_returns_400_when_table_not_exist() throws JsonProcessingException {
        List<Student> newStudents = List.of(new Student("Iva", 3.5), new Student("Nadya", 4.5));
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(newStudents);

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.body()).thenReturn(jsonString);
        when(entityDao.addNewRecordsToTable(Student.class, newStudents)).thenThrow(BadRequestResponse.class);

        assertThrows(BadRequestResponse.class, () -> controller.handleAddNewRecords(ctx));
    }

    @Test
    void GET_to_get_all_records_returns_json_with_entities() {
        Iterable<BaseEntity> entities = List.of(new Student());

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(entityDao.getAllRecordsFromTable(Student.class)).thenReturn(entities);

        controller.handleGetAllRecords(ctx);
        verify(ctx).json(entities);
    }

    @Test
    void GET_to_get_all_records_with_limit_offset_returns_json_with_entities() {
        Iterable<BaseEntity> entities = List.of(new Student());

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.queryParam("limit")).thenReturn("1");
        when(ctx.queryParam("offset")).thenReturn("1");
        when(entityDao.getAllRecordsFromTable(Student.class, 1, 1)).thenReturn(entities);

        controller.handleGetAllRecords(ctx);
        verify(ctx).json(entities);
    }

    @Test
    void GET_to_get_all_records_returns_500_when_table_not_exist() {
        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(entityDao.getAllRecordsFromTable(Student.class)).thenThrow(InternalServerErrorResponse.class);

        assertThrows(InternalServerErrorResponse.class, () -> controller.handleGetAllRecords(ctx));
    }

    @Test
    void PUT_to_update_record_returns_json_with_entity() {
        Student entity = new Student();
        entity.setId(1);
        Student updatedEntity = new Student();

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.bodyAsClass(Student.class)).thenReturn(entity);
        when(ctx.pathParam(ID_PARAMETER_NAME)).thenReturn("1");
        when(entityDao.updateRecordInTable(entity, 1)).thenReturn(updatedEntity);

        controller.handleUpdateRecord(ctx);
        verify(ctx).json(updatedEntity);
    }

    @Test
    void PUT_to_update_record_never_called_when_id_not_match() {
        Student entity = new Student();
        entity.setId(1);

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.pathParam(ID_PARAMETER_NAME)).thenReturn("2");
        when(ctx.bodyAsClass(Student.class)).thenReturn(entity);

        verify(entityDao, never()).updateRecordInTable(any(), any());
    }

    @Test
    void PUT_to_update_record_returns_400_when_invalid_id() {
        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.pathParam(ID_PARAMETER_NAME)).thenReturn("A");

        assertThrows(BadRequestException.class, () -> controller.handleUpdateRecord(ctx));
    }

    @Test
    void DELETE_to_remove_record_returns_true() {
        int id = 1;
        Student entity = new Student();
        entity.setId(id);

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.pathParam(ID_PARAMETER_NAME)).thenReturn(String.valueOf(id));
        when(entityDao.removeRecordFromTable(Student.class, 1)).thenReturn(true);

        controller.handleRemoveRecord(ctx);
        verify(ctx).json(true);
    }

    @Test
    void DELETE_to_remove_record_returns_false() {
        int id = 1;
        Student entity = new Student();
        entity.setId(id);

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.pathParam(ID_PARAMETER_NAME)).thenReturn(String.valueOf(id));
        when(entityDao.removeRecordFromTable(Student.class, 10)).thenReturn(false);

        controller.handleRemoveRecord(ctx);
        verify(ctx).json(false);
    }

    @Test
    void DELETE_to_remove_record_returns_400_when_invalid_id() {
        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.pathParam(ID_PARAMETER_NAME)).thenReturn("A");

        assertThrows(BadRequestException.class, () -> controller.handleRemoveRecord(ctx));
    }

    @Test
    void DELETE_to_remove_specific_records_returns_true() {
        List<Integer> ids = List.of(1, 2);

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.bodyAsClass(Integer[].class)).thenReturn(ids.toArray(new Integer[0]));
        when(entityDao.removeSpecificRecordsFromTable(Student.class, ids)).thenReturn(true);

        controller.handleRemoveSpecificRecords(ctx);
        verify(ctx).json(true);
    }

    @Test
    void DELETE_to_remove_specific_records_returns_false() {
        List<Integer> ids = List.of(1, 2);

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.bodyAsClass(Integer[].class)).thenReturn(ids.toArray(new Integer[0]));
        when(entityDao.removeSpecificRecordsFromTable(Student.class, ids)).thenReturn(false);

        controller.handleRemoveSpecificRecords(ctx);
        verify(ctx).json(false);
    }

    @Test
    void DELETE_to_remove_all_records_returns_true() {
        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        doNothing().when(entityDao).removeAllRecordsFromTable(Student.class);

        controller.handleRemoveAllRecords(ctx);
        verify(entityDao).removeAllRecordsFromTable(Student.class);
    }

    @Test
    void GET_to_get_record_by_id_returns_json_with_entity() {
        int id = 1;
        Student entity = new Student();
        entity.setId(id);

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.pathParam(ID_PARAMETER_NAME)).thenReturn(String.valueOf(id));
        when(entityDao.getById(Student.class, id)).thenReturn(entity);

        controller.handleGetById(ctx);
        verify(ctx).json(entity);
    }

    @Test
    void GET_to_get_record_by_id_returns_404_when_table_not_exist() {
        int id = 1;
        Student entity = new Student();
        entity.setId(id);

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.pathParam(ID_PARAMETER_NAME)).thenReturn(String.valueOf(id));
        when(entityDao.getById(Student.class, id)).thenThrow(NotFoundResponse.class);

        assertThrows(NotFoundResponse.class, () -> controller.handleGetById(ctx));
    }

    @Test
    void GET_to_get_record_by_id_returns_400_when_invalid_id() {
        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.pathParam(ID_PARAMETER_NAME)).thenReturn("A");

        assertThrows(BadRequestException.class, () -> controller.handleGetById(ctx));
    }

    @Test
    void GET_to_get_entities_by_filters_return_json_with_entities() {
        var queryParameters = Map.of("fullNames", List.of("FirstStudent", "SecondStudent"));
        Iterable<BaseEntity> entities = List.of(new Student());

        when(ctx.pathParam(ENTITY_CLASS_PARAMETER_NAME)).thenReturn(ENTITY_CLASS_NAME);
        when(ctx.queryParamMap()).thenReturn(queryParameters);
        when(entityDao.getByFilters(Student.class, queryParameters)).thenReturn(entities);

        controller.handleGetByFilters(ctx);
        verify(ctx).json(entities);
    }
}
