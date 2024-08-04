package database.controller;

import java.util.Objects;

public class ErrorResponse {
    private String status;
    private String error;
    private String message;

    public ErrorResponse(String status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

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
