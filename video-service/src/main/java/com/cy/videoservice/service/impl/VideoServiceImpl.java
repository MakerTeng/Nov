package com.cy.videoservice.service.impl;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.videoservice.config.VideoStorageProperties;
import com.cy.videoservice.dto.VideoResponse;
import com.cy.videoservice.dto.VideoUploadRequest;
import com.cy.videoservice.entity.Video;
import com.cy.videoservice.mapper.VideoMapper;
import com.cy.videoservice.service.VideoService;
import com.cy.videoservice.service.VideoStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoMapper videoMapper;
    private final VideoStorageService videoStorageService;
    private final VideoStorageProperties storageProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long upload(Long userId, String username, VideoUploadRequest request) {
        String relativePath = videoStorageService.store(request.getFile());
        LocalDateTime now = LocalDateTime.now();

        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setFileUrl(relativePath);
        String cover = request.getCoverUrl();
        if (!StringUtils.hasText(cover)) {
            cover = videoStorageService.generateCover(relativePath);
        }
        video.setCoverUrl(cover);
        video.setTags(request.getTags());
        video.setDurationSec(request.getDurationSec());
        video.setCommentCount(0L);
        video.setUploaderId(userId);
        video.setUploaderName(username);
        video.setLikeCount(0L);
        video.setPlayCount(0L);
        video.setStatus(1);
        video.setCreateTime(now);
        video.setUpdateTime(now);
        videoMapper.insert(video);
        return video.getId();
    }

    @Override
    public VideoResponse findById(Long id, boolean increasePlay) {
        Video video = videoMapper.findById(id);
        if (video == null || video.getStatus() == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "video not found");
        }
        if (increasePlay) {
            videoMapper.increasePlay(id);
            long current = video.getPlayCount() == null ? 0 : video.getPlayCount();
            video.setPlayCount(current + 1);
        }
        return toResponse(video);
    }

    @Override
    public List<VideoResponse> listAll(boolean includeInactive, int page, int size) {
        int sizeLimit = Math.min(Math.max(size, 1), 200);
        int currentPage = Math.max(page, 1);
        int offset = (currentPage - 1) * sizeLimit;
        List<Video> videos = videoMapper.listPaged(includeInactive, offset, sizeLimit);
        return videos.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<VideoResponse> listMine(Long userId) {
        return videoMapper.listByUploader(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void like(Long id) {
        int updated = videoMapper.increaseLike(id);
        if (updated == 0) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "video not found");
        }
    }

    @Override
    public List<VideoResponse> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return videoMapper.findByIds(ids).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long operatorId, boolean operatorIsAdmin) {
        Video video = videoMapper.findById(id);
        if (video == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "video not found");
        }
        if (!operatorIsAdmin && (video.getUploaderId() == null || !video.getUploaderId().equals(operatorId))) {
            throw new BizException(ErrorCode.FORBIDDEN, "只能删除自己的视频");
        }
        int deleted = videoMapper.delete(id);
        if (deleted == 0) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "删除视频失败，请重试");
        }
        videoStorageService.delete(video.getFileUrl());
        if (StringUtils.hasText(video.getCoverUrl())) {
            videoStorageService.delete(video.getCoverUrl());
        }
    }

    @Override
    public void recordPlayback(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return;
        }
        videoMapper.increasePlayByFile(relativePath);
    }

    private VideoResponse toResponse(Video video) {
        long likeCount = video.getLikeCount() == null ? 0 : video.getLikeCount();
        long playCount = video.getPlayCount() == null ? 0 : video.getPlayCount();
        long commentCount = video.getCommentCount() == null ? 0 : video.getCommentCount();
        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .fileUrl(buildMediaUrl(video.getFileUrl()))
                .coverUrl(video.getCoverUrl())
                .tags(video.getTags())
                .durationSec(video.getDurationSec())
                .commentCount(commentCount)
                .uploaderId(video.getUploaderId())
                .uploaderName(video.getUploaderName())
                .likeCount(likeCount)
                .playCount(playCount)
                .status(video.getStatus())
                .createTime(video.getCreateTime())
                .build();
    }

    private String buildMediaUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return null;
        }
        String prefix = storageProperties.getMediaPrefix();
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix + relativePath;
    }
}
