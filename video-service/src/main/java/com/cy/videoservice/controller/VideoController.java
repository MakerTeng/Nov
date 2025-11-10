package com.cy.videoservice.controller;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.common.model.ApiResponse;
import com.cy.common.security.SecurityConstants;
import com.cy.videoservice.dto.VideoBatchRequest;
import com.cy.videoservice.dto.VideoResponse;
import com.cy.videoservice.dto.VideoUploadRequest;
import com.cy.videoservice.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/upload")
    public ApiResponse<Long> upload(@Valid @RequestBody VideoUploadRequest request, HttpServletRequest servletRequest) {
        Long userId = requireUserId(servletRequest);
        String role = servletRequest.getHeader(SecurityConstants.HEADER_USER_ROLE);
        if (role == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "missing role information");
        }
        if (!role.equalsIgnoreCase("CREATOR") && !role.equalsIgnoreCase("ADMIN")) {
            throw new BizException(ErrorCode.FORBIDDEN, "only creator or admin users can upload videos");
        }
        String username = servletRequest.getHeader(SecurityConstants.HEADER_USER_NAME);
        if (username == null) {
            username = "unknown";
        }
        return ApiResponse.success(videoService.upload(userId, username, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<VideoResponse> detail(@PathVariable Long id,
                                             @RequestParam(defaultValue = "true") boolean increasePlay) {
        return ApiResponse.success(videoService.findById(id, increasePlay));
    }

    @GetMapping("/list")
    public ApiResponse<List<VideoResponse>> list(HttpServletRequest servletRequest) {
        String role = servletRequest.getHeader(SecurityConstants.HEADER_USER_ROLE);
        boolean includeInactive = role != null && role.equalsIgnoreCase("ADMIN");
        return ApiResponse.success(videoService.listAll(includeInactive));
    }

    @GetMapping("/mylist")
    public ApiResponse<List<VideoResponse>> myVideos(HttpServletRequest servletRequest) {
        Long userId = requireUserId(servletRequest);
        return ApiResponse.success(videoService.listMine(userId));
    }

    @PostMapping("/{id}/like")
    public ApiResponse<Void> like(@PathVariable Long id, HttpServletRequest servletRequest) {
        requireUserId(servletRequest);
        videoService.like(id);
        return ApiResponse.success();
    }

    @PostMapping("/batch")
    public ApiResponse<List<VideoResponse>> batch(@Valid @RequestBody VideoBatchRequest request) {
        return ApiResponse.success(videoService.findByIds(request.getIds()));
    }

    private Long requireUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader(SecurityConstants.HEADER_USER_ID);
        if (userIdHeader == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "please login first");
        }
        return Long.parseLong(userIdHeader);
    }
}
