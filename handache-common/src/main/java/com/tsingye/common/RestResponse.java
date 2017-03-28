package com.tsingye.common;

/**
 * Created by tsingye on 16-6-21.
 */
public class RestResponse<T> {

    public static final RestResponse<?> OK = new RestResponse<>(0, "OK");

    public static final RestResponse<?> ERROR = new RestResponse<>(-1, "Error");

    private Integer status;

    private String message;

    private T result;

    public RestResponse(StatusCode statusCode) {
        this(statusCode.statusCode(), statusCode.message());
    }

    public RestResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public RestResponse(Integer status, String message, T result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public RestResponse<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public static <T> RestResponse<T> ok() {
        return new RestResponse<>(0, "OK");
    }

    public static <T> RestResponse<T> error() {
        return new RestResponse<>(-1, "Error");
    }

    public static <T> RestResponse<T> of(StatusCode statusCode) {
        return new RestResponse<>(statusCode);
    }
}
