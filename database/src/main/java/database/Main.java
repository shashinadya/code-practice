package database;

import database.controller.DatabaseServiceRestController;
import database.controller.DatabaseControllerExceptionHandler;
import database.helper.Swagger;
import database.helper.Utils;
import database.service.SqlDatabaseService;
import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;

public class Main {

    public static void main(String[] args) {
        final var entities = Utils.getSubclassesOfBaseEntity();
        var app = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(pluginConfig ->
                    pluginConfig.withDefinitionConfiguration((version, definition) ->
                            definition.withInfo(info -> {
                                info.setDescription(Swagger.buildDescription(entities));
                                info.setTitle(Swagger.TITLE);
                            }))));
            config.registerPlugin(new SwaggerPlugin());

            final var databaseService = new SqlDatabaseService("jdbc:mysql://localhost:3306/",
                    "db_user", "Qwerty!1", "entities", "application.properties");
            final var dbServiceRestController = new DatabaseServiceRestController(databaseService, entities);
            dbServiceRestController.configureRouter(config);
        }).start();

        var databaseControllerExceptionHandler = new DatabaseControllerExceptionHandler();
        databaseControllerExceptionHandler.register(app);

        System.out.println("Check out Swagger UI docs at http://localhost:8080/swagger");
    }
}
