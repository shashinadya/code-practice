package database.exception.handler;

import database.exception.exception.BadRequestException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.util.Map;

public class FileDatabaseExceptionHandler {

    public static void register(Javalin app) {
        app.exception(BadRequestException.class, FileDatabaseExceptionHandler::handleBadRequest);
        app.exception(NotFoundResponse.class, FileDatabaseExceptionHandler::handleNotFound);
        app.exception(Exception.class, FileDatabaseExceptionHandler::handleInternalServerError);
    }

    private static void handleBadRequest(BadRequestException e, Context ctx) {
        ctx.status(400); // Set status code
        ctx.json(Map.of(
                "status", 400,
                "error", "Bad request",
                "message", e.getMessage()
        ));
    }

    private static void handleNotFound(NotFoundResponse e, Context ctx) {
        ctx.status(404); // Set status code
        ctx.json(Map.of(
                "status", 404,
                "error", "Not found",
                "message", e.getMessage()
        ));
    }

    private static void handleInternalServerError(Exception e, Context ctx) {
        ctx.status(500); // Set status code
        ctx.json(Map.of(
                "status", 500,
                "error", "Internal server error",
                "message", e.getMessage()
        ));
    }
}
