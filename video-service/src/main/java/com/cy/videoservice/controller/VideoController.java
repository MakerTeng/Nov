package com.cy.videoservice.controller;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.common.model.ApiResponse;
import com.cy.common.security.SecurityConstants;
import com.cy.videoservice.dto.VideoBatchRequest;
import com.cy.videoservice.dto.VideoResponse;
import com.cy.videoservice.dto.VideoUploadRequest;
import com.cy.videoservice.service.VideoService;
import com.cy.videoservice.service.VideoStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final VideoStorageService videoStorageService;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> upload(@Valid @ModelAttribute VideoUploadRequest request, HttpServletRequest servletRequest) {
        Long userId = requireUserId(servletRequest);
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
    public ApiResponse<List<VideoResponse>> list(HttpServletRequest servletRequest,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "12") int size) {
        String role = servletRequest.getHeader(SecurityConstants.HEADER_USER_ROLE);
        boolean includeInactive = role != null && role.equalsIgnoreCase("ADMIN");
        return ApiResponse.success(videoService.listAll(includeInactive, page, size));
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

    @GetMapping("/media/**")
    public void media(HttpServletRequest request,
                      HttpServletResponse response) {
        String relativePath = extractPathFromPattern(request);
        if (!StringUtils.hasText(relativePath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            Resource resource = videoStorageService.load(relativePath);
            writeVideoStream(resource, request, response);
            videoService.recordPlayback(relativePath);
        } catch (BizException ex) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, HttpServletRequest servletRequest) {
        Long userId = requireUserId(servletRequest);
        String role = servletRequest.getHeader(SecurityConstants.HEADER_USER_ROLE);
        boolean isAdmin = role != null && role.equalsIgnoreCase("ADMIN");
        videoService.delete(id, userId, isAdmin);
        return ApiResponse.success();
    }

    private Long requireUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader(SecurityConstants.HEADER_USER_ID);
        if (userIdHeader == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "please login first");
        }
        return Long.parseLong(userIdHeader);
    }

    private void writeVideoStream(Resource resource, HttpServletRequest request, HttpServletResponse response) throws Exception {
        long fileLength = resource.contentLength();
        String rangeHeader = request.getHeader(HttpHeaders.RANGE);
        long start = 0;
        long end = fileLength - 1;
        if (StringUtils.hasText(rangeHeader) && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            start = parseLong(ranges[0], 0);
            if (ranges.length > 1 && StringUtils.hasText(ranges[1])) {
                end = parseLong(ranges[1], end);
            }
            end = Math.min(end, fileLength - 1);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }

        long contentLength = end - start + 1;
        MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
        response.setContentType(mediaType.toString());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline");
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        if (response.getStatus() == HttpServletResponse.SC_PARTIAL_CONTENT) {
            response.setHeader(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileLength));
        }

        StreamUtils.copyRange(resource.getInputStream(), response.getOutputStream(), start, end);
        response.flushBuffer();
    }

    private long parseLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private String extractPathFromPattern(HttpServletRequest request) {
        String pathWithinHandlerMapping = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (!StringUtils.hasText(pathWithinHandlerMapping) || !StringUtils.hasText(bestMatchPattern)) {
            return null;
        }
        return antPathMatcher.extractPathWithinPattern(bestMatchPattern, pathWithinHandlerMapping);
    }
}
