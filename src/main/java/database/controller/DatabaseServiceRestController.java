package database.controller;

import database.entity.BaseEntity;
import database.service.JsonDatabaseService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DatabaseServiceRestController {
    private static final JsonDatabaseService databaseService = new JsonDatabaseService();

    public void configureRouter(JavalinConfig config) {
        config.router.apiBuilder(() ->
                path("/api/v1/database", () -> {
                    path("/{entityClass}", () -> {
                        post(this::handleCreateTable);
                        delete(this::handleDeleteTable);
                        path("/{entities}", () -> {
                            post(this::handleAddNewRecord);
                            path("/filter", () -> get(this::handleGetByFilters));
                            get(this::handleGetAllRecords);
                            path("/{id}", () -> {
                                put(this::handleUpdateRecord);
                                delete(this::handleDeleteRecord);
                                get(this::handleGetById);
                            });
                            delete(this::handleRemoveAllRecords);
                        });
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
        BaseEntity entity = ctx.bodyAsClass(entityClass);
        ctx.json(databaseService.addNewRecordToTable(entity));
    }

    private void handleGetByFilters(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        Map<String, Object> filters = new HashMap<>();

        //TODO: change filter logic with generics
        String fullName = ctx.queryParam("fullName");
        String averageScoreParam = ctx.queryParam("averageScore");

        if (fullName != null) {
            filters.put("fullName", fullName);
        }
        if (averageScoreParam != null) {
            try {
                double averageScore = Double.parseDouble(averageScoreParam);
                filters.put("averageScore", averageScore);
            } catch (NumberFormatException e) {
                throw new BadRequestResponse("Invalid format for averageScore. Expected a number.");
            }
        }

        Iterable<? extends BaseEntity> result = databaseService.getByFilters(entityClass, filters);
        ctx.json(result);
    }

    private void handleGetAllRecords(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.getAllRecordsFromTable(entityClass));
    }

    private void handleUpdateRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        BaseEntity entity = ctx.bodyAsClass(entityClass);
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
