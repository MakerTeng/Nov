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
    private String tags;
    private Integer durationSec;
    private Long commentCount;
    private Long uploaderId;
    private String uploaderName;
    private Long likeCount;
    private Long playCount;
    private Integer status;
    private LocalDateTime createTime;
}
