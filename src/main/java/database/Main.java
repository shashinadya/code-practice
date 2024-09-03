package database;

import database.controller.DatabaseServiceRestController;
import database.controller.FileDatabaseExceptionHandler;
import io.javalin.Javalin;

public class Main {
    private static final DatabaseServiceRestController dbServiceRestController = new DatabaseServiceRestController();

    public static void main(String[] args) {
        var app = Javalin.create(dbServiceRestController::configureRouter)
                .start();

        FileDatabaseExceptionHandler fileDatabaseExceptionHandler = new FileDatabaseExceptionHandler();
        fileDatabaseExceptionHandler.register(app);
    }
}
