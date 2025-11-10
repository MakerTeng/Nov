package com.cy.recommendservice.model;

import lombok.Data;

@Data
public class VideoInfo {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String coverUrl;
    private Long uploaderId;
    private String uploaderName;
    private Long likeCount;
    private Long playCount;
    private Integer status;
}
