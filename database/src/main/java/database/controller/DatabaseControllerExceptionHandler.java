package database.controller;

import database.exception.BadRequestException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.HttpStatus;

import static io.javalin.http.HttpStatus.BAD_REQUEST;
import static io.javalin.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static io.javalin.http.HttpStatus.NOT_FOUND;

/**
 * The {@code DatabaseControllerExceptionHandler} class is responsible for handling exceptions
 * in Javalin-based REST APIs by registering exception handlers for various types of exceptions.
 * It ensures that meaningful error responses are returned to clients when errors occur,
 * providing a uniform structure for error messages.
 *
 * <p>This class registers exception handlers for specific exceptions, including:
 * <ul>
 *     <li>{@link BadRequestException} - for handling bad request scenarios (HTTP 400).</li>
 *     <li>{@link NotFoundResponse} - for handling resource not found scenarios (HTTP 404).</li>
 *     <li>{@link Exception} - for handling general internal server errors (HTTP 500).</li>
 * </ul>
 *
 * <p>Each handler sends a JSON-formatted error response with a status code, error description, and message.
 * The error responses are encapsulated using the {@link ErrorResponse} class, which includes details
 * about the error that occurred.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class DatabaseControllerExceptionHandler {

    /**
     * Registers exception handlers for various types of exceptions in the Javalin application.
     * This method is used to ensure that appropriate error handling is in place, providing
     * consistent and informative error responses to clients.
     *
     * <p>The following exceptions are handled:
     * <ul>
     *     <li>{@link BadRequestException} - Returns HTTP 400 when an invalid request is made.</li>
     *     <li>{@link NotFoundResponse} - Returns HTTP 404 when a resource is not found.</li>
     *     <li>{@link Exception} - Returns HTTP 500 for general internal server errors.</li>
     * </ul>
     *
     * @param app the {@link Javalin} application instance where the exception handlers will be registered
     */
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
