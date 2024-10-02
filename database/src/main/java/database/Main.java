package database;

import database.controller.DatabaseServiceRestController;
import database.controller.DatabaseControllerExceptionHandler;
import database.helper.Settings;
import database.service.JsonDatabaseService;
import database.service.SqlDatabaseService;
import io.javalin.Javalin;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        Settings settings = new Settings("application.properties");
        int port = settings.getPort();
        final var databaseService = new SqlDatabaseService(settings);
//        final var databaseService = new JsonDatabaseService("application.properties");
        final var dbServiceRestController = new DatabaseServiceRestController(databaseService);
        var app = Javalin.create(dbServiceRestController::configureRouter)
                .start(port);

        DatabaseControllerExceptionHandler fileDatabaseExceptionHandler = new DatabaseControllerExceptionHandler();
        fileDatabaseExceptionHandler.register(app);
    }
}
