package com.example.aicodereviewapi.exception;

import com.example.aicodereviewapi.dto.error.ApiErrorResponse;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        String message =
                ex.getBindingResult().getAllErrors().stream()
                        .map(err -> Optional.ofNullable(err.getDefaultMessage()).orElse("Validation failed"))
                        .findFirst()
                        .orElse("Validation failed");

        ApiErrorResponse error = new ApiErrorResponse(ErrorCode.VALIDATION_ERROR, message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ApiErrorResponse error = new ApiErrorResponse(ErrorCode.VALIDATION_ERROR, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(Exception ex) {
        ApiErrorResponse error = new ApiErrorResponse(ErrorCode.UNKNOWN_ERROR, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiErrorResponse> handleSecurityException(SecurityException ex) {
        ApiErrorResponse error = new ApiErrorResponse(ErrorCode.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        ApiErrorResponse error = new ApiErrorResponse(ErrorCode.FORBIDDEN, "Access denied");
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
            org.springframework.security.core.AuthenticationException ex) {
        ApiErrorResponse error = new ApiErrorResponse(ErrorCode.UNAUTHORIZED, "Authentication failed");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiErrorResponse error = new ApiErrorResponse(ErrorCode.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OwnershipException.class)
    public ResponseEntity<ApiErrorResponse> handleOwnershipException(OwnershipException ex) {
        ApiErrorResponse error = new ApiErrorResponse(ErrorCode.FORBIDDEN, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(
                new ApiErrorResponse(ErrorCode.INVALID_CREDENTIALS, ex.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(
                new ApiErrorResponse(ErrorCode.EMAIL_ALREADY_EXISTS, ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
