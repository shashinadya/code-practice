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

public class Main {

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

        System.out.println("Check out Swagger UI docs at http://localhost:" + port + "/swagger");
    }
}
