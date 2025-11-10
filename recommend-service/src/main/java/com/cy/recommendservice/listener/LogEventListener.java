package com.cy.recommendservice.listener;

import com.cy.common.config.RabbitMQConfig;
import com.cy.common.mq.LogEvent;
import com.cy.recommendservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogEventListener {

    private final RecommendationService recommendationService;

    @RabbitListener(queues = RabbitMQConfig.LOG_QUEUE)
    public void onMessage(LogEvent event) {
        log.debug("received log event: {}", event);
        recommendationService.handleLogEvent(event);
    }
}
