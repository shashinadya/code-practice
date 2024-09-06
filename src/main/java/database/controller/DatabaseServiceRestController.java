package database.controller;

import database.entity.BaseEntity;
import database.service.DatabaseService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;

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
        config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
            pluginConfig.withDefinitionConfiguration((version, definition) -> {
                definition.withInfo(info -> info.setTitle("Javalin OpenAPI"));
            });
        }));
        config.registerPlugin(new SwaggerPlugin());
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

    @OpenApi(
            summary = "Create a new table",
            operationId = "createTable",
            path = "/api/v1/database/{entityClass}/table",
            method = HttpMethod.POST,
            pathParams = {@OpenApiParam(name = "entityClass", description = "Class of the entity", required = true)},
            responses = {@OpenApiResponse(status = "200", description = "Table created"),
                    @OpenApiResponse(status = "404", description = "Table not found"),
                    @OpenApiResponse(status = "400", description = "Invalid entity class")}
    )
    void handleCreateTable(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.createTable(entityClass));
    }

    @OpenApi(
            summary = "Delete a table",
            operationId = "deleteTable",
            path = "/api/v1/database/{entityClass}/table",
            method = HttpMethod.DELETE,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "The class of the entity whose table will be deleted", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Table deleted successfully", content = @OpenApiContent(from = String.class)),
                    @OpenApiResponse(status = "404", description = "Table not found"),
                    @OpenApiResponse(status = "400", description = "Invalid entity class")
            }
    )
    void handleDeleteTable(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.deleteTable(entityClass));
    }

    @OpenApi(
            summary = "Add a new record to the table",
            operationId = "addNewRecordToTable",
            path = "/api/v1/database/{entityClass}",
            method = HttpMethod.POST,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "The class of the entity to which the record will be added", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = BaseEntity.class),
                    description = "The new record to be added",
                    required = true
            ),
            responses = {
                    @OpenApiResponse(status = "200", description = "Record added successfully", content = @OpenApiContent(from = BaseEntity.class)),
                    @OpenApiResponse(status = "400", description = "Invalid entity data"),
                    @OpenApiResponse(status = "404", description = "Entity class not found")
            }
    )
    void handleAddNewRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        var entity = ctx.bodyAsClass(entityClass);
        ctx.json(databaseService.addNewRecordToTable(entity));
    }

    @OpenApi(
            summary = "Get records by filters",
            operationId = "getByFilters",
            path = "/api/v1/database/{entityClass}/filter",
            method = HttpMethod.GET,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "The class of the entity to filter", required = true)
            },
            queryParams = {
                    @OpenApiParam(name = "filter", description = "Filter criteria as key-value pairs", required = false)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Records retrieved successfully", content = @OpenApiContent(from = BaseEntity.class, isArray = true)),
                    @OpenApiResponse(status = "404", description = "No records found for the given filters"),
                    @OpenApiResponse(status = "400", description = "Invalid filter parameters")
            }
    )
    //TODO: do not use this method since service method will be reworked
    void handleGetByFilters(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        Map<String, List<String>> queryParameters = ctx.queryParamMap();
        Iterable<? extends BaseEntity> result = databaseService.getByFilters(entityClass, queryParameters);
        ctx.json(result);
    }

    @OpenApi(
            summary = "Get all records",
            operationId = "getAllRecordsFromTable",
            path = "/api/v1/database/{entityClass}",
            method = HttpMethod.GET,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "The class of the entity", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "List of all records", content = @OpenApiContent(from = BaseEntity[].class)),
                    @OpenApiResponse(status = "400", description = "Invalid entity class"),
                    @OpenApiResponse(status = "404", description = "Entity class not found")
            }
    )
    void handleGetAllRecords(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.getAllRecordsFromTable(entityClass));
    }

    @OpenApi(
            summary = "Update an existing record in the table",
            operationId = "updateRecordInTable",
            path = "/api/v1/database/{entityClass}/{id}",
            method = HttpMethod.PUT,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "The class of the entity to which the record belongs", required = true),
                    @OpenApiParam(name = "id", description = "The ID of the record to update", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = BaseEntity.class),
                    description = "The updated record data",
                    required = true
            ),
            responses = {
                    @OpenApiResponse(status = "200", description = "Record updated successfully", content = @OpenApiContent(from = BaseEntity.class)),
                    @OpenApiResponse(status = "400", description = "Invalid record data"),
                    @OpenApiResponse(status = "404", description = "Record not found")
            }
    )
    void handleUpdateRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        var entity = ctx.bodyAsClass(entityClass);
        ctx.json(databaseService.updateRecordInTable(entity, entity.getId()));
    }

    @OpenApi(
            summary = "Remove a record from the table",
            operationId = "removeRecordFromTable",
            path = "/api/v1/database/{entityClass}/{id}",
            method = HttpMethod.DELETE,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "The class of the entity from which the record will be removed", required = true),
                    @OpenApiParam(name = "id", description = "The ID of the record to be removed", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Record removed successfully"),
                    @OpenApiResponse(status = "404", description = "Record not found"),
                    @OpenApiResponse(status = "400", description = "Invalid entity class or ID")
            }
    )
    void handleRemoveRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        int id = Integer.parseInt(ctx.pathParam(ID_PARAMETER_NAME));
        ctx.json(databaseService.removeRecordFromTable(entityClass, id));
    }

    @OpenApi(
            summary = "Get a record by its ID",
            operationId = "getById",
            path = "/api/v1/database/{entityClass}/{id}",
            method = HttpMethod.GET,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "The class of the entity to retrieve", required = true),
                    @OpenApiParam(name = "id", description = "The ID of the record to retrieve", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Record retrieved successfully", content = @OpenApiContent(from = BaseEntity.class)),
                    @OpenApiResponse(status = "404", description = "Record not found")
            }
    )
    void handleGetById(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        int id = Integer.parseInt(ctx.pathParam(ID_PARAMETER_NAME));
        var entity = databaseService.getById(entityClass, id);
        if (entity == null) {
            throw new NotFoundResponse("Entity with provided id not found: " + id);
        }
        ctx.json(entity);
    }

    @OpenApi(
            summary = "Remove all records from the table",
            operationId = "removeAllRecordsFromTable",
            path = "/api/v1/database/{entityClass}",
            method = HttpMethod.DELETE,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "The class of the entity whose records will be removed", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "All records removed successfully", content = @OpenApiContent(from = BaseEntity.class)),
                    @OpenApiResponse(status = "404", description = "Table not found"),
                    @OpenApiResponse(status = "400", description = "Invalid entity class")
            }
    )
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
            throw new BadRequestResponse("Invalid entity class: " + entityClassName);
        }
    }
}
