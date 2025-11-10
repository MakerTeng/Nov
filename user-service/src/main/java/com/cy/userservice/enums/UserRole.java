package com.cy.userservice.enums;

import java.util.Arrays;

public enum UserRole {
    USER("USER"),
    CREATOR("CREATOR"),
    ADMIN("ADMIN");

    private final String code;

    UserRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static boolean isAdmin(String role) {
        return ADMIN.code.equalsIgnoreCase(role);
    }

    public static boolean contains(String role) {
        return Arrays.stream(values())
                .anyMatch(item -> item.code.equalsIgnoreCase(role));
    }
}
