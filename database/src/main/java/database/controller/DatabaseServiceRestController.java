package database.controller;

import database.entity.BaseEntity;
import database.exception.BadRequestException;
import database.exception.IdMismatchException;
import database.exception.InvalidParameterValueException;
import database.service.DatabaseService;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

public class DatabaseServiceRestController {
    private final DatabaseService databaseService;
    private final Set<Class<? extends BaseEntity>> entities;
    static final String ID_PARAMETER_NAME = "id";
    static final String INVALID_PARAM_VALUE = "Invalid value for limit or offset parameter. They must be integers.";
    static final String INVALID_ID_VALUE = "Invalid value for id parameter. It must be integer.";

    public DatabaseServiceRestController(DatabaseService databaseService, Set<Class<? extends BaseEntity>> entities) {
        this.databaseService = databaseService;
        this.entities = entities;
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

    @OpenApi(
            summary = "Create new table",
            operationId = "createTable",
            path = "/api/v1/database/{entityClass}/table",
            methods = HttpMethod.POST,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "Class of the entity", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Table created",
                            content = @OpenApiContent(from = Boolean.class)),
                    @OpenApiResponse(status = "400", description = "Invalid entity class"),
                    @OpenApiResponse(status = "500", description = "Unable to create table")
            }
    )
    void handleCreateTable(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.createTable(entityClass));
    }

    @OpenApi(
            summary = "Delete table",
            operationId = "deleteTable",
            path = "/api/v1/database/{entityClass}/table",
            methods = HttpMethod.DELETE,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "Class of the entity", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Table deleted successfully",
                            content = @OpenApiContent(from = Boolean.class)),
                    @OpenApiResponse(status = "400", description = "Invalid entity class"),
                    @OpenApiResponse(status = "500", description = "Unable to delete table")
            }
    )
    void handleDeleteTable(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        ctx.json(databaseService.deleteTable(entityClass));
    }

    @OpenApi(
            summary = "Add new record to table",
            operationId = "addNewRecord",
            path = "/api/v1/database/{entityClass}",
            methods = HttpMethod.POST,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "Class of the entity", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = BaseEntity.class),
                    description = "New record to be added",
                    required = true
            ),
            responses = {
                    @OpenApiResponse(status = "200", description = "Record added successfully",
                            content = @OpenApiContent(from = BaseEntity.class)),
                    @OpenApiResponse(status = "400", description = "Invalid entity class"),
                    @OpenApiResponse(status = "500", description = "Invalid entity data or unable to create record")
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
            methods = HttpMethod.GET,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "Class of the entity", required = true)
            },
            queryParams = {
                    @OpenApiParam(name = "Format: fieldName=value, separated by &",
                            description = "Filters by entity's fields. Multiple values for one field can be provided " +
                                    "using the following pattern: filter1=value1&filter1=value2")
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "List of all records satisfied by given filters",
                            content = @OpenApiContent(from = BaseEntity[].class)),
                    @OpenApiResponse(status = "400", description = "Invalid entity class"),
                    @OpenApiResponse(status = "400", description = "Invalid filters"),
                    @OpenApiResponse(status = "500", description = "Unable to retrieve records")
            }
    )
    void handleGetByFilters(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        Map<String, List<String>> queryParameters = ctx.queryParamMap();
        Iterable<? extends BaseEntity> result = databaseService.getByFilters(entityClass, queryParameters);
        ctx.json(result);
    }

    @OpenApi(
            summary = "Get all records",
            operationId = "getAllRecords",
            path = "/api/v1/database/{entityClass}",
            methods = HttpMethod.GET,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "Class of the entity", required = true)
            },
            queryParams = {
                    @OpenApiParam(name = "limit", description = "Maximum records count to be retrieved"),
                    @OpenApiParam(name = "offset", description = "Offset value from the first record")
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "List of all records",
                            content = @OpenApiContent(from = BaseEntity[].class)),
                    @OpenApiResponse(status = "400", description = "Invalid entity class"),
                    @OpenApiResponse(status = "400", description = "Invalid id, limit or offset format"),
                    @OpenApiResponse(status = "500", description = "Unable to retrieve records")
            }
    )
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
                throw new InvalidParameterValueException(INVALID_PARAM_VALUE);
            }
        } else {
            ctx.json(databaseService.getAllRecordsFromTable(entityClass));
        }
    }

    @OpenApi(
            summary = "Update existing record",
            operationId = "updateRecord",
            path = "/api/v1/database/{entityClass}/{id}",
            methods = HttpMethod.PUT,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "Class of the entity", required = true),
                    @OpenApiParam(name = "id", description = "Id of the record to update", required = true)
            },
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = BaseEntity.class),
                    description = "Updated record data",
                    required = true
            ),
            responses = {
                    @OpenApiResponse(status = "200", description = "Record updated successfully",
                            content = @OpenApiContent(from = BaseEntity.class)),
                    @OpenApiResponse(status = "400", description = "Invalid entity class or record does not exist"),
                    @OpenApiResponse(status = "400", description = "Invalid id format"),
                    @OpenApiResponse(status = "400", description = "Id in path mismatches id from given entity"),
                    @OpenApiResponse(status = "500", description = "Invalid entity data or unable to update record")
            }
    )
    void handleUpdateRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        Integer pathId;
        try {
            pathId = Integer.parseInt(ctx.pathParam(ID_PARAMETER_NAME));
        } catch (NumberFormatException e) {
            throw new InvalidParameterValueException(INVALID_ID_VALUE);
        }
        var entity = ctx.bodyAsClass(entityClass);
        if (entity.getId() == null || !entity.getId().equals(pathId)) {
            throw new IdMismatchException("ID in the path and entity ID do not match or entity ID is missing.");
        }

        ctx.json(databaseService.updateRecordInTable(entity, pathId));
    }

    @OpenApi(
            summary = "Remove record",
            operationId = "removeRecord",
            path = "/api/v1/database/{entityClass}/{id}",
            methods = HttpMethod.DELETE,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "Class of the entity", required = true),
                    @OpenApiParam(name = "id", description = "Id of the record to be removed", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Record removed successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid entity class or invalid id format"),
                    @OpenApiResponse(status = "500", description = "Unable to remove record")
            }
    )
    void handleRemoveRecord(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        try {
            int id = Integer.parseInt(ctx.pathParam(ID_PARAMETER_NAME));
            ctx.json(databaseService.removeRecordFromTable(entityClass, id));
        } catch (NumberFormatException e) {
            throw new InvalidParameterValueException(INVALID_ID_VALUE);
        }
    }

    @OpenApi(
            summary = "Get record by id",
            operationId = "getById",
            path = "/api/v1/database/{entityClass}/{id}",
            methods = HttpMethod.GET,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "Class of the entity", required = true),
                    @OpenApiParam(name = "id", description = "Id of the record to retrieve", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "Record retrieved successfully",
                            content = @OpenApiContent(from = BaseEntity.class)),
                    @OpenApiResponse(status = "400", description = "Invalid entity class or invalid id format"),
                    @OpenApiResponse(status = "404", description = "Record not found"),
                    @OpenApiResponse(status = "500", description = "Unable to retrieve record")
            }
    )
    void handleGetById(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        int id;
        try {
            id = Integer.parseInt(ctx.pathParam(ID_PARAMETER_NAME));
        } catch (NumberFormatException e) {
            throw new InvalidParameterValueException(INVALID_ID_VALUE);
        }
        var entity = databaseService.getById(entityClass, id);
        if (entity == null) {
            throw new NotFoundResponse("Entity with provided id not found: " + id);
        }
        ctx.json(entity);
    }

    @OpenApi(
            summary = "Remove all records",
            operationId = "removeAllRecords",
            path = "/api/v1/database/{entityClass}",
            methods = HttpMethod.DELETE,
            pathParams = {
                    @OpenApiParam(name = "entityClass", description = "Class of the entity", required = true)
            },
            responses = {
                    @OpenApiResponse(status = "200", description = "All records removed successfully"),
                    @OpenApiResponse(status = "400", description = "Invalid entity class"),
                    @OpenApiResponse(status = "500", description = "Unable to remove all records")
            }
    )
    void handleRemoveAllRecords(Context ctx) {
        Class<? extends BaseEntity> entityClass = getClassFromPath(ctx);
        databaseService.removeAllRecordsFromTable(entityClass);
    }

    private Class<? extends BaseEntity> getClassFromPath(Context ctx) {
        String entityClassName = ctx.pathParam("entityClass");
        return entities.stream()
                .filter(entity -> entity.getSimpleName().equalsIgnoreCase(entityClassName))
                .findAny()
                .orElseThrow(() -> new BadRequestException("Invalid database.entity class: " + entityClassName));
    }
}
