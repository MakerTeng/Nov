package com.cy.logservice.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoAction {
    private Long id;
    private Long userId;
    private Long videoId;
    private String action;
    private Integer weight;
    private LocalDateTime createTime;
}
