package database.controller;

import database.entity.BaseEntity;
import database.entity.Student;
import database.service.DatabaseService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatabaseServiceRestControllerTest {
    private final DatabaseServiceRestController controller = new DatabaseServiceRestController();
    private final Context ctx = mock(Context.class);
    private final DatabaseService databaseService = mock(DatabaseService.class);

    public DatabaseServiceRestControllerTest() {
        controller.setDatabaseService(databaseService);
    }

    @BeforeEach
    void setUp() {
        reset(ctx);
    }

    @Test
    void POST_to_create_table_returns_true() {
        String entityClass = "Student";

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(databaseService.createTable(any())).thenReturn(true);

        controller.handleCreateTable(ctx);
        verify(ctx).json(true);
    }

    @Test
    void POST_to_create_table_returns_500_when_database_already_exists() {
        String entityClass = "Student";

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(databaseService.createTable(any())).thenThrow(InternalServerErrorResponse.class);

        assertThrows(InternalServerErrorResponse.class, () -> controller.handleCreateTable(ctx));
    }

    @Test
    void DELETE_to_delete_table_returns_true() {
        String entityClass = "Student";

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(databaseService.deleteTable(any())).thenReturn(true);

        controller.handleDeleteTable(ctx);
        verify(ctx).json(true);
    }

    @Test
    void DELETE_to_delete_table_returns_500_when_database_not_exist() {
        String entityClass = "Student";

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(databaseService.deleteTable(any())).thenThrow(InternalServerErrorResponse.class);

        assertThrows(InternalServerErrorResponse.class, () -> controller.handleDeleteTable(ctx));
    }

    @Test
    void POST_to_add_new_record_returns_json_with_entity() {
        String entityClass = "Student";
        Student entity = new Student();
        Student newEntity = new Student();

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(ctx.bodyAsClass(Student.class)).thenReturn(entity);
        when(databaseService.addNewRecordToTable(entity)).thenReturn(newEntity);

        controller.handleAddNewRecord(ctx);
        verify(ctx).json(newEntity);
    }

    @Test
    void POST_to_add_new_record_returns_400_when_database_not_exists() {
        String entityClass = "Student";
        Student entity = new Student();

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(ctx.bodyAsClass(Student.class)).thenReturn(entity);
        when(databaseService.addNewRecordToTable(entity)).thenThrow(BadRequestResponse.class);

        assertThrows(BadRequestResponse.class, () -> controller.handleAddNewRecord(ctx));
    }

    @Test
    void GET_to_get_all_records_returns_json_with_entities() {
        String entityClass = "Student";
        Iterable<BaseEntity> entities = List.of(new Student());

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(databaseService.getAllRecordsFromTable(Student.class)).thenReturn(entities);

        controller.handleGetAllRecords(ctx);
        verify(ctx).json(entities);
    }

    @Test
    void GET_to_get_all_records_returns_500_when_database_not_exist() {
        String entityClass = "Student";

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(databaseService.getAllRecordsFromTable(Student.class)).thenThrow(InternalServerErrorResponse.class);

        assertThrows(InternalServerErrorResponse.class, () -> controller.handleGetAllRecords(ctx));
    }

    @Test
    void PUT_to_update_record_returns_json_with_entity() {
        String entityClass = "Student";
        Student entity = new Student();
        entity.setId(1);
        Student updatedEntity = new Student();

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(ctx.bodyAsClass(Student.class)).thenReturn(entity);
        when(databaseService.updateRecordInTable(entity, 1)).thenReturn(updatedEntity);

        controller.handleUpdateRecord(ctx);
        verify(ctx).json(updatedEntity);
    }

    @Test
    void PUT_to_update_record_returns_400_when_id_not_exist() {
        String entityClass = "Student";
        Student entity = new Student();
        entity.setId(1);

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(ctx.bodyAsClass(Student.class)).thenReturn(entity);
        when(databaseService.updateRecordInTable(entity, 1)).thenThrow(BadRequestResponse.class);

        assertThrows(BadRequestResponse.class, () -> controller.handleUpdateRecord(ctx));
    }

    @Test
    void DELETE_to_remove_record_returns_true() {
        String entityClass = "Student";
        int id = 1;
        Student entity = new Student();
        entity.setId(id);

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(ctx.pathParam("id")).thenReturn(String.valueOf(id));
        when(databaseService.removeRecordFromTable(Student.class, 1)).thenReturn(true);

        controller.handleRemoveRecord(ctx);
        verify(ctx).json(true);
    }

    @Test
    void DELETE_to_remove_record_returns_false() {
        String entityClass = "Student";
        int id = 1;
        Student entity = new Student();
        entity.setId(id);

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(ctx.pathParam("id")).thenReturn(String.valueOf(id));
        when(databaseService.removeRecordFromTable(Student.class, 10)).thenReturn(false);

        controller.handleRemoveRecord(ctx);
        verify(ctx).json(false);
    }

    @Test
    void DELETE_to_remove_all_records_returns_true() {
        String entityClass = "Student";

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        doNothing().when(databaseService).removeAllRecordsFromTable(Student.class);

        controller.handleRemoveAllRecords(ctx);
        verify(databaseService).removeAllRecordsFromTable(Student.class);
    }

    @Test
    void GET_to_get_record_by_id_returns_json_with_entity() {
        String entityClass = "Student";
        int id = 1;
        Student entity = new Student();
        entity.setId(id);

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(ctx.pathParam("id")).thenReturn(String.valueOf(id));
        when(databaseService.getById(Student.class, id)).thenReturn(entity);

        controller.handleGetById(ctx);
        verify(ctx).json(entity);
    }

    @Test
    void GET_to_get_record_by_id_returns_404_when_database_not_exist() {
        String entityClass = "Student";
        int id = 1;
        Student entity = new Student();
        entity.setId(id);

        when(ctx.pathParam("entityClass")).thenReturn(entityClass);
        when(ctx.pathParam("id")).thenReturn(String.valueOf(id));
        when(databaseService.getById(Student.class, id)).thenThrow(NotFoundResponse.class);

        assertThrows(NotFoundResponse.class, () -> controller.handleGetById(ctx));
    }
}
