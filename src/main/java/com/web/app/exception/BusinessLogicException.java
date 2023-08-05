package com.web.app.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends IllegalAccessException {

    private final ExceptionCode exceptionCode;

    public BusinessLogicException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
