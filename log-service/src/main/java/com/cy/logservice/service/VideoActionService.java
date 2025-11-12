package com.cy.logservice.service;

import com.cy.logservice.dto.VideoActionRequest;

public interface VideoActionService {

    Long record(Long userId, VideoActionRequest request);
}
