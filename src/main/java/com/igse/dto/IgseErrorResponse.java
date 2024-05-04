package com.igse.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class IgseErrorResponse <T>{
    private Integer status;
    private T error;
}
