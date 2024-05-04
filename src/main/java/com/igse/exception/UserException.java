package com.igse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserException extends RuntimeException {
    private Integer status;
    private String message;
}
