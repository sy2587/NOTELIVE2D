package com.kumistudy.common.api;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        int status,
        List<FieldErrorResponse> fieldErrors,
        Instant timestamp,
        String path) {

    public ErrorResponse {
        fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
        return of(errorCode, message, List.of(), path);
    }

    public static ErrorResponse of(
            ErrorCode errorCode,
            String message,
            List<FieldErrorResponse> fieldErrors,
            String path) {
        return new ErrorResponse(
                errorCode.name(),
                message,
                errorCode.status().value(),
                fieldErrors,
                Instant.now(),
                path
        );
    }

    public record FieldErrorResponse(String field, String message) {
    }
}
