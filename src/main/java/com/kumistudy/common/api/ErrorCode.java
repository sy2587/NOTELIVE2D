package com.kumistudy.common.api;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "輸入資料有誤"),
    MALFORMED_JSON(HttpStatus.BAD_REQUEST, "JSON 格式不正確"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "請求參數不正確"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "帳號或密碼錯誤"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "請先登入"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "沒有權限執行此操作"),
    ACCOUNT_UNAVAILABLE(HttpStatus.FORBIDDEN, "帳號目前無法使用"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "找不到指定資源"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "不支援此請求方法"),
    RESOURCE_CONFLICT(HttpStatus.CONFLICT, "資料狀態衝突"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "伺服器發生錯誤");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() {
        return status;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
