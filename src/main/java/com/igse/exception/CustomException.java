package com.igse.exception;

public class CustomException {

    private Integer status;
    private String message;

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

    @Override
    public String toString() {
        return "CustomException{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
