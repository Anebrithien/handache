package com.tsingye.common;

/**
 * Created by tsingye on 16-6-21.
 */
public class ErrorResponse {

    private int status;

    private String message;

    public ErrorResponse(String message) {
        this(-1, message);
    }

    public ErrorResponse(Exception e) {
        this(-1, e.getMessage());
    }

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(int status, Exception ex) {
        this.status = status;
        this.message = ex.getMessage();
    }

    public ErrorResponse(StatusCode statusCode) {
        this.status = statusCode.statusCode();
        this.message = statusCode.message();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
