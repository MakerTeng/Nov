package com.cy.videoservice.service.impl;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.videoservice.dto.VideoResponse;
import com.cy.videoservice.dto.VideoUploadRequest;
import com.cy.videoservice.entity.Video;
import com.cy.videoservice.mapper.VideoMapper;
import com.cy.videoservice.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoMapper videoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long upload(Long userId, String username, VideoUploadRequest request) {
        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setFileUrl(request.getFileUrl());
        video.setCoverUrl(request.getCoverUrl());
        video.setUploaderId(userId);
        video.setUploaderName(username);
        video.setLikeCount(0L);
        video.setPlayCount(0L);
        video.setStatus(1);
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
    public List<VideoResponse> listAll(boolean includeInactive) {
        List<Video> videos = includeInactive ? videoMapper.listAll() : videoMapper.listActive();
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

    private VideoResponse toResponse(Video video) {
        long likeCount = video.getLikeCount() == null ? 0 : video.getLikeCount();
        long playCount = video.getPlayCount() == null ? 0 : video.getPlayCount();
        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .fileUrl(video.getFileUrl())
                .coverUrl(video.getCoverUrl())
                .uploaderId(video.getUploaderId())
                .uploaderName(video.getUploaderName())
                .likeCount(likeCount)
                .playCount(playCount)
                .status(video.getStatus())
                .createTime(video.getCreateTime())
                .build();
    }
}
