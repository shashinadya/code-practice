package database;

import database.controller.DatabaseServiceRestController;
import database.controller.DatabaseControllerExceptionHandler;
import database.service.JsonDatabaseService;
import database.service.SqlDatabaseService;
import io.javalin.Javalin;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
//        final var databaseService = new SqlDatabaseService("jdbc:mysql://localhost:3306/",
//                "db_user", "Qwerty!1", "entities", "application.properties");
        final var databaseService = new JsonDatabaseService("application.properties");
        final var dbServiceRestController = new DatabaseServiceRestController(databaseService);
        var app = Javalin.create(dbServiceRestController::configureRouter)
                .start();

        DatabaseControllerExceptionHandler fileDatabaseExceptionHandler = new DatabaseControllerExceptionHandler();
        fileDatabaseExceptionHandler.register(app);
    }
}
