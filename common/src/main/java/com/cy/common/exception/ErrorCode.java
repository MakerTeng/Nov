package com.cy.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(0, "success"),
    USER_NOT_FOUND(1001, "user not found"),
    USERNAME_EXISTS(1002, "username already exists"),
    BAD_CREDENTIALS(1003, "bad credentials"),
    UNAUTHORIZED(1004, "unauthorized"),
    FORBIDDEN(1005, "forbidden"),
    DATA_NOT_FOUND(1006, "data not found"),
    VALIDATION_ERROR(1007, "validation failed"),
    BUSINESS_ERROR(1008, "business error"),
    INTERNAL_ERROR(2000, "internal error");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
