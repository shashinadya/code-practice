package code.practice;

import database.entity.BaseEntity;
import database.entity.Student;
import database.service.JsonDatabaseService;
import io.javalin.Javalin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        var databaseService = new JsonDatabaseService();

        var app = Javalin.create(/*config*/)
                .post("/api/v1/database", ctx -> ctx.json(databaseService.createTable(Student.class)))
                .delete("/api/v1/database", ctx -> ctx.json(databaseService.deleteTable(Student.class)))
                .post("/api/v1/database/{entities}", ctx -> ctx.json(databaseService
                        .addNewRecordToTable(ctx.bodyAsClass(Student.class))))
                //.get("/api/v1/database/{entities}", ctx -> ctx.json(databaseService.getAllRecordsFromTable(Student.class)))
                .put("/api/v1/database/{entities}/{id}", ctx -> ctx.json(databaseService
                        .updateRecordInTable(ctx.bodyAsClass(Student.class), ctx.bodyAsClass(Student.class).getId())))
                .get("/api/v1/database/{entities}/{id}", ctx -> ctx.json(databaseService
                        .getById(Student.class, Integer.parseInt(ctx.pathParam("id")))))
                .delete("/api/v1/database/{entities}/{id}", ctx -> ctx.json(databaseService
                        .removeRecordFromTable(Student.class, ctx.pathParam("id"))))
                .put("/api/v1/database/{entities}", ctx -> databaseService.removeAllRecordsFromTable(Student.class))
                .get("/api/v1/database/{entities}", ctx -> {
                    String fullName = ctx.queryParam("fullName");
                    String averageScore = ctx.queryParam("averageScore");
                    List<String> courseIds = ctx.queryParams("courseIds");

                    Map<String, String> filters = new HashMap<>();

                    if (fullName != null) filters.put("fullName", fullName);
                    if (averageScore != null) filters.put("averageScore", averageScore);
                    //if (courseIds != null) filters.put("courseIds", courseIds);

                    Iterable<? extends BaseEntity> result = databaseService.getByFilters(Student.class, filters);

                    ctx.json(result);
                })
                .start();


    }
}
