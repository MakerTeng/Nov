package com.cy.recommendservice.service.impl;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import com.cy.common.mq.LogEvent;
import com.cy.common.model.ApiResponse;
import com.cy.recommendservice.client.VideoClient;
import com.cy.recommendservice.mapper.VideoActionMapper;
import com.cy.recommendservice.model.VideoActionAggregate;
import com.cy.recommendservice.model.VideoInfo;
import com.cy.recommendservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final VideoClient videoClient;
    private final VideoActionMapper videoActionMapper;

    @Override
    public List<VideoInfo> recommend(Long userId, int limit) {
        int fetchLimit = limit <= 0 ? 10 : limit;
        List<VideoActionAggregate> aggregates = videoActionMapper.aggregateByUser(userId, fetchLimit * 3, 600);
        List<Long> rankedIds = aggregates.stream()
                .sorted(Comparator.comparingDouble(this::score).reversed())
                .map(VideoActionAggregate::getVideoId)
                .limit(fetchLimit)
                .collect(Collectors.toList());

        List<VideoInfo> results = new ArrayList<>(fetchVideos(rankedIds));
        if (results.size() < fetchLimit) {
            int fallbackSize = Math.min(fetchLimit * 3, 200);
            ApiResponse<List<VideoInfo>> fallbackResponse = videoClient.list(1, fallbackSize);
            List<VideoInfo> allVideos = safeData(fallbackResponse);
            for (VideoInfo video : allVideos) {
                if (results.size() >= fetchLimit) {
                    break;
                }
                if (containsVideo(results, video.getId())) {
                    continue;
                }
                results.add(video);
            }
        }
        return results;
    }

    @Override
    public void handleLogEvent(LogEvent event) {
        // 按照新的权重算法改为直接读取数据库中的 video_actions，此处仅保留接口兼容
    }

    @Override
    public List<VideoInfo> topN(int limit) {
        int fetchLimit = limit <= 0 ? 10 : Math.min(limit, 50);
        int fetchSize = Math.min(fetchLimit * 4, 200);
        ApiResponse<List<VideoInfo>> response = videoClient.list(1, fetchSize);
        List<VideoInfo> videos = safeData(response);
        return videos.stream()
                .sorted(Comparator.comparingLong(this::trendScore).reversed())
                .limit(fetchLimit)
                .collect(Collectors.toList());
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

    private boolean containsVideo(List<VideoInfo> videos, Long videoId) {
        return videos.stream().anyMatch(video -> video.getId().equals(videoId));
    }

    private double score(VideoActionAggregate aggregate) {
        double watch = aggregate.getWatchScore() == null ? 0 : aggregate.getWatchScore();
        double liked = aggregate.getLikedScore() == null ? 0 : Math.min(aggregate.getLikedScore(), 1.0);
        double commented = aggregate.getCommentedScore() == null ? 0 : Math.min(aggregate.getCommentedScore(), 1.0);
        return 0.6 * clamp(watch) + 0.3 * clamp(liked) + 0.1 * clamp(commented);
    }

    private double clamp(double value) {
        if (value < 0) {
            return 0;
        }
        if (value > 1) {
            return 1;
        }
        return value;
    }

    private long trendScore(VideoInfo video) {
        long play = video.getPlayCount() == null ? 0 : video.getPlayCount();
        long like = video.getLikeCount() == null ? 0 : video.getLikeCount();
        long comment = video.getCommentCount() == null ? 0 : video.getCommentCount();
        return play + like * 3 + comment * 5;
    }
}
