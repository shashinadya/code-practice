package database;

import database.entity.BaseEntity;
import database.entity.Student;
import database.exception.handler.FileDatabaseExceptionHandler;
import database.service.JsonDatabaseService;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

//TODO: now it works for particular entity, it should be rewrite with generics
public class Main {
    static JsonDatabaseService databaseService = new JsonDatabaseService();

    public static void main(String[] args) {
        var app = Javalin.create(Main::configureRouter)
                .start();

        FileDatabaseExceptionHandler.register(app);
    }

    private static void configureRouter(JavalinConfig config) {
        config.router.apiBuilder(() ->
                path("/api/v1/database", () -> {
                    post(ctx -> ctx.json(databaseService.createTable(Student.class)));
                    delete(ctx -> ctx.json(databaseService.deleteTable(Student.class)));
                    path("/{entities}", () -> {
                        post(ctx -> ctx.json(databaseService.addNewRecordToTable(ctx.bodyAsClass(Student.class))));
                        get(ctx -> ctx.json(databaseService.getAllRecordsFromTable(Student.class)));
                        put(ctx -> databaseService.removeAllRecordsFromTable(Student.class));
                        path("/filter", () -> get(Main::handleGetByFilters));
                        path("/{id}", () -> {
                            put(ctx -> ctx.json(databaseService.updateRecordInTable(ctx.bodyAsClass(Student.class),
                                    ctx.bodyAsClass(Student.class).getId())));
                            delete(ctx -> ctx.json(databaseService.removeRecordFromTable(Student.class,
                                    Integer.parseInt(ctx.pathParam("id")))));
                            get(Main::handleGetById);
                        });
                    });
                }));
    }

    private static void handleGetByFilters(Context ctx) {
        String fullName = ctx.queryParam("fullName");
        String averageScore = ctx.queryParam("averageScore");

        Map<String, String> filters = new HashMap<>();

        if (fullName != null) filters.put("fullName", fullName);
        if (averageScore != null) filters.put("averageScore", averageScore);

        Iterable<? extends BaseEntity> result = databaseService.getByFilters(Student.class, filters);

        ctx.json(result);
    }

    private static void handleGetById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        var entity = databaseService.getById(Student.class, id);
        if (entity != null) {
            ctx.json(entity);
        } else {
            throw new NotFoundResponse("Entity with provided id not found: " + id);
        }
    }
}
