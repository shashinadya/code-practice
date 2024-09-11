import controller.DatabaseServiceRestController;
import controller.FileDatabaseExceptionHandler;
import service.JsonDatabaseService;
import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {
        final var databaseService = new JsonDatabaseService();
        final var dbServiceRestController = new DatabaseServiceRestController(databaseService);
        var app = Javalin.create(dbServiceRestController::configureRouter)
                .start();

        FileDatabaseExceptionHandler fileDatabaseExceptionHandler = new FileDatabaseExceptionHandler();
        fileDatabaseExceptionHandler.register(app);
    }
}
