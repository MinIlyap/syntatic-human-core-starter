package org.example.exception;

import jakarta.validation.ConstraintViolationException;
import org.example.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    private static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = buildValidationErrorMessage(ex);
        return buildErrorResponse(VALIDATION_ERROR, errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = buildConstraintViolationMessage(ex);
        return buildErrorResponse(VALIDATION_ERROR, errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse("RUNTIME_ERROR", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return buildErrorResponse(INTERNAL_ERROR, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String buildValidationErrorMessage(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));
    }

    private String buildConstraintViolationMessage(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s",
                        violation.getPropertyPath(),
                        violation.getMessage()))
                .collect(Collectors.joining("; "));
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String errorCode,
            String message,
            HttpStatus status
    ) {
        ErrorResponse error = new ErrorResponse(errorCode, message, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
}