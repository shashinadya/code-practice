package database.controller;

import database.exception.BadRequestException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.HttpStatus;

import static io.javalin.http.HttpStatus.BAD_REQUEST;
import static io.javalin.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static io.javalin.http.HttpStatus.NOT_FOUND;

public class FileDatabaseExceptionHandler {

    public void register(Javalin app) {
        app.exception(BadRequestException.class, this::handleBadRequest);
        app.exception(NotFoundResponse.class, this::handleNotFound);
        app.exception(Exception.class, this::handleInternalServerError);
    }

    private void handleBadRequest(BadRequestException e, Context ctx) {
        setResponse(ctx, BAD_REQUEST, BAD_REQUEST.getMessage(), e.getMessage());
    }

    private void handleNotFound(NotFoundResponse e, Context ctx) {
        setResponse(ctx, NOT_FOUND, NOT_FOUND.getMessage(), e.getMessage());
    }

    private void handleInternalServerError(Exception e, Context ctx) {
        setResponse(ctx, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getMessage(), e.getMessage());
    }

    private void setResponse(Context ctx, HttpStatus status, String error, String message) {
        ctx.status(status.getCode());
        ctx.json(new ErrorResponse(status.getCode(), error, message));
    }
}
