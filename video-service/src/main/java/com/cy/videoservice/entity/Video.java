package com.cy.videoservice.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Video {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String coverUrl;
    private String tags;
    private Integer durationSec;
    private Long commentCount;
    private Long uploaderId;
    private String uploaderName;
    private Long likeCount;
    private Long playCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
