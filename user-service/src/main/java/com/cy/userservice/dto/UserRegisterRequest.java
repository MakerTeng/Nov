package com.cy.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @NotBlank(message = "用户名必填")
    @Size(min = 3, max = 32, message = "用户名长度需在 3-32 个字符之间")
    private String username;

    @NotBlank(message = "密码必填")
    @Size(min = 6, max = 64, message = "密码长度至少 6 位")
    private String password;

    private String email;

    /**
     * USER / CREATOR, default USER
     */
    private String role;
}
