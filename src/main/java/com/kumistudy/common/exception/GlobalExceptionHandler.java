package com.kumistudy.common.exception;

import com.kumistudy.common.api.ApiResponse;
import com.kumistudy.common.api.ErrorResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        List<ErrorResponse.FieldErrorResponse> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();

        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "輸入資料有誤", fieldErrors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        return response(HttpStatus.CONFLICT, "RESOURCE_CONFLICT", exception.getMessage(), List.of());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException exception) {
        return response(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", exception.getMessage(), List.of());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException exception) {
        return response(HttpStatus.FORBIDDEN, "ACCOUNT_UNAVAILABLE", exception.getMessage(), List.of());
    }

    private ResponseEntity<ApiResponse<Void>> response(
            HttpStatus status,
            String code,
            String message,
            List<ErrorResponse.FieldErrorResponse> fieldErrors) {
        ErrorResponse error = new ErrorResponse(code, message, fieldErrors);
        return ResponseEntity.status(status).body(ApiResponse.failure(error));
    }
}
