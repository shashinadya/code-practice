package code.practice;

import database.entity.BaseEntity;
import database.entity.Student;
import database.exception.CreationDatabaseException;
import database.exception.DatabaseDoesNotExistException;
import database.exception.DeletionDatabaseException;
import database.exception.IdDoesNotExistException;
import database.exception.WriteFileException;
import database.service.JsonDatabaseService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class Main {
    static JsonDatabaseService databaseService = new JsonDatabaseService();

    public static void main(String[] args) {
        var app = Javalin.create()
                .start();

        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Server Error: " + e.getMessage());
        });

        app.exception(CreationDatabaseException.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Server Error: " + e.getMessage());
        });

        app.exception(DeletionDatabaseException.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Server Error: " + e.getMessage());
        });

        app.exception(DatabaseDoesNotExistException.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Server Error: " + e.getMessage());
        });

        app.exception(IdDoesNotExistException.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Server Error: " + e.getMessage());
        });

        app.exception(WriteFileException.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500);
            ctx.result("Server Error: " + e.getMessage());
        });

        app.post("/api/v1/database", ctx -> ctx.json(databaseService.createTable(Student.class)))
                .delete("/api/v1/database", ctx -> ctx.json(databaseService.deleteTable(Student.class)))
                .post("/api/v1/database/{entities}", ctx ->
                        ctx.json(databaseService.addNewRecordToTable(ctx.bodyAsClass(Student.class))))
                .get("/api/v1/database/{entities}", ctx ->
                        ctx.json(databaseService.getAllRecordsFromTable(Student.class)))
                .put("/api/v1/database/{entities}/{id}", ctx ->
                        ctx.json(databaseService.updateRecordInTable(ctx.bodyAsClass(Student.class),
                                ctx.bodyAsClass(Student.class).getId())))
                .delete("/api/v1/database/{entities}/{id}", ctx ->
                        ctx.json(databaseService.removeRecordFromTable(Student.class,
                                Integer.parseInt(ctx.pathParam("id")))))
                .put("/api/v1/database/{entities}", ctx ->
                        databaseService.removeAllRecordsFromTable(Student.class))
                .get("/api/v1/database/{entities}/filter", ctx -> handleGetByFilters(ctx))
                .get("/api/v1/database/{entities}/{id}", ctx -> handleGetById(ctx));
    }

    public static void handleGetByFilters(Context ctx) {
        String fullName = ctx.queryParam("fullName");
        String averageScore = ctx.queryParam("averageScore");

        Map<String, String> filters = new HashMap<>();

        if (fullName != null) filters.put("fullName", fullName);
        if (averageScore != null) filters.put("averageScore", averageScore);

        Iterable<? extends BaseEntity> result = databaseService.getByFilters(Student.class, filters);

        ctx.json(result);
    }

    public static void handleGetById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        if (databaseService.getById(Student.class, id) != null) {
            ctx.json(databaseService.getById(Student.class, id));
        } else {
            ctx.status(404);
            ctx.result("Entity with ID: " + id + " not found");
        }
    }
}
