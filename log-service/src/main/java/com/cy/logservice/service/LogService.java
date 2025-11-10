package com.cy.logservice.service;

import com.cy.logservice.dto.LogCollectRequest;
import com.cy.logservice.entity.UserBehaviorLog;

import java.util.List;

public interface LogService {
    Long collect(Long userId, LogCollectRequest request, String username);

    List<UserBehaviorLog> listByUser(Long userId);

    List<UserBehaviorLog> listByVideo(Long videoId);
}
