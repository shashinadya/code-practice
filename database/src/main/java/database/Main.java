package database;

import database.controller.DatabaseServiceRestController;
import database.controller.DatabaseControllerExceptionHandler;
import database.helper.Swagger;
import database.helper.Utils;
import database.helper.Settings;
import database.service.DatabaseService;
import database.service.SqlDatabaseService;
import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;

/**
 * The {@code Main} class serves as the entry point to the application.
 *
 * <p>This class initializes the application settings, configures the web server using Javalin,
 * registers OpenAPI and Swagger plugins, and sets up routes for the {@code DatabaseServiceRestController}.
 * It also manages the graceful shutdown of the application.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class Main {

    /**
     * The main method which serves as the entry point to the application.
     *
     * <p>This method performs the following tasks:
     * <ul>
     *   <li>Loads application settings from a properties file.</li>
     *   <li>Initializes the database service (in this case, an SQL-based service).</li>
     *   <li>Starts the Javalin web server on the configured port.</li>
     *   <li>Registers OpenAPI and Swagger plugins for API documentation.</li>
     *   <li>Configures REST API routes via {@code DatabaseServiceRestController}.</li>
     *   <li>Handles server shutdown events, ensuring that the database service is properly shut down.</li>
     *   <li>Registers a shutdown hook to stop the Javalin web server when the JVM terminates.</li>
     * </ul>
     *
     * @param args the command-line arguments passed to the program
     */
    public static void main(String[] args) {
        final Settings settings = new Settings("application.properties");
        final int port = settings.getPort();
        final var entities = Utils.getSubclassesOfBaseEntity();

        final DatabaseService databaseService = new SqlDatabaseService(settings);
        var app = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(pluginConfig ->
                    pluginConfig.withDefinitionConfiguration((version, definition) ->
                            definition.withInfo(info -> {
                                info.setDescription(Swagger.buildDescription(entities));
                                info.setTitle(Swagger.TITLE);
                            }))));
            config.registerPlugin(new SwaggerPlugin());

            final var dbServiceRestController = new DatabaseServiceRestController(databaseService, entities);
            dbServiceRestController.configureRouter(config);
        }).start(port);

        app.events(event -> event.serverStopping(databaseService::shutdown));

        var databaseControllerExceptionHandler = new DatabaseControllerExceptionHandler();
        databaseControllerExceptionHandler.register(app);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered. Stopping Javalin...");
            app.stop();
        }));

        System.out.println("Check out Swagger UI docs at http://localhost:" + port + "/swagger");
    }
}
