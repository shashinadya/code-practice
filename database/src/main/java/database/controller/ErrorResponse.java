package database.controller;

import java.util.Objects;

/**
 * The {@code ErrorResponse} class represents an error response that is sent back to the client
 * in case of an exception or error during request processing. It contains HTTP status code,
 * error description, and a detailed error message that can provide additional context for the failure.
 *
 * <p>This class is typically used in API error handling scenarios to return consistent error responses
 * to the client. It provides getter and setter methods for the response fields and also overrides
 * {@code equals()} and {@code hashCode()} for proper comparison and usage in collections.
 *
 * <p>Typical usage:
 * <pre>
 * {@code
 * ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", "The requested resource was not found");
 * }
 * </pre>
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class ErrorResponse {
    private int status;
    private String error;
    private String message;

    /**
     * Constructs an {@code ErrorResponse} with the provided status, error, and message.
     *
     * @param status  the HTTP status code of the error
     * @param error   a short description of the error
     * @param message a detailed error message providing additional context
     */
    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Gets the HTTP status code of the error response.
     *
     * @return the HTTP status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the HTTP status code of the error response.
     *
     * @param status the HTTP status code to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the short description of the error.
     *
     * @return the error description
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the short description of the error.
     *
     * @param error the error description to set
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Gets the detailed error message providing additional context.
     *
     * @return the detailed error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the detailed error message providing additional context.
     *
     * @param message the detailed error message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return Objects.equals(status, that.status) && Objects.equals(error, that.error) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, error, message);
    }
}
