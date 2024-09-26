package database.controller;

import database.entity.BaseEntity;
import database.service.DatabaseService;
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
    private DatabaseService databaseService;
    static final String ID_PARAMETER_NAME = "id";

    public DatabaseServiceRestController() {
    }

    public DatabaseServiceRestController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

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
                        delete(this::handleRemoveRecord);
                        get(this::handleGetById);
                    });
                }));
    }

    void handleCreateTable(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.createTable(entityClass));
    }

    void handleDeleteTable(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.deleteTable(entityClass));
    }

    void handleAddNewRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        var entity = ctx.bodyAsClass(entityClass);
        ctx.json(databaseService.addNewRecordToTable(entity));
    }

    //TODO: do not use this method since database.service method will be reworked
    void handleGetByFilters(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        Map<String, List<String>> queryParameters = ctx.queryParamMap();
        Iterable<? extends BaseEntity> result = databaseService.getByFilters(entityClass, queryParameters);
        ctx.json(result);
    }

    void handleGetAllRecords(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        String limitParam = ctx.queryParam("limit");
        String offsetParam = ctx.queryParam("offset");

        if (limitParam != null && offsetParam != null) {
            try {
                int limit = Integer.parseInt(limitParam);
                int offset = Integer.parseInt(offsetParam);

                ctx.json(databaseService.getAllRecordsFromTable(entityClass, limit, offset));
            } catch (NumberFormatException e) {
                ctx.status(400).result("Invalid value for limit or offset parameter. They must be integers.");
            }
        } else {
            ctx.json(databaseService.getAllRecordsFromTable(entityClass));
        }
    }

    void handleUpdateRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        Integer pathId = Integer.parseInt(ctx.pathParam("id"));
        var entity = ctx.bodyAsClass(entityClass);

        if (entity.getId() == null || !entity.getId().equals(pathId)) {
            ctx.status(400);
            ctx.result("ID in the path and entity ID do not match or entity ID is missing.");
            return;
        }

        ctx.json(databaseService.updateRecordInTable(entity, pathId));
    }

    void handleRemoveRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        int id = Integer.parseInt(ctx.pathParam(ID_PARAMETER_NAME));
        ctx.json(databaseService.removeRecordFromTable(entityClass, id));
    }

    void handleGetById(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        int id = Integer.parseInt(ctx.pathParam(ID_PARAMETER_NAME));
        var entity = databaseService.getById(entityClass, id);
        if (entity == null) {
            throw new NotFoundResponse("Entity with provided id not found: " + id);
        }
        ctx.json(entity);
    }

    void handleRemoveAllRecords(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        databaseService.removeAllRecordsFromTable(entityClass);
    }

    private Class<? extends BaseEntity> getClassFromPath(Context ctx) {
        String entityClassName = ctx.pathParam("entityClass");
        try {
            String className = "database.entity." + entityClassName;
            return (Class<? extends BaseEntity>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BadRequestResponse("Invalid database.entity class: " + entityClassName);
        }
    }
}
