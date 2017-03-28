package com.tsingye.common;

/**
 * Created by tsingye on 16-6-21.
 */
public class RestException extends Exception {

    private static final long serialVersionUID = 2229339457107100448L;

    protected StatusCode statusCode;

    public RestException() {
    }

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestException(StatusCode statusCode) {
        super(statusCode.message());
        this.statusCode = statusCode;
    }

    public RestException(StatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public RestException(StatusCode statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
