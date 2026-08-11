package com.telehealth.config;

import com.telehealth.shared.exceptions.TeleHealthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TeleHealthException.EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(TeleHealthException.EntityNotFoundException ex) {
        return problemDetail(HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(TeleHealthException.BusinessRuleViolationException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRule(TeleHealthException.BusinessRuleViolationException ex) {
        return problemDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(TeleHealthException.ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(TeleHealthException.ConflictException ex) {
        return problemDetail(HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(TeleHealthException.UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorized(TeleHealthException.UnauthorizedException ex) {
        return problemDetail(HttpStatus.FORBIDDEN, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(TeleHealthException.ExternalServiceException.class)
    public ResponseEntity<ProblemDetail> handleExternalService(TeleHealthException.ExternalServiceException ex) {
        log.error("External service error: {}", ex.getMessage(), ex);
        return problemDetail(HttpStatus.BAD_GATEWAY, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        pd.setTitle("Validation Failed");
        pd.setType(URI.create("https://telehealth.com/errors/validation"));
        pd.setProperty("errorCode", "VALIDATION_ERROR");
        pd.setProperty("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return problemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<ProblemDetail> problemDetail(HttpStatus status, String errorCode, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setType(URI.create("https://telehealth.com/errors/" + errorCode.toLowerCase().replace('_', '-')));
        pd.setProperty("errorCode", errorCode);
        pd.setProperty("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(pd);
    }
}
