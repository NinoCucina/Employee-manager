package com.vti.configuration.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private int code;
    private String message;
    private Object errors;
    private String moreInformation;

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.moreInformation = "http://localhost/api/v1/exceptions/" + code;
    }

    public ErrorResponse(int code, String message, Object errors) {
        this(code, message);
        this.errors = errors;
    }
}
