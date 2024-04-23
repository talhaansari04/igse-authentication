package com.igse.exception;

public class UserException extends RuntimeException {
    public UserException(Integer status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

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
}
