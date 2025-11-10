package com.cy.logservice.service.impl;

import com.cy.common.config.RabbitMQConfig;
import com.cy.common.mq.LogEvent;
import com.cy.logservice.dto.LogCollectRequest;
import com.cy.logservice.entity.UserBehaviorLog;
import com.cy.logservice.mapper.UserBehaviorLogMapper;
import com.cy.logservice.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final UserBehaviorLogMapper logMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long collect(Long userId, LogCollectRequest request, String username) {
        UserBehaviorLog log = new UserBehaviorLog();
        log.setUserId(userId);
        log.setVideoId(request.getVideoId());
        log.setAction(request.getAction());
        log.setDetail(request.getDetail());
        log.setCreateTime(LocalDateTime.now());
        logMapper.insert(log);

        LogEvent event = LogEvent.builder()
                .userId(userId)
                .videoId(request.getVideoId())
                .action(request.getAction())
                .detail(request.getDetail())
                .timestamp(System.currentTimeMillis())
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.LOG_QUEUE, event);
        return log.getId();
    }

    @Override
    public List<UserBehaviorLog> listByUser(Long userId) {
        return logMapper.listByUser(userId);
    }

    @Override
    public List<UserBehaviorLog> listByVideo(Long videoId) {
        return logMapper.listByVideo(videoId);
    }
}
