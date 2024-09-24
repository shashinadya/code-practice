package database;

import database.controller.DatabaseServiceRestController;
import database.controller.FileDatabaseExceptionHandler;
import database.service.JsonDatabaseService;
import database.service.SqlDatabaseService;
import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {
        final var databaseService = new SqlDatabaseService();
        final var dbServiceRestController = new DatabaseServiceRestController(databaseService);
        var app = Javalin.create(dbServiceRestController::configureRouter)
                .start();

        FileDatabaseExceptionHandler fileDatabaseExceptionHandler = new FileDatabaseExceptionHandler();
        fileDatabaseExceptionHandler.register(app);
    }
}
