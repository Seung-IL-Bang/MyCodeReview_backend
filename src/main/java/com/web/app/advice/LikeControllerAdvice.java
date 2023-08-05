package com.web.app.advice;

import com.web.app.dto.ApiResponse;
import com.web.app.exception.BusinessLogicException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class LikeControllerAdvice {

    @ResponseStatus() // default: INTERNAL_SERVER_ERROR 500
    @ExceptionHandler(BusinessLogicException.class)
    public ApiResponse<Object> handleBusinessException(BusinessLogicException e) {

        int statusCode = e.getExceptionCode().getStatusCode();

        HttpStatus status = HttpStatus.resolve(statusCode);

        return ApiResponse.of(status, e.getMessage(), null);
    }

    @ResponseStatus()
    @ExceptionHandler(NoSuchElementException.class)
    public ApiResponse<Object> handleNoSuchElementException(NoSuchElementException e) {
        return ApiResponse.of(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

}
