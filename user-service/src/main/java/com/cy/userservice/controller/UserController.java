package com.cy.userservice.controller;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.common.model.ApiResponse;
import com.cy.common.security.SecurityConstants;
import com.cy.userservice.dto.MenuItem;
import com.cy.userservice.dto.UserLoginRequest;
import com.cy.userservice.dto.UserLoginResponse;
import com.cy.userservice.dto.UserProfileResponse;
import com.cy.userservice.dto.UserRegisterRequest;
import com.cy.userservice.enums.UserRole;
import com.cy.userservice.service.MenuService;
import com.cy.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MenuService menuService;

    @PostMapping("/register")
    public ApiResponse<Long> register(@Valid @RequestBody UserRegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> profile(HttpServletRequest request) {
        Long userId = resolveUserId(request);
        return ApiResponse.success(userService.profile(userId));
    }

    @GetMapping("/menu")
    public ApiResponse<List<MenuItem>> menu(HttpServletRequest request) {
        String role = resolveRole(request);
        return ApiResponse.success(menuService.buildMenus(role));
    }

    @GetMapping("/list")
    public ApiResponse<List<UserProfileResponse>> list(HttpServletRequest request) {
        String role = resolveRole(request);
        if (!UserRole.isAdmin(role)) {
            throw new BizException(ErrorCode.FORBIDDEN, "admin role required");
        }
        return ApiResponse.success(userService.listAll());
    }

    private Long resolveUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader(SecurityConstants.HEADER_USER_ID);
        if (userIdHeader == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "missing user information");
        }
        return Long.parseLong(userIdHeader);
    }

    private String resolveRole(HttpServletRequest request) {
        String role = request.getHeader(SecurityConstants.HEADER_USER_ROLE);
        if (role == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "missing role information");
        }
        return role;
    }
}
