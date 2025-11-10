package com.cy.videoservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VideoResponse {
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
    private LocalDateTime createTime;
}
