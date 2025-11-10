package com.cy.logservice.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBehaviorLog {
    private Long id;
    private Long userId;
    private Long videoId;
    private String action;
    private String detail;
    private LocalDateTime createTime;
}
