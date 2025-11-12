package com.cy.videoservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "video.storage")
public class VideoStorageProperties {

    /**
     * 本地存储目录，默认 videos
     */
    private String location = "videos";

    /**
     * 通过接口暴露时的前缀，例如 /api/video/media
     */
    private String mediaPrefix = "/api/video/media";
}
