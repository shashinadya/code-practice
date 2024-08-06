package database.controller;

import database.exception.BadRequestException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.HttpStatus;

public class FileDatabaseExceptionHandler {

    public void register(Javalin app) {
        app.exception(BadRequestException.class, this::handleBadRequest);
        app.exception(NotFoundResponse.class, this::handleNotFound);
        app.exception(Exception.class, this::handleInternalServerError);
    }

    private void handleBadRequest(BadRequestException e, Context ctx) {
        setResponse(ctx, HttpStatus.BAD_REQUEST, "Bad Request", e.getMessage());
    }

    private void handleNotFound(NotFoundResponse e, Context ctx) {
        setResponse(ctx, HttpStatus.NOT_FOUND, "Not found", e.getMessage());
    }

    private void handleInternalServerError(Exception e, Context ctx) {
        setResponse(ctx, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e.getMessage());
    }

    private void setResponse(Context ctx, HttpStatus status, String error, String message) {
        ctx.status(status.getCode());
        ctx.json(new ErrorResponse(status.getCode(), error, message));
    }
}
