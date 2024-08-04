package database.controller;

import database.exception.BadRequestException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

public class FileDatabaseExceptionHandler {

    public void register(Javalin app) {
        FileDatabaseExceptionHandler fileDatabaseExceptionHandler = new FileDatabaseExceptionHandler();
        app.exception(BadRequestException.class, fileDatabaseExceptionHandler::handleBadRequest);
        app.exception(NotFoundResponse.class, fileDatabaseExceptionHandler::handleNotFound);
        app.exception(Exception.class, fileDatabaseExceptionHandler::handleInternalServerError);
    }

    private void handleBadRequest(BadRequestException e, Context ctx) {
        ctx.status(400);
        ctx.json(new ErrorResponse("400", "Bad Request", e.getMessage()));
    }

    private void handleNotFound(NotFoundResponse e, Context ctx) {
        ctx.status(404);
        ctx.json(new ErrorResponse("404", "Not found", e.getMessage()));
    }

    private void handleInternalServerError(Exception e, Context ctx) {
        ctx.status(500);
        ctx.json(new ErrorResponse("500", "Internal server error", e.getMessage()));
    }
}
