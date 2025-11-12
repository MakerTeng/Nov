package com.cy.videoservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoUploadRequest {

    @NotBlank(message = "标题必填")
    private String title;

    private String description;

    /**
     * 逗号分隔的标签
     */
    private String tags;

    /**
     * 视频时长，单位秒
     */
    private Integer durationSec;

    private String coverUrl;

    @NotNull(message = "请上传视频文件")
    private MultipartFile file;
}
