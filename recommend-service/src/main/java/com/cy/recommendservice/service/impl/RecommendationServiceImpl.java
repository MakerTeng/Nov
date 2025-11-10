package com.cy.recommendservice.service.impl;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.common.mq.LogEvent;
import com.cy.common.model.ApiResponse;
import com.cy.recommendservice.client.VideoClient;
import com.cy.recommendservice.model.VideoInfo;
import com.cy.recommendservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final VideoClient videoClient;

    private final Map<Long, Long> videoScores = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> userHistories = new ConcurrentHashMap<>();

    @Override
    public List<VideoInfo> recommend(Long userId, int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        Set<Long> watched = userHistories.getOrDefault(userId, Collections.emptySet());
        List<Long> topVideoIds = videoScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .filter(id -> !watched.contains(id))
                .limit(limit)
                .collect(Collectors.toList());

        List<VideoInfo> results = new ArrayList<>(fetchVideos(topVideoIds));
        if (results.size() < limit) {
            ApiResponse<List<VideoInfo>> allVideosResponse = videoClient.list();
            List<VideoInfo> allVideos = safeData(allVideosResponse);
            for (VideoInfo video : allVideos) {
                if (results.size() >= limit) {
                    break;
                }
                if (watched.contains(video.getId()) || containsVideo(results, video.getId())) {
                    continue;
                }
                results.add(video);
            }
        }
        return results;
    }

    @Override
    public void handleLogEvent(LogEvent event) {
        if (event == null || event.getVideoId() == null) {
            return;
        }
        long delta = scoreDelta(event.getAction());
        videoScores.merge(event.getVideoId(), delta, Long::sum);
        if (event.getUserId() != null) {
            userHistories
                    .computeIfAbsent(event.getUserId(), id -> ConcurrentHashMap.newKeySet())
                    .add(event.getVideoId());
        }
    }

    private List<VideoInfo> fetchVideos(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        VideoClient.BatchRequest request = new VideoClient.BatchRequest();
        request.setIds(ids);
        ApiResponse<List<VideoInfo>> response = videoClient.batch(request);
        List<VideoInfo> videos = safeData(response);
        Map<Long, VideoInfo> map = videos.stream().collect(Collectors.toMap(VideoInfo::getId, v -> v, (a, b) -> a));
        List<VideoInfo> ordered = new ArrayList<>();
        for (Long id : ids) {
            VideoInfo info = map.get(id);
            if (info != null) {
                ordered.add(info);
            }
        }
        return ordered;
    }

    private List<VideoInfo> safeData(ApiResponse<List<VideoInfo>> response) {
        if (response == null) {
            return List.of();
        }
        if (response.getCode() != ErrorCode.SUCCESS.getCode()) {
            throw new BizException(response.getCode(), response.getMessage());
        }
        return response.getData() == null ? List.of() : response.getData();
    }

    private long scoreDelta(String action) {
        if (action == null) {
            return 1;
        }
        return switch (action.toUpperCase()) {
            case "LIKE" -> 3;
            case "COMMENT" -> 2;
            default -> 1;
        };
    }

    private boolean containsVideo(List<VideoInfo> videos, Long videoId) {
        return videos.stream().anyMatch(video -> video.getId().equals(videoId));
    }
}
