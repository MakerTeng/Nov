package com.cy.recommendservice.service;

import com.cy.common.mq.LogEvent;
import com.cy.recommendservice.model.VideoInfo;

import java.util.List;

public interface RecommendationService {

    List<VideoInfo> recommend(Long userId, int limit);

    void handleLogEvent(LogEvent event);

    List<VideoInfo> topN(int limit);
}
