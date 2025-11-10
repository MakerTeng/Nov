package com.cy.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 32, message = "username length must be between 3 and 32 characters")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 64, message = "password must contain at least 6 characters")
    private String password;

    /**
     * USER / CREATOR, default USER
     */
    private String role;
}
