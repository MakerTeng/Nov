package com.cy.logservice.controller;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.common.model.ApiResponse;
import com.cy.common.security.SecurityConstants;
import com.cy.logservice.dto.LogCollectRequest;
import com.cy.logservice.entity.UserBehaviorLog;
import com.cy.logservice.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping("/collect")
    public ApiResponse<Long> collect(@Valid @RequestBody LogCollectRequest request, HttpServletRequest servletRequest) {
        Long userId = requireUser(servletRequest);
        String username = servletRequest.getHeader(SecurityConstants.HEADER_USER_NAME);
        Long logId = logService.collect(userId, request, username);
        return ApiResponse.success(logId);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserBehaviorLog>> byUser(@PathVariable Long userId, HttpServletRequest servletRequest) {
        ensureAdmin(servletRequest);
        return ApiResponse.success(logService.listByUser(userId));
    }

    @GetMapping("/video/{videoId}")
    public ApiResponse<List<UserBehaviorLog>> byVideo(@PathVariable Long videoId, HttpServletRequest servletRequest) {
        ensureAdmin(servletRequest);
        return ApiResponse.success(logService.listByVideo(videoId));
    }

    private Long requireUser(HttpServletRequest request) {
        String userId = request.getHeader(SecurityConstants.HEADER_USER_ID);
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "missing user information");
        }
        return Long.parseLong(userId);
    }

    private void ensureAdmin(HttpServletRequest request) {
        String role = request.getHeader(SecurityConstants.HEADER_USER_ROLE);
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new BizException(ErrorCode.FORBIDDEN, "admin role required");
        }
    }
}
