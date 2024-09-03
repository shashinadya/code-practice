package database.controller;

import database.entity.BaseEntity;
import database.service.DatabaseService;
import database.service.JsonDatabaseService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

public class DatabaseServiceRestController {
    private static final DatabaseService databaseService = new JsonDatabaseService();

    public void configureRouter(JavalinConfig config) {
        config.router.apiBuilder(() ->
                path("/api/v1/database/{entityClass}", () -> {
                    path("/table", () -> {
                        post(this::handleCreateTable);
                        delete(this::handleDeleteTable);
                    });
                    post(this::handleAddNewRecord);
                    get(this::handleGetAllRecords);
                    delete(this::handleRemoveAllRecords);
                    path("/filter", () -> get(this::handleGetByFilters));
                    path("/{id}", () -> {
                        put(this::handleUpdateRecord);
                        delete(this::handleDeleteRecord);
                        get(this::handleGetById);
                    });
                }));
    }

    private void handleCreateTable(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.createTable(entityClass));
    }

    private void handleDeleteTable(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.deleteTable(entityClass));
    }

    private void handleAddNewRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        var entity = ctx.bodyAsClass(entityClass);
        ctx.json(databaseService.addNewRecordToTable(entity));
    }

    //TODO: do not use this method since service method will be reworked
    private void handleGetByFilters(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);

        Map<String, List<String>> queryParameters = ctx.queryParamMap();

        Iterable<? extends BaseEntity> result = databaseService.getByFilters(entityClass, queryParameters);
        ctx.json(result);
    }

    private void handleGetAllRecords(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.getAllRecordsFromTable(entityClass));
    }

    private void handleUpdateRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        var entity = ctx.bodyAsClass(entityClass);
        ctx.json(databaseService.updateRecordInTable(entity, entity.getId()));
    }

    private void handleDeleteRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        int id = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(databaseService.removeRecordFromTable(entityClass, id));
    }

    private void handleGetById(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        int id = Integer.parseInt(ctx.pathParam("id"));
        var entity = databaseService.getById(entityClass, id);
        if (entity == null) {
            throw new NotFoundResponse("Entity with provided id not found: " + id);
        }
        ctx.json(entity);
    }

    private void handleRemoveAllRecords(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        databaseService.removeAllRecordsFromTable(entityClass);
    }

    private Class<? extends BaseEntity> getClassFromPath(Context ctx) {
        String entityClassName = ctx.pathParam("entityClass");
        try {
            String className = "database.entity." + entityClassName;
            return (Class<? extends BaseEntity>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BadRequestResponse("Invalid entity class: " + entityClassName);
        }
    }
}
