package com.kumistudy.common.api;

import java.util.List;

public record ErrorResponse(String code, String message, List<FieldErrorResponse> fieldErrors) {

    public record FieldErrorResponse(String field, String message) {
    }
}
