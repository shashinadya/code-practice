package database;

import database.controller.DatabaseServiceRestController;
import database.controller.FileDatabaseExceptionHandler;
import io.javalin.Javalin;

public class Main {
    public static DatabaseServiceRestController dbServiceRestController = new DatabaseServiceRestController();

    public static void main(String[] args) {
        var app = Javalin.create(config -> dbServiceRestController.configureRouter(config))
                .start();

        FileDatabaseExceptionHandler fileDatabaseExceptionHandler = new FileDatabaseExceptionHandler();
        fileDatabaseExceptionHandler.register(app);
    }
}
