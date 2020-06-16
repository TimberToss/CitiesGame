package com.example.speechrecognition.data.state;


public class Resource <T> {
    private DataStatus status;
    private T data;
    private String message;

    public DataStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    private Resource(T data, String message, DataStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public static final class Success<T> extends Resource<T> {
        public Success(T data) {
            super(data, null, DataStatus.SUCCESS);
        }
    }
    public static final class Loading<T> extends Resource<T>  {

        public Loading() {
            super(null, null, DataStatus.LOADING);
        }
    }
    public static final class Error<T> extends Resource<T>  {

        public Error(String message) {
            super(null, message, DataStatus.ERROR);
        }
    }

    public enum DataStatus {
        SUCCESS,
        ERROR,
        LOADING
    }
}
