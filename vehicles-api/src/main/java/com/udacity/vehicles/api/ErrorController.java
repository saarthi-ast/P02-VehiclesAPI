package com.udacity.vehicles.api;

import com.udacity.vehicles.service.CarNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static com.udacity.vehicles.constants.ApplicationConstants.CAR_NOT_FOUND;

/**
 * Implements the Error controller related to any errors handled by the Vehicles API
 */
@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler{
private static final Logger log = LoggerFactory.getLogger(ErrorController.class);
    private static final String DEFAULT_VALIDATION_FAILED_MESSAGE = "Validation failed";
    private static final String DEFAULT_UNSUPPORTED_OP_MESSAGE = "Operation not supported";


    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleExceptions(
            RuntimeException ex, WebRequest request) {
        log.error("Error in Vehicle API - handleExceptions - "+ex.getMessage()+" \n Caused by: "+ex.getCause());
        String bodyOfResponse;
        if (ex instanceof CarNotFoundException) {
            bodyOfResponse = CAR_NOT_FOUND;
            return handleExceptionInternal(ex, bodyOfResponse,
                    new HttpHeaders(), HttpStatus.NOT_FOUND, request);
        } else {
            bodyOfResponse = "Operation not supported.";
            return handleExceptionInternal(ex, bodyOfResponse,
                    new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(
                        Collectors.toList());

        ApiError apiError = new ApiError(DEFAULT_VALIDATION_FAILED_MESSAGE, errors);
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
    }

}

