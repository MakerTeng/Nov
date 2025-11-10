package com.cy.recommendservice.controller;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.common.model.ApiResponse;
import com.cy.common.security.SecurityConstants;
import com.cy.recommendservice.model.VideoInfo;
import com.cy.recommendservice.service.RecommendationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/list")
    public ApiResponse<List<VideoInfo>> recommend(@RequestParam(defaultValue = "10") int limit,
                                                  HttpServletRequest request) {
        Long userId = resolveUserId(request);
        return ApiResponse.success(recommendationService.recommend(userId, limit));
    }

    @GetMapping("/admin/{userId}")
    public ApiResponse<List<VideoInfo>> recommendForUser(@PathVariable Long userId, HttpServletRequest request,
                                                         @RequestParam(defaultValue = "10") int limit) {
        ensureAdmin(request);
        return ApiResponse.success(recommendationService.recommend(userId, limit));
    }

    private Long resolveUserId(HttpServletRequest request) {
        String userId = request.getHeader(SecurityConstants.HEADER_USER_ID);
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "please login first");
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
