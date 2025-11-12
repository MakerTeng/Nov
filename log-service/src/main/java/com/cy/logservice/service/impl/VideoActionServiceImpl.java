package com.cy.logservice.service.impl;

import com.cy.logservice.dto.VideoActionRequest;
import com.cy.logservice.entity.VideoAction;
import com.cy.logservice.mapper.VideoActionMapper;
import com.cy.logservice.service.VideoActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VideoActionServiceImpl implements VideoActionService {

    private final VideoActionMapper videoActionMapper;

    @Override
    public Long record(Long userId, VideoActionRequest request) {
        Long firstId = null;
        Integer watchTime = request.getWatchTime() == null ? 0 : request.getWatchTime();
        if (watchTime > 0) {
            firstId = saveAction(userId, request.getVideoId(), "view", watchTime);
        }
        if (Boolean.TRUE.equals(request.getLiked())) {
            Long id = saveAction(userId, request.getVideoId(), "like", 1);
            if (firstId == null) {
                firstId = id;
            }
        }
        if (Boolean.TRUE.equals(request.getCommented())) {
            Long id = saveAction(userId, request.getVideoId(), "comment", 1);
            if (firstId == null) {
                firstId = id;
            }
        }
        return firstId == null ? 0L : firstId;
    }

    private Long saveAction(Long userId, Long videoId, String action, int weight) {
        VideoAction entity = new VideoAction();
        entity.setUserId(userId);
        entity.setVideoId(videoId);
        entity.setAction(action);
        entity.setWeight(weight);
        entity.setCreateTime(LocalDateTime.now());
        videoActionMapper.insert(entity);
        return entity.getId();
    }
}
